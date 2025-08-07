package com.github.bannirui.mms.client.consumer;

import com.github.bannirui.mms.common.BrokerType;
import com.github.bannirui.mms.common.MmsConst;
import com.github.bannirui.mms.common.MmsException;
import com.github.bannirui.mms.logger.MmsLogger;
import com.github.bannirui.mms.metadata.ConsumerGroupMetadata;
import com.github.bannirui.mms.zookeeper.MmsZkClient;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConsumerFactory {

    private static Map<String, MmsConsumerProxy> consumers = new ConcurrentHashMap<>();

    public static final Logger logger = MmsLogger.log;

    private ConsumerFactory() {
    }

    public static MmsConsumerProxy getConsumer(ConsumerGroup consumerGroup, Properties properties, MessageListener listener) {
        return doGetConsumer(consumerGroup.getConsumerGroup(), consumerGroup.getConsumerName(), consumerGroup.getTags(), properties, listener);
    }

    private static MmsConsumerProxy doGetConsumer(String consumerGroup, String name, Set<String> tags, Properties properties, MessageListener listener) {
        String cacheName = consumerGroup + "_" + name;
        if (consumers.get(cacheName) == null) {
            synchronized (ConsumerFactory.class) {
                if (consumers.get(cacheName) == null) {
                    MmsConsumerProxy consumer;
                    ConsumerGroupMetadata metadata;
                    try {
                        metadata = MmsZkClient.getInstance().readConsumerGroupMetadata(consumerGroup);
                    } catch (Throwable ex) {
                        logger.error("get consumer metadata error", ex);
                        throw MmsException.METAINFO_EXCEPTION;
                    }
                    logger.info("Consumer created {}", metadata.toString());
                    boolean isOrderly = false;
                    if (properties.containsKey(MmsConst.CLIENT_CONFIG.CONSUME_ORDERLY)) {
                        isOrderly = Boolean.parseBoolean(properties.getProperty(MmsConst.CLIENT_CONFIG.CONSUME_ORDERLY));
                    }
                    if (BrokerType.ROCKETMQ.equals(metadata.getClusterMetadata().getBrokerType())) {
                        consumer = new RocketmqConsumerProxy(metadata, isOrderly, name, tags, properties, listener);
                    } else {
                        consumer = new KafkaConsumerProxy(metadata, isOrderly, name, properties, listener);
                    }
                    consumers.putIfAbsent(cacheName, consumer);
                    return consumer;
                }
            }
        }
        return consumers.get(cacheName);
    }

    public synchronized static void shutdown() {
        for (Map.Entry<String, MmsConsumerProxy> entry : consumers.entrySet()) {
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

    public static Collection<MmsConsumerProxy> getConsumers() {
        return consumers.values();
    }
}

