package com.github.bannirui.mms.client.producer;

import com.github.bannirui.mms.common.BrokerType;
import com.github.bannirui.mms.common.MmsConst;
import com.github.bannirui.mms.common.MmsException;
import com.github.bannirui.mms.logger.MmsLogger;
import com.github.bannirui.mms.metadata.TopicMetadata;
import com.github.bannirui.mms.zookeeper.MmsZkClient;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;

public class ProducerFactory {

    public static final Logger logger = MmsLogger.log;

    private final static Map<String, MmsProducerProxy> topicProducers = new ConcurrentHashMap<>();

    private ProducerFactory() {
    }

    public static MmsProducerProxy getProducer(String topic) {
        return doGetProducer(topic, MmsConst.DEFAULT_PRODUCER);
    }

    public static MmsProducerProxy getProducer(String topic, Properties properties) {
        if (properties == null || properties.isEmpty()) {
            return getProducer(topic);
        }
        return doGetProducer(topic, MmsConst.DEFAULT_PRODUCER, properties);
    }

    public synchronized static void shutdown() {
        for (Map.Entry<String, MmsProducerProxy> entry : topicProducers.entrySet()) {
            entry.getValue().shutdown();
        }
        topicProducers.clear();
        logger.info("ProducerFactory has been shutdown");
    }

    public synchronized static void shutdown(String topic) {
        String key = topic + "_" + MmsConst.DEFAULT_PRODUCER;
        if (topicProducers.containsKey(key)) {
            topicProducers.get(key).shutdown();
        }
        logger.info("Producer of " + topic + " has been shutdown");
    }

    static void recycle(String name, String instanceName) {
        String key = name + "_" + instanceName;
        topicProducers.remove(key);
        logger.info("producer {} has been remove", key);
    }

    public static Collection<MmsProducerProxy> getProducers() {
        return topicProducers.values();
    }

    private static MmsProducerProxy doGetProducer(String topic, String name, Properties properties) {
        String cacheName = topic + "_" + name;
        if (topicProducers.get(cacheName) == null) {
            synchronized (ProducerFactory.class) {
                if (topicProducers.get(cacheName) == null) {
                    MmsProducerProxy producer;
                    TopicMetadata metadata;
                    try {
                        metadata = MmsZkClient.getInstance().readTopicMetadata(topic);
                    } catch (Exception ex) {
                        logger.error("read topic {} metadata error", topic, ex);
                        throw MmsException.METAINFO_EXCEPTION;
                    }
                    logger.info("Producer create: topic metadata is {}", metadata.toString());
                    if (BrokerType.ROCKETMQ.equals(metadata.getClusterMetadata().getBrokerType())) {
                        producer = new RocketmqProducerProxy(metadata, false, name, properties);
                    } else {
                        producer = new KafkaProducerProxy(metadata, false, name, properties);
                    }
                    topicProducers.putIfAbsent(cacheName, producer);
                    return producer;
                }
            }
        }
        return topicProducers.get(cacheName);
    }

    private static MmsProducerProxy doGetProducer(String topic, String name) {
        String cacheName = topic + "_" + name;
        if (topicProducers.get(cacheName) == null) {
            synchronized (topicProducers) {
                if (topicProducers.get(cacheName) == null) {
                    MmsProducerProxy producer = null;
                    TopicMetadata metadata = null;
                    try {
                        metadata = MmsZkClient.getInstance().readTopicMetadata(topic);
                    } catch (Exception ex) {
                        logger.error("read topic {} metadata error", topic, ex);
                        throw MmsException.METAINFO_EXCEPTION;
                    }
                    logger.info("Producer create: topic metadata is {}", metadata.toString());
                    if (BrokerType.ROCKETMQ.equals(metadata.getClusterMetadata().getBrokerType())) {
                        producer = new RocketmqProducerProxy(metadata, false, name);
                    } else {
                        producer = new KafkaProducerProxy(metadata, false, name);
                    }
                    topicProducers.putIfAbsent(cacheName, producer);
                    return producer;
                }
            }
        }
        return topicProducers.get(cacheName);
    }
}


