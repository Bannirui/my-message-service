package com.github.bannirui.mms.client.producer;

import com.github.bannirui.mms.common.HostServerType;
import com.github.bannirui.mms.common.MmsConst;
import com.github.bannirui.mms.common.MmsException;
import com.github.bannirui.mms.logger.MmsLogger;
import com.github.bannirui.mms.metadata.TopicMetadata;
import com.github.bannirui.mms.zookeeper.MmsZkClient;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class ProducerFactory {

    public static final Logger logger = MmsLogger.log;

    /**
     * key=topic_name
     * name是给生产者的名字 默认用{@link MmsConst#DEFAULT_PRODUCER}
     */
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
                    if (HostServerType.ROCKETMQ.equals(metadata.getClusterMetadata().getBrokerType())) {
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

    /**
     * @param topic topic的名字
     * @param name  生产者的名字
     */
    private static MmsProducerProxy doGetProducer(String topic, String name) {
        String cacheName = topic + "_" + name;
        if (Objects.isNull(topicProducers.get(cacheName))) {
            synchronized (topicProducers) {
                if (Objects.isNull(topicProducers.get(cacheName))) {
                    MmsProducerProxy producer = null;
                    TopicMetadata metadata = null;
                    try {
                        metadata = MmsZkClient.getInstance().readTopicMetadata(topic);
                    } catch (Exception ex) {
                        logger.error("read topic {} metadata error", topic, ex);
                        throw MmsException.METAINFO_EXCEPTION;
                    }
                    logger.info("Producer create: topic metadata is {}", metadata.toString());
                    if (Objects.equals(HostServerType.ROCKETMQ.getCode(), metadata.getClusterMetadata().getBrokerType())) {
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


