package com.github.bannirui.mms.service.manager.kafka;

import com.github.bannirui.mms.metadata.ClusterMetadata;
import com.github.bannirui.mms.service.manager.AbstractMessageMiddlewareProcessor;
import com.github.bannirui.mms.zookeeper.MmsZkClient;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Properties;

public class KafkaMiddlewareManager extends AbstractMessageMiddlewareProcessor {
    private static final Logger logger = LoggerFactory.getLogger(KafkaMiddlewareManager.class);

    private AdminClient adminClient;

    public KafkaMiddlewareManager(MmsZkClient zkClient, ClusterMetadata clusterMetadata) {
        this(zkClient, clusterMetadata, null);
    }

    public KafkaMiddlewareManager(MmsZkClient zkClient, ClusterMetadata clusterMetadata, RollBack rollback) {
        super(zkClient, clusterMetadata, rollback);
    }

    @Override
    protected void create() {
        Properties properties = new Properties();
        properties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, super.clusterMetadata.getBootAddr());
        this.adminClient = AdminClient.create(properties);
    }

    @Override
    protected void destroy() {
        if (Objects.nonNull(this.adminClient)) {
            this.adminClient.close();
            this.adminClient = null;
        }
        if (Objects.nonNull(super.rollback)) {
            super.rollback.destroy();
        }
    }

    @Override
    public void createTopic(String topicName, int partitions, Integer replication) {
        // todo
    }

    @Override
    public boolean existTopic(String topicName) {
        // todo
        return false;
    }

    @Override
    public void updateTopic(String topicName, int partitions) {
        // todo
    }

    @Override
    public void createConsumerGroup(String consumerGroup, Boolean broadcast, Boolean consumerFromMin) {
        // kafka不需要申请消费组
    }
}
