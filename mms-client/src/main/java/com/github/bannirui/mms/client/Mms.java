package com.github.bannirui.mms.client;

import com.github.bannirui.mms.client.common.MmsMessage;
import com.github.bannirui.mms.client.common.SimpleMessage;
import com.github.bannirui.mms.client.consumer.ConsumerFactory;
import com.github.bannirui.mms.client.consumer.ConsumerGroup;
import com.github.bannirui.mms.client.consumer.MessageListener;
import com.github.bannirui.mms.client.metrics.MmsStatsReporter;
import com.github.bannirui.mms.client.producer.Producer;
import com.github.bannirui.mms.client.producer.ProducerFactory;
import com.github.bannirui.mms.client.producer.SendCallback;
import com.github.bannirui.mms.client.producer.SendResult;
import com.github.bannirui.mms.common.MmsConst;
import com.github.bannirui.mms.logger.MmsLogger;
import com.github.bannirui.mms.zookeeper.MmsZkClient;
import com.google.common.collect.Sets;
import org.slf4j.Logger;

import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class Mms implements LifeCycle {

    public static final Logger logger = MmsLogger.log;

    private final MmsStatsReporter reporter;

    private static final Mms instance = new Mms();

    protected volatile boolean running;

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    private Mms() {
        logger.info("mms version {} initialized for {}", MmsConst.MMS_VERSION, MmsConst.MMS_IP);
        running = true;
        reporter = new MmsStatsReporter();
        reporter.start(10, TimeUnit.SECONDS);
        logger.info("mms initialized");
    }

    @Override
    public void start() {
    }

    @Override
    public void shutdown() {
        reporter.shutdown();
        ProducerFactory.shutdown();
        ConsumerFactory.shutdown();
        try {
            MmsZkClient.getInstance().close();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        running = false;
        logger.info("mms has been shutdown");
    }

    /**
     * 关闭指定Topic的Producer
     */
    public static void stopProducer(String topic) {
        ProducerFactory.shutdown(topic);
    }

    /**
     * 关闭指定消费组的Consumer
     */
    public static void stopConsumer(String consumerGroup) {
        ConsumerFactory.shutdown(consumerGroup);
    }

    public static void stop() {
        instance.shutdown();
    }

    public static void subscribe(String consumerGroup, MessageListener listener) {
        instance.doSubscribe(consumerGroup, listener);
    }

    public static void subscribe(String consumerGroup, Set<String> tags, MessageListener listener, Properties properties) {
        instance.doSubscribe(consumerGroup, tags, listener, properties);
    }

    public static void subscribe(String consumerGroup, MessageListener listener, Properties properties) {
        instance.doSubscribe(consumerGroup, Sets.newHashSet(), listener, properties);
    }

    public static void subscribe(String consumerGroup, Set<String> tags, MessageListener listener) {
        instance.doSubscribe(consumerGroup, tags, listener);
    }

    public static void subscribe(String consumerGroup, String tag, MessageListener listener) {
        instance.doSubscribe(consumerGroup, Sets.newHashSet(tag), listener);
    }

    public static SendResult send(String topic, SimpleMessage simpleMessage) {
        return instance.doSendSync(topic, simpleMessage, null);
    }

    public static void sendAsync(String topic, SimpleMessage simpleMessage, SendCallback callBack) {
        instance.doSendAsync(topic, simpleMessage, null, callBack);
    }

    public static void sendOneway(String topic, SimpleMessage simpleMessage) {
        instance.doSendOneway(topic, simpleMessage);
    }

    public static SendResult send(String topic, SimpleMessage simpleMessage, Properties properties) {
        return instance.doSendSync(topic, simpleMessage, properties);
    }

    public static void sendAsync(String topic, SimpleMessage simpleMessage, Properties properties, SendCallback callBack) {
        instance.doSendAsync(topic, simpleMessage, properties, callBack);
    }

    private void doSubscribe(String consumerGroup, MessageListener listener) {
        if (!running) {
            logger.error("MMS is not running,will not consume message");
            return;
        }
        ConsumerFactory.getConsumer(new ConsumerGroup(consumerGroup), new Properties(), listener);
    }

    private void doSubscribe(String consumerGroup, Set<String> tags, MessageListener listener) {
        if (!running) {
            logger.error("MMS is not running,will not consume message");
            return;
        }
        ConsumerFactory.getConsumer(new ConsumerGroup(consumerGroup, MmsConst.DEFAULT_CONSUMER, tags), new Properties(), listener);
    }

    private void doSubscribe(String consumerGroup, Set<String> tags, MessageListener listener, Properties properties) {
        if (!running) {
            logger.error("MMS is not running,will not consume message");
            return;
        }
        ConsumerFactory.getConsumer(new ConsumerGroup(consumerGroup, MmsConst.DEFAULT_CONSUMER, tags), properties, listener);
    }

    private void doSendOneway(String topic, SimpleMessage simpleMessage) {
        if (!running) {
            logger.warn("MMS is not running,will not send message");
            return;
        }
        Producer producer = ProducerFactory.getProducer(topic);
        producer.oneway(new MmsMessage(simpleMessage));
    }

    private SendResult doSendSync(String topic, SimpleMessage simpleMessage, Properties properties) {
        if (!running) {
            logger.warn("MMS is not running,will not send message");
            return SendResult.buildErrorResult("MMS is not running");
        }
        Producer producer = ProducerFactory.getProducer(topic, properties);
        return producer.syncSend(new MmsMessage(simpleMessage));
    }

    private void doSendAsync(String topic, SimpleMessage simpleMessage, Properties properties, SendCallback callBack) {
        if (!running) {
            logger.warn("MMS is not running,will not send message");
            return;
        }
        Producer producer = ProducerFactory.getProducer(topic, properties);
        producer.asyncSend(new MmsMessage(simpleMessage), callBack);
    }
}

