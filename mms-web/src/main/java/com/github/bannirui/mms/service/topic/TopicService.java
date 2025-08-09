package com.github.bannirui.mms.service.topic;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.github.bannirui.mms.common.BrokerType;
import com.github.bannirui.mms.common.MmsServerStatusEnum;
import com.github.bannirui.mms.common.ResourceStatus;
import com.github.bannirui.mms.common.ZkRegister;
import com.github.bannirui.mms.dal.mapper.ServerMapper;
import com.github.bannirui.mms.dal.mapper.TopicEnvServerMapper;
import com.github.bannirui.mms.dal.mapper.TopicMapper;
import com.github.bannirui.mms.dal.model.Server;
import com.github.bannirui.mms.dal.model.Topic;
import com.github.bannirui.mms.dal.model.TopicEnvServer;
import com.github.bannirui.mms.dal.model.TopicEnvServerRef;
import com.github.bannirui.mms.dto.topic.MmsTopicConfigInfo;
import com.github.bannirui.mms.req.ApplyTopicReq;
import com.github.bannirui.mms.req.ApproveTopicReq;
import com.github.bannirui.mms.service.manager.MessageAdminManagerAdapt;
import com.github.bannirui.mms.service.manager.MiddlewareProcess;
import com.github.bannirui.mms.service.manager.MmsContextManager;
import com.github.bannirui.mms.util.Assert;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TopicService {

    private static final Logger logger = LoggerFactory.getLogger(TopicService.class);

    @Autowired
    private TopicMapper topicMapper;
    @Autowired
    private ServerMapper serverMapper;
    @Autowired
    private TopicEnvServerMapper topicEnvServerMapper;

    @Autowired
    private MessageAdminManagerAdapt messageAdminManagerAdapt;
    @Autowired
    ZkRegister zkRegister;

    /**
     * 申请topic
     *
     * @return 新增的topic数量
     */
    @Transactional(rollbackFor = Exception.class)
    public int addTopic(ApplyTopicReq req, String operator) {
        //  topic唯一
        boolean isUnique = this.uniqueTopicCheck(req.getName());
        Assert.that(isUnique, "topic名称重复");
        Topic topic = new Topic();
        topic.setUserId(req.getUserId());
        topic.setName(req.getName());
        topic.setClusterType(req.getClusterType());
        topic.setAppId(req.getAppId());
        topic.setTps(req.getTps());
        topic.setMsgSz(req.getMsgSz());
        // 申请
        topic.setStatus(ResourceStatus.CREATE_NEW.getCode());
        int count = topicMapper.insert(topic);
        // 集群是由管理员审批时候分配的
        req.getEnvs().forEach(env -> this.installTopicEnvRef(operator, topic, null, env.getEnvId()));
        return count;
    }

    /**
     * topic 集群 环境映射
     *
     * @param envId {@link com.github.bannirui.mms.dal.model.Env#id}
     */
    private TopicEnvServer installTopicEnvRef(String operator, Topic topic, Long clusterServerId, Long envId) {
        TopicEnvServer tes = new TopicEnvServer();
        tes.setTopicId(topic.getId());
        tes.setServerId(clusterServerId);
        tes.setEnvId(envId);
        this.topicEnvServerMapper.insert(tes);
        return tes;
    }

    /**
     * 审批topic
     *
     * @param topicId  要审核的topic
     * @param userName 谁审核的
     * @return 初始化失败的环境
     */
    public List<Long> approveTopic(Long topicId, ApproveTopicReq req, String userName) {
        // 要审批的topic
        Topic topic = this.topicMapper.selectById(topicId);
        // 准备分配的集群 集群是依附环境选择出来的
        List<ApproveTopicReq.TopicEnvServerInfo> candidateServers = req.getEnvServers();
        List<Long> candidateServerIds = candidateServers.stream().map(ApproveTopicReq.TopicEnvServerInfo::getServerId).toList();
        // 可以分配的集群
        List<Server> admitServers = checkCandidateServer(candidateServerIds);
        // 即使多个环境 每个topic也只能是一种mq的类型 kafka or rocket
        long clusterTypeCnt = admitServers.stream().map(Server::getType).distinct().count();
        Assert.that(Objects.equals(clusterTypeCnt, 1L), "topic在不同环境也得选择同一种集群类型");
        // 每个环境分配的集群
        Map<Long, Server> admitServerGroupById = admitServers.stream().collect(Collectors.toMap(Server::getId, x -> x));
        // 当前topic的配置信息 key=envId 可能当前环境下已经分配好了一个集群 现在需要更新换成其他集群
        Map<Long, TopicEnvServerRef> curTopicInfoGroup8Env = this.topicEnvServerMapper.getByTopicId(topic.getId())
                .stream()
                .collect(Collectors.toMap(TopicEnvServerRef::getEnvId, x -> x));
        this.topicMapper.updateById(new Topic() {{
            setId(topicId);
            setPartitions(req.getPartitions());
            setReplication(req.getReplication());
        }});
        List<Long> initFailEnv = new ArrayList<>();
        // 初始化主题资源
        for (ApproveTopicReq.TopicEnvServerInfo env : candidateServers) {
            // 申请的环境
            Long envId = env.getEnvId();
            // 为这个环境分配的集群
            Long serverId = env.getServerId();
            Server server = admitServerGroupById.get(serverId);
            // 这个环境下可能已经存在了一个集群 可能是为环境新增的集群 可能是为环境换别的集群
            Long curServerId = null;
            if (curTopicInfoGroup8Env.containsKey(serverId)) {
                curServerId = curTopicInfoGroup8Env.get(envId).getServerId();
            }
            try {
                if (!initTopicResource(envId, topic, server, curServerId, req.getPartitions())) {
                    continue;
                }
                // 更新当前环境下挂着的集群
                this.topicEnvServerMapper.updateById(
                        new TopicEnvServer() {{
                            setId(curTopicInfoGroup8Env.get(envId).getTesId());
                            setServerId(serverId);
                        }});
            } catch (Exception e) {
                logger.error("The current environmental subject approval failed:{}", envId, e);
                initFailEnv.add(envId);
            }
        }
        // 全部都初始化失败了拦截 部分成功部分失败的不管了
        Assert.that(initFailEnv.size() < candidateServers.size(), "主题审批失败");
        // topic的状态推进
        this.topicMapper.updateById(
                new Topic() {{
                    setId(topicId);
                    setStatus(ResourceStatus.CREATE_APPROVED.getCode());
                }}
        );
        return initFailEnv;
    }

    // topic name不能重复
    private boolean uniqueTopicCheck(String topicName) {
        return new LambdaQueryChainWrapper<>(topicMapper)
                .apply("LOWER(name) = {0}", topicName.toLowerCase())
                .count() == 0;
    }

    /**
     * @param envId         {@link com.github.bannirui.mms.dal.model.Env#id}
     * @param server        topic的集群 这个集群可能是首次分配给这个topic 也可能topic有了一个集群现在给换成别的集群
     * @param curServerId   topic当前已有的集群 可能是空的 可能是有值的
     * @param newPartitions 更新集群的分区数
     * @return 初始化topic成功与否
     */
    private boolean initTopicResource(Long envId, Topic topic, Server server, Long curServerId, Integer newPartitions) {
        MmsContextManager.setEnv(envId);
        String clusterName = server.getName();
        String topicName = topic.getName();
        MmsTopicConfigInfo topicConfigInfo = this.buildTopicConfigInfo(topic, server);
        // 给主题首次分配集群or给主题换集群
        if (Objects.equals(ResourceStatus.CREATE_NEW.getCode(), topic.getStatus())
                || !Objects.equals(server.getId(), curServerId)) {
            this.createTopic(topicConfigInfo);
            this.zkRegister.registerTopic2Zk(clusterName, topicName, BrokerType.getByCode(server.getType()));
        } else if (!Objects.equals(topic.getPartitions(), newPartitions)) {
            // 仅仅更新分区数 不需要注册到远程配置中心
            this.updateTopic(topicConfigInfo);
        } else {
            return false;
        }
        return true;
    }

    /**
     * 准备审批释放的集群 先校验是否符合要求
     *
     * @param candidateServerIds 准备审批的集群
     */
    private List<Server> checkCandidateServer(List<Long> candidateServerIds) {
        // 审核集群的信息
        List<Server> servers = this.serverMapper.selectBatchIds(candidateServerIds)
                .stream()
                .filter(x -> Objects.equals(MmsServerStatusEnum.ENABLE.getCode(), x.getStatus()))
                .toList();
        Assert.that(Objects.equals(servers.size(), candidateServerIds.size()), "无效的集群");
        return servers;
    }

    /**
     * topic信息配置成集群维度
     */
    private MmsTopicConfigInfo buildTopicConfigInfo(Topic topic, Server clusterServer) {
        MmsTopicConfigInfo ret = new MmsTopicConfigInfo();
        ret.setClusterName(clusterServer.getName());
        Map<String, Integer> partitions = new HashMap<>();
        partitions.put(topic.getName(), topic.getPartitions());
        ret.setPartitions(partitions);
        Map<String, Integer> replication = new HashMap<>();
        replication.put(topic.getName(), topic.getReplication());
        ret.setReplications(replication);
        return ret;
    }

    /**
     * 创建topic
     */
    private void createTopic(MmsTopicConfigInfo req) {
        logger.info("create topic info {}", req.toString());
        MiddlewareProcess middlewareProcess = this.messageAdminManagerAdapt.getOrCreateAdmin(req.getClusterName());
        // key is topic name
        Map<String, Integer> partitions = req.getPartitions();
        if (MapUtils.isNotEmpty(partitions)) {
            Map<String, Integer> replications = req.getReplications();
            for (Map.Entry<String, Integer> partition : partitions.entrySet()) {
                String topicName = partition.getKey();
                int replication = replications.get(topicName);
                // 向broker发起创建topic的请求
                middlewareProcess.createTopic(topicName, partition.getValue(), replication);
            }
        }
    }

    /**
     * 更新topic
     */
    public void updateTopic(MmsTopicConfigInfo req) {
        MiddlewareProcess middlewareProcess = messageAdminManagerAdapt.getOrCreateAdmin(req.getClusterName());
        Map<String, Integer> partitions = req.getPartitions();
        if (MapUtils.isEmpty(partitions)) return;
        Map<String, Integer> replications = req.getReplications();
        for (Map.Entry<String, Integer> partition : partitions.entrySet()) {
            String topicName = partition.getKey();
            int partitionCnt = partition.getValue();
            int replicationCnt = replications.get(topicName);
            if (middlewareProcess.existTopic(topicName)) {
                logger.info("Update the topic {} in cluster {}", topicName, req.getClusterName());
                middlewareProcess.updateTopic(topicName, partitionCnt);
            } else {
                logger.info(" Create the topic {} in cluster {}", topicName, req.getClusterName());
                middlewareProcess.createTopic(topicName, partitionCnt, replicationCnt);
            }
        }
    }
}
