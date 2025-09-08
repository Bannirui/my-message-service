package com.github.bannirui.mms.service.env;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.bannirui.mms.common.ResourceStatus;
import com.github.bannirui.mms.common.ZkRegister;
import com.github.bannirui.mms.dal.mapper.EnvMapper;
import com.github.bannirui.mms.dal.model.Env;
import com.github.bannirui.mms.dal.model.EnvHostServerExt;
import com.github.bannirui.mms.dal.model.MqMetaDataExt;
import com.github.bannirui.mms.service.manager.MmsContextManager;
import com.github.bannirui.mms.service.manager.ZkDatasourceManagerAdapt;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class EnvDatasourceService {

    private static final Logger logger = LoggerFactory.getLogger(EnvDatasourceService.class);

    @Autowired
    private EnvMapper envMapper;
    @Autowired
    private ZkDatasourceManagerAdapt zkDatasourceManagerAdapt;
    @Autowired
    private ZkRegister zkRegister;

    @PostConstruct
    public void init() {
        List<Env> envs = this.envMapper.selectList(new LambdaQueryWrapper<Env>()
                .select(Env::getId)
                .in(Env::getStatus, ResourceStatus.CREATE_APPROVED.getCode(), ResourceStatus.UPDATE_APPROVED.getCode(), ResourceStatus.ENABLE.getCode()));
        if (CollectionUtils.isEmpty(envs)) {
            logger.info("No enabled envs found");
        }
        logger.info("Found {} enabled envs", envs.size());
        for (Env env : envs) {
            if (!this.reloadEnvZkClient(env.getId())) {
                // zk连接没初始化成功 抹掉环境的zk 后置化到mms-web使用的时候校验
                logger.info("zk连接加载失败 抹掉环境{}的zk信息", env.getId());
                this.envMapper.updateById(new Env() {{
                    setId(env.getId());
                    setZkId(0L);
                }});
            } else {
                // zk没问题 把最新的mq数据注册到zk上
                MmsContextManager.setEnv(env.getId());
                List<MqMetaDataExt> mqMetaDataExts = this.envMapper.mqMetaDataByEnvId(env.getId());
                if (CollectionUtils.isNotEmpty(mqMetaDataExts)) {
                    List<MqMetaDataExt> activeClusters = mqMetaDataExts.stream().filter(x -> ((x.getClusterStatus() & ResourceStatus.ENABLE_MASK)) != 0).toList();
                    if (CollectionUtils.isNotEmpty(activeClusters)) {
                        activeClusters.forEach(x -> {
                            logger.info("刷新zk注册信息 cluster={}", x.getClusterName());
                            zkRegister.registerCluster2Zk(x.getClusterName(), x.getClusterHost() + ":" + x.getClusterPort(), x.getClusterType());
                        });
                        Map<String, MqMetaDataExt> topicMap = activeClusters.stream().filter(x->StringUtils.isNotEmpty(x.getTopicName()))
                                .collect(Collectors.toMap(
                                        MqMetaDataExt::getTopicName,
                                        Function.identity(),
                                        (oldValue, newValue) -> newValue));
                        if (MapUtils.isNotEmpty(topicMap)) {
                            topicMap.forEach((k, v) -> {
                                logger.info("刷新zk注册信息 topic={}", k);
                                zkRegister.registerTopic2Zk(v.getClusterName(), k, v.getClusterType());
                            });
                        }
                        Map<String, MqMetaDataExt> consumerMap = activeClusters.stream().filter(x->StringUtils.isNotEmpty(x.getConsumerName()))
                                .collect(Collectors.toMap(
                                        MqMetaDataExt::getConsumerName,
                                        Function.identity(),
                                        (oldValue, newValue) -> newValue));
                        if (MapUtils.isNotEmpty(consumerMap)) {
                            topicMap.forEach((k, v) -> {
                                logger.info("刷新zk注册信息 consumer={}", k);
                                zkRegister.registerConsumer2Zk(v.getClusterName(), v.getClusterType(), k, v.getTopicName(), Objects.equals(1, v.getConsumerBroadcast()), Objects.equals(1, v.getConsumerFromMin()));
                            });
                        }
                    }
                }
            }
        }
    }

    /**
     * 每个环境都分配了zk作为元数据注册中心
     * 初始化好连接放内存 后面直接根据环境标识操作zk{@link com.github.bannirui.mms.service.manager.MmsContextManager#setEnv}
     *
     * @param envId 哪个环境
     */
    public boolean reloadEnvZkClient(Long envId) {
        EnvHostServerExt env = this.envMapper.selectByEnvId(envId);
        String host = null;
        Integer port = null;
        if (Objects.isNull(env) || StringUtils.isEmpty(host = env.getZkHost())
                || Objects.isNull(port = env.getZkPort()) || port <= 0) {
            logger.warn("环境{}拿到的zk配置不合法{}", envId, env);
            return false;
        }
        // todo zk集群
        String zkUrl = host + ":" + port;
        logger.info("reload zk {} for env {}", zkUrl, env.getEnvName());
        this.zkDatasourceManagerAdapt.reload(envId, zkUrl);
        return true;
    }
}
