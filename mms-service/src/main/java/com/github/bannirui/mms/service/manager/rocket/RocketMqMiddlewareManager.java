package com.github.bannirui.mms.service.manager.rocket;

import com.github.bannirui.mms.common.MmsException;
import com.github.bannirui.mms.metadata.ClusterMetadata;
import com.github.bannirui.mms.service.manager.AbstractMessageMiddlewareProcessor;
import com.github.bannirui.mms.service.mq.rocket.MQAdminExtImpl;
import com.github.bannirui.mms.util.Assert;
import com.github.bannirui.mms.zookeeper.MmsZkClient;
import org.apache.commons.collections.CollectionUtils;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.TopicConfig;
import org.apache.rocketmq.common.constant.PermName;
import org.apache.rocketmq.remoting.protocol.body.ClusterInfo;
import org.apache.rocketmq.remoting.protocol.body.SubscriptionGroupWrapper;
import org.apache.rocketmq.remoting.protocol.route.TopicRouteData;
import org.apache.rocketmq.remoting.protocol.subscription.SubscriptionGroupConfig;
import org.apache.rocketmq.tools.admin.DefaultMQAdminExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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
    public void createTopic(String topicName, int partitions, Integer replication) {
        TopicConfig topicConfig = new TopicConfig();
        topicConfig.setTopicName(topicName);
        topicConfig.setWriteQueueNums(partitions);
        topicConfig.setReadQueueNums(partitions);
        topicConfig.setPerm(PermName.PERM_READ | PermName.PERM_WRITE);
        List<String> failedServers = new ArrayList<>();
        try {
            ClusterInfo clusterInfo = this.defaultMQAdminExt.examineBrokerClusterInfo();
            Set<String> brokerNames = clusterInfo.getClusterAddrTable().get(clusterName);
            Assert.that(CollectionUtils.isNotEmpty(brokerNames), clusterName + ", brokerNames is empty, can not create topic");
            for (String brokerName : brokerNames) {
                this.defaultMQAdminExt.createAndUpdateTopicConfig(clusterInfo.getBrokerAddrTable().get(brokerName).selectBrokerAddr(), topicConfig);
                logger.info("topic {} created to {}", topicConfig.getTopicName(), clusterInfo.getBrokerAddrTable().get(brokerName).selectBrokerAddr());
            }
            TopicRouteData topicRouteData;
            Thread.sleep(1_000L);
            try {
                topicRouteData = this.defaultMQAdminExt.examineTopicRouteInfo(topicName);
            } catch (Throwable ex) {
                logger.warn("examine topic failed", ex);
                Thread.sleep(3_000L);
                topicRouteData = this.defaultMQAdminExt.examineTopicRouteInfo(topicName);
            }
            List<String> createdBrokers = topicRouteData.getBrokerDatas().stream().map(t -> {
                logger.info("{} created in {}", topicConfig.getTopicName(), t.selectBrokerAddr());
                return t.getBrokerName();
            }).toList();
            for (String brokerName : brokerNames) {
                if (!createdBrokers.contains(brokerName)) {
                    failedServers.add(brokerName);
                }
            }
        } catch (Exception err) {
            logger.error("create topic error", err);
            throw new MmsException(err.getMessage());
        }
        if (!CollectionUtils.isEmpty(failedServers)) {
            logger.error("Failed to create topic resource,:{},cluster:{}", failedServers, clusterName);
            throw new MmsException("Failed to create topic resource");
        }
    }

    public DefaultMQAdminExt getAdmin() {
        return this.defaultMQAdminExt;
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
        SubscriptionGroupConfig subscriptionGroupConfig = new SubscriptionGroupConfig();
        subscriptionGroupConfig.setGroupName(consumerGroup);
        subscriptionGroupConfig.setConsumeBroadcastEnable(broadcast);
        subscriptionGroupConfig.setConsumeFromMinEnable(consumerFromMin);
        List<String> failedServers = new ArrayList<>();
        try {
            ClusterInfo clusterInfo = defaultMQAdminExt.examineBrokerClusterInfo();
            Set<String> brokerNames = clusterInfo.getClusterAddrTable().get(clusterName);
            for (String brokerName : brokerNames) {
                String brokerAddr = clusterInfo.getBrokerAddrTable().get(brokerName).selectBrokerAddr();
                defaultMQAdminExt.createAndUpdateSubscriptionGroupConfig(brokerAddr, subscriptionGroupConfig);
                logger.info("consumerGroup {} created to {}", consumerGroup, clusterInfo.getBrokerAddrTable().get(brokerName).selectBrokerAddr());
            }
            // 等待创建subscriptionGroup的异常操作完成
            Thread.sleep(1_000L);
            for (String brokerName : brokerNames) {
                String brokerAddr = clusterInfo.getBrokerAddrTable().get(brokerName).selectBrokerAddr();
                SubscriptionGroupWrapper allSubscriptionGroup = defaultMQAdminExt.getAllSubscriptionGroup(brokerAddr, 1_000L);
                if (!allSubscriptionGroup.getSubscriptionGroupTable().containsKey(consumerGroup)) {
                    failedServers.add(clusterInfo.getBrokerAddrTable().get(brokerName).selectBrokerAddr());
                } else {
                    logger.info("{} created for server {}", consumerGroup, brokerAddr);
                }
            }
        } catch (Exception e) {
            logger.error("create or update consumer {} error", consumerGroup, e);
            throw new MmsException(e.getMessage(), 9999);
        }
        if (!CollectionUtils.isEmpty(failedServers)) {
            logger.error("create or update consumer {} error:{}", consumerGroup, failedServers);
            throw new MmsException("create consumerGroup in these brokers failed, brokerLst=" + failedServers.toString(), 9999);
        }
    }
}
