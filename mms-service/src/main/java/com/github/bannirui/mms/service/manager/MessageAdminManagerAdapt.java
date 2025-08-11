package com.github.bannirui.mms.service.manager;

import com.github.bannirui.mms.common.BrokerType;
import com.github.bannirui.mms.common.MmsException;
import com.github.bannirui.mms.metadata.ClusterMetadata;
import com.github.bannirui.mms.service.manager.kafka.KafkaMiddlewareManager;
import com.github.bannirui.mms.service.manager.rocket.RocketMqMiddlewareManager;
import com.github.bannirui.mms.service.router.ZkRouter;
import com.github.bannirui.mms.util.Assert;
import com.github.bannirui.mms.zookeeper.MmsZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MessageAdminManagerAdapt {

    private static final Logger logger = LoggerFactory.getLogger(MessageAdminManagerAdapt.class);

    private final Map<String, AbstractMessageMiddlewareProcessor> implMap = new ConcurrentHashMap<>();

    @Autowired
    ZkRouter zkRouter;

    /**
     * @param clusterName mq集群名
     */
    public MiddlewareProcess getOrCreateAdmin(String clusterName) {
        String genKey = this.generateKey(clusterName);
        if (!this.implMap.containsKey(genKey)) {
            synchronized (this.implMap) {
                if (!this.implMap.containsKey(genKey)) {
                    this.implMap.put(genKey, this.createAdmin(clusterName));
                }
            }
        }
        return this.implMap.get(genKey);
    }

    private String generateKey(String clusterName) {
        Long envId = MmsContextManager.getEnv();
        return Objects.isNull(envId) ? "&" + clusterName : envId + "&" + clusterName;
    }

    /**
     * @param clusterName mq集群名
     */
    private AbstractMessageMiddlewareProcessor createAdmin(String clusterName) {
        MmsZkClient zkClient = this.zkRouter.currentZkClient();
        // 从zk中拿到集群配置
        ClusterMetadata clusterMetadata = zkClient.readClusterMetadata(clusterName);
        Assert.that(Objects.nonNull(clusterMetadata), "Cluster metadata not found for " + clusterName);
        Long env = MmsContextManager.getEnv();
        AbstractMessageMiddlewareProcessor middlewareProcess;
        AbstractMessageMiddlewareProcessor.RollBack rollBack = () -> {
            MmsContextManager.setEnv(env);
            rm(clusterName);
        };
        // 集群类型
        BrokerType brokerType = BrokerType.getByCode(clusterMetadata.getBrokerType());
        switch (brokerType) {
            case KAFKA -> middlewareProcess = new KafkaMiddlewareManager(zkClient, clusterMetadata, rollBack);
            case ROCKETMQ -> middlewareProcess = new RocketMqMiddlewareManager(zkClient, clusterMetadata, rollBack);
            default -> throw new MmsException("Illegal cluster type: " + brokerType + " for " + clusterName);
        }
        return middlewareProcess;
    }

    public void rm(String clusterName) {
        String genKey = this.generateKey(clusterName);
        synchronized (this.implMap) {
            if (this.implMap.containsKey(genKey)) {
                this.implMap.remove(genKey).destroy();
            }
        }
    }
}
