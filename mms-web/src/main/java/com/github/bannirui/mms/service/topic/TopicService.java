package com.github.bannirui.mms.service.topic;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.github.bannirui.mms.common.ResourceStatus;
import com.github.bannirui.mms.dal.mapper.TopicEnvironmentRefMapper;
import com.github.bannirui.mms.dal.mapper.TopicMapper;
import com.github.bannirui.mms.dal.model.Topic;
import com.github.bannirui.mms.dal.model.TopicEnvironmentRef;
import com.github.bannirui.mms.dto.topic.TopicDTO;
import com.github.bannirui.mms.service.domain.topic.TopicEnvironmentInfoVo;
import com.github.bannirui.mms.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class TopicService {

    private static final Logger logger = LoggerFactory.getLogger(TopicService.class);

    @Autowired
    private TopicMapper topicMapper;
    @Autowired
    private TopicEnvironmentRefMapper topicEnvironmentRefMapper;

    /**
     * 申请topic
     */
    @Transactional(rollbackFor = Exception.class)
    public int addTopic(TopicDTO topicDto, String operator) {
        boolean isUnique = this.uniqueTopicCheck(topicDto.getName());
        Assert.that(isUnique, "主题名称重复");
        Topic topic = new Topic();
        topic.setName(topicDto.getName());
        topic.setStatus(ResourceStatus.CREATE_NEW.getStatus());
        topic.setCreateDate(LocalDateTime.now());
        int count = topicMapper.insert(topic);
        topicDto.getEnvironments().forEach(env -> {
            this.installTopicEnvRef(operator, topic, null, env.getEnvironmentId());
        });
        return count;
    }

    private TopicEnvironmentRef installTopicEnvRef(String operator, Topic topic, Integer clusterServiceId, int envId) {
        TopicEnvironmentRef entity = new TopicEnvironmentRef();
        entity.setTopicId(topic.getId());
        entity.setEnvironmentId(envId);
        this.topicEnvironmentRefMapper.insert(entity);
        return entity;
    }

    /**
     * 审核topic
     *
     * @param topicDto 要审核的topic
     * @param userName 谁审核的
     */
    public List<Integer> approveTopic(TopicDTO topicDto, String userName) {
        Topic topic = topicMapper.selectById(topicDto.getId());
        List<TopicEnvironmentInfoVo> environments = topicDto.getEnvironments();
        List<ZmsServiceEntity> serviceList = listZmsServiceEntities(environmentInfoVos);
        long countCluster = serviceList.stream().map(ZmsServiceEntity::getServerType).distinct().count();
        Assert.that(countCluster == 1, "主题不同环境只能选择同一类型的集群");
        Map<Integer, ZmsServiceEntity> serviceIdNameMap = serviceList.stream().collect(Collectors.toMap(ZmsServiceEntity::getId, item -> item));
        //本地集群配置
        Map<Integer, TopicEnvironmentRef> localEnvIdServiceIdMap = environmentRefMapper.listByTopicId(Lists.newArrayList(topicDto.getId())).stream()
                .collect(Collectors.toMap(TopicEnvironmentRef::getEnvironmentId, item -> item));

        topicMapper.updateById(topicDto.getId(), topicDto.getPartitions(), topicDto.getReplication());
        List<Integer> failEnv = Lists.newArrayList();
        //初始化主题资源
        for (TopicEnvironmentInfoVo environmentInfoVo : environmentInfoVos) {
            Integer envId = environmentInfoVo.getEnvironmentId();
            Integer serviceId = environmentInfoVo.getServiceId();
            Integer localClusterId = localEnvIdServiceIdMap.get(envId).getServiceId();
            ZmsServiceEntity service = serviceIdNameMap.get(serviceId);
            try {
                boolean updated = initTopicResource(envId, topicDto.getPartitions(), topicDto.getReplication(), topic, localClusterId, service);
                if (!updated) {
                    continue;
                }
                // 当前环境集群改变
                environmentRefMapper.updateCluster(localEnvIdServiceIdMap.get(envId).getId(), service.getId(), userName);
            } catch (Exception e) {
                logger.error("The current environmental subject approval failed:{}", envId, e);
                failEnv.add(envId);
            }
        }
        Assert.that(failEnv.size() < environmentInfoVos.size(), "主题审批失败");
        topicMapper.updateById(topicDto.getId(), ResourceStatus.CREATE_APPROVED.getStatus());
        return failEnv;
    }

    // topic name不能重复
    private boolean uniqueTopicCheck(String topicName) {
        return new LambdaQueryChainWrapper<>(topicMapper)
                .apply("LOWER(name) = {0}", topicName.toLowerCase())
                .count() == 0;
    }

    private boolean initTopicResource(Integer envId, Integer partitions, Integer replication, Topic topic, Integer localClusterServiceId, MmsServiceEntity service) {
        ZmsContextManager.setEnv(envId);
        String clusterName = service.getServerName();
        String topicName = topic.getName();

        ZmsTopicConfigInfo zmsTopicConfigInfo = assembleZmsTopicConfigInfo(partitions, replication, clusterName, topicName);
        //新增或修改主题集群
        if (Objects.equals(ResourceStatus.CREATE_NEW.getStatus(), topic.getStatus())
                || !Objects.equals(service.getId(), localClusterServiceId)) {
            topicService.createTopic(zmsTopicConfigInfo);
            zkRegister.registerTopicToZk(clusterName, topicName, BrokerType.parse(service.getServerType()));
        } else if (!topic.getPartitions().equals(partitions)) {
            //partition变化不更改ZMS_ZK
            topicService.updateTopic(zmsTopicConfigInfo);
        } else {
            return false;
        }
        return true;
    }
}
