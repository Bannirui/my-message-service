package com.github.bannirui.mms.client.consumer;

import com.github.bannirui.mms.common.HostServerType;
import com.github.bannirui.mms.common.MmsConst;
import com.github.bannirui.mms.common.MmsException;
import com.github.bannirui.mms.logger.MmsLogger;
import com.github.bannirui.mms.metadata.ConsumerGroupMetadata;
import com.github.bannirui.mms.zookeeper.MmsZkClient;
import org.slf4j.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ConsumerFactory {

    /**
     * 缓存消费者代理对象
     * <ul>
     *     <li>key mq的consumerGroup+消费者name</li>
     *     <li>val 消费者代理对象</li>
     * </ul>
     */
    private static Map<String, ConsumerProxy> consumers = new ConcurrentHashMap<>();

    public static final Logger logger = MmsLogger.log;

    private ConsumerFactory() {
    }

    public static ConsumerProxy getConsumer(ConsumerGroup consumerGroup, Properties properties, MessageListener listener) {
        return doGetConsumer(consumerGroup.getConsumerGroup(), consumerGroup.getConsumerName(), consumerGroup.getTags(), properties, listener);
    }

    private static ConsumerProxy doGetConsumer(String consumerGroup, String name, Set<String> tags, Properties properties, MessageListener listener) {
        String cacheName = consumerGroup + "_" + name;
        if (!consumers.containsKey(cacheName)) {
            synchronized (ConsumerFactory.class) {
                if (!consumers.containsKey(cacheName)) {
                    ConsumerProxy consumerProxy;
                    ConsumerGroupMetadata metadata;
                    try {
                        // 从zk注册中心拿mq的consumer信息
                        metadata = MmsZkClient.getInstance().readConsumerGroupMetadata(consumerGroup);
                    } catch (Throwable ex) {
                        logger.error("从zk注册中心读mq消费组失败", ex);
                        throw MmsException.METAINFO_EXCEPTION;
                    }
                    logger.info("为{}消费组从注册中心拿到的mq元数据是{}", consumerGroup, metadata);
                    boolean isOrderly = false;
                    if (properties.containsKey(MmsConst.CLIENT_CONFIG.CONSUME_ORDERLY)) {
                        isOrderly = Boolean.parseBoolean(properties.getProperty(MmsConst.CLIENT_CONFIG.CONSUME_ORDERLY));
                    }
                    if (Objects.equals(HostServerType.ROCKETMQ.getCode(), metadata.getClusterMetadata().getBrokerType())) {
                        consumerProxy = new RocketmqConsumerProxy(metadata, isOrderly, name, tags, properties, listener);
                    } else if (Objects.equals(HostServerType.KAFKA.getCode(), metadata.getClusterMetadata().getBrokerType())) {
                        consumerProxy = new KafkaConsumerProxy(metadata, isOrderly, name, properties, listener);
                    } else {
                        throw new MmsException("从注册中心拿到的mq类型未知 没办法创建对应消费者");
                    }
                    logger.info("为{}消费组创建的消费者代理对象是{}", consumerGroup, consumerProxy);
                    consumers.putIfAbsent(cacheName, consumerProxy);
                    return consumerProxy;
                }
            }
        }
        return consumers.get(cacheName);
    }

    public synchronized static void shutdown() {
        for (Map.Entry<String, ConsumerProxy> entry : consumers.entrySet()) {
            entry.getValue().shutdown();
        }
        consumers.clear();
        logger.info("ConsumerFactory shutdown");
    }

    public synchronized static void shutdown(String consumerGroup) {
        String key = consumerGroup + "_" + MmsConst.DEFAULT_CONSUMER;
        if (consumers.containsKey(key)) {
            consumers.get(key).shutdown();
        }
        logger.info("ConsumerFactory shutdown");
    }

    static void recycle(String name, String instanceName) {
        String key = name + "_" + instanceName;
        consumers.remove(key);
        logger.info("Consumer {} removed", key);
    }

    public static Collection<ConsumerProxy> getConsumers() {
        return consumers.values();
    }
}

