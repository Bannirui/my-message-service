package com.github.bannirui.mms.service.manager.rocket;

import com.github.bannirui.mms.common.MmsException;
import com.github.bannirui.mms.metadata.ClusterMetadata;
import com.github.bannirui.mms.service.manager.AbstractMessageMiddlewareProcessor;
import com.github.bannirui.mms.service.mq.rocket.MQAdminExtImpl;
import com.github.bannirui.mms.zookeeper.MmsZkClient;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.tools.admin.DefaultMQAdminExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class RocketMqMiddlewareManager extends AbstractMessageMiddlewareProcessor {

    private static final Logger logger = LoggerFactory.getLogger(RocketMqMiddlewareManager.class);

    private DefaultMQAdminExt defaultMQAdminExt;

    public RocketMqMiddlewareManager(MmsZkClient zkClient, ClusterMetadata clusterMetadata) {
        this(zkClient, clusterMetadata, null);
    }

    public RocketMqMiddlewareManager(MmsZkClient zkClient, ClusterMetadata clusterMetadata, RollBack rollBack) {
        super(zkClient, clusterMetadata, rollBack);
    }

    @Override
    protected void create() {
        try {
            this.defaultMQAdminExt = new MQAdminExtImpl();
            this.defaultMQAdminExt.setNamesrvAddr(super.clusterMetadata.getBootAddr());
            this.defaultMQAdminExt.setInstanceName(Long.toString(System.currentTimeMillis()));
            this.defaultMQAdminExt.setVipChannelEnabled(false);
            this.defaultMQAdminExt.start();
        } catch (MQClientException e) {
            logger.error("Failed to create MQAdminExtImpl: {}", super.clusterMetadata, e);
            throw new MmsException(e.getErrorMessage(), 9999);
        }

    }

    @Override
    protected void destroy() {
        if (Objects.nonNull(this.defaultMQAdminExt)) {
            this.defaultMQAdminExt.shutdown();
            this.defaultMQAdminExt = null;
        }
        if (Objects.nonNull(super.rollback)) {
            super.rollback.destroy();
        }
    }

    @Override
    public void createTopic(String topic, int partitions, Integer replication) {
        // todo
    }

    public DefaultMQAdminExt getAdmin() {
        return this.defaultMQAdminExt;
    }
}
