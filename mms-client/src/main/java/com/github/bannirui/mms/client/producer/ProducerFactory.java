package com.github.bannirui.mms.client.producer;

import com.github.bannirui.mms.common.HostServerType;
import com.github.bannirui.mms.common.MmsConst;
import com.github.bannirui.mms.common.MmsException;
import com.github.bannirui.mms.logger.MmsLogger;
import com.github.bannirui.mms.metadata.TopicMetadata;
import com.github.bannirui.mms.zookeeper.MmsZkClient;
import org.apache.commons.collections.MapUtils;
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
    private final static Map<String, ProducerProxy> topicProducers = new ConcurrentHashMap<>();

    private ProducerFactory() {
    }

    public static ProducerProxy getProducer(String topic) {
        return doGetProducer(topic, MmsConst.DEFAULT_PRODUCER);
    }

    public static ProducerProxy getProducer(String topic, Properties properties) {
        logger.info("开始为主题{}创建生产者代理对象", topic);
        if (MapUtils.isEmpty(properties)) {
            return getProducer(topic);
        }
        return doGetProducer(topic, MmsConst.DEFAULT_PRODUCER, properties);
    }

    public synchronized static void shutdown() {
        for (Map.Entry<String, ProducerProxy> entry : topicProducers.entrySet()) {
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
        logger.info("{}的生产者成功停止", topic);
    }

    static void recycle(String name, String instanceName) {
        String key = name + "_" + instanceName;
        topicProducers.remove(key);
        logger.info("producer {} has been remove", key);
    }

    public static Collection<ProducerProxy> getProducers() {
        return topicProducers.values();
    }

    /**
     * 尝试从缓存中取代理对象 没有就从注册中心根据topic拿到mq的元数据生成代理对象缓存起来
     *
     * @param topic      topic的名字
     * @param name       生产者的名字
     * @param properties 生产者的配置
     * @return 生产者的代理对象
     */
    private static ProducerProxy doGetProducer(String topic, String name, Properties properties) {
        String cacheName = topic + "_" + name;
        if (topicProducers.get(cacheName) == null) {
            synchronized (ProducerFactory.class) {
                if (topicProducers.get(cacheName) == null) {
                    ProducerProxy producer;
                    TopicMetadata metadata;
                    try {
                        metadata = MmsZkClient.getInstance().readTopicMetadata(topic);
                    } catch (Exception ex) {
                        logger.error("为{}读取topic元数据失败", topic, ex);
                        throw MmsException.METAINFO_EXCEPTION;
                    }
                    logger.info("读到的topic元数据是{}", metadata);
                    if (Objects.equals(HostServerType.ROCKETMQ.getCode(), metadata.getClusterMetadata().getBrokerType())) {
                        producer = new RocketmqProducerProxy(metadata, false, name, properties);
                    } else if (Objects.equals(HostServerType.KAFKA.getCode(), metadata.getClusterMetadata().getBrokerType())) {
                        producer = new KafkaProducerProxy(metadata, false, name, properties);
                    } else {
                        throw new MmsException("mq类型未知");
                    }
                    topicProducers.putIfAbsent(cacheName, producer);
                }
            }
        }
        return topicProducers.get(cacheName);
    }

    /**
     * 从缓存{@link ProducerFactory#topicProducers}中拿生产者代理对象
     * 拿不到就尝试从注册中心取mq元数据生成生产者代理对象
     *
     * @param topic topic的名字
     * @param name  生产者的名字
     */
    private static ProducerProxy doGetProducer(String topic, String name) {
        String cacheName = topic + "_" + name;
        if (Objects.isNull(topicProducers.get(cacheName))) {
            synchronized (topicProducers) {
                if (Objects.isNull(topicProducers.get(cacheName))) {
                    ProducerProxy producer = null;
                    TopicMetadata metadata = null;
                    try {
                        metadata = MmsZkClient.getInstance().readTopicMetadata(topic);
                    } catch (Exception ex) {
                        logger.error("从注册中心读{}这个topic元数据失败", topic, ex);
                        throw MmsException.METAINFO_EXCEPTION;
                    }
                    logger.info("开始创建生产者代理对象 生产者元数据{}", metadata);
                    if (Objects.equals(HostServerType.ROCKETMQ.getCode(), metadata.getClusterMetadata().getBrokerType())) {
                        producer = new RocketmqProducerProxy(metadata, false, name);
                    } else if (Objects.equals(HostServerType.KAFKA.getCode(), metadata.getClusterMetadata().getBrokerType())) {
                        producer = new KafkaProducerProxy(metadata, false, name);
                    } else {
                        throw new MmsException("mq类型未知");
                    }
                    topicProducers.putIfAbsent(cacheName, producer);
                    return producer;
                }
            }
        }
        return topicProducers.get(cacheName);
    }
}


