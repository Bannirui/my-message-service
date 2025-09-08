package com.github.bannirui.mms.client;

import com.github.bannirui.mms.client.common.MmsMessage;
import com.github.bannirui.mms.client.common.SimpleMessage;
import com.github.bannirui.mms.client.consumer.ConsumerFactory;
import com.github.bannirui.mms.client.consumer.ConsumerGroup;
import com.github.bannirui.mms.client.consumer.MessageListener;
import com.github.bannirui.mms.client.metrics.MmsStatsReporter;
import com.github.bannirui.mms.client.producer.*;
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

    /**
     * 标识mms服务的运行状态
     */
    protected volatile boolean running;

    public boolean isRunning() {
        return this.running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    private Mms() {
        logger.info("本机{}开始初始化mss服务 版本是{}", MmsConst.MMS_IP, MmsConst.MMS_VERSION);
        this.running = true;
        this.reporter = new MmsStatsReporter();
        this.reporter.start(10, TimeUnit.SECONDS);
        logger.info("mms服务初始化成功");
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
        logger.info("mms服务成功关闭");
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
            logger.error("mms服务不在运行 不能进行订阅");
            return;
        }
        ConsumerFactory.getConsumer(new ConsumerGroup(consumerGroup), new Properties(), listener);
    }

    private void doSubscribe(String consumerGroup, Set<String> tags, MessageListener listener) {
        if (!running) {
            logger.error("mms服务不在运行 不能进行订阅");
            return;
        }
        ConsumerFactory.getConsumer(new ConsumerGroup(consumerGroup, MmsConst.DEFAULT_CONSUMER, tags), new Properties(), listener);
    }

    private void doSubscribe(String consumerGroup, Set<String> tags, MessageListener listener, Properties properties) {
        if (!running) {
            logger.error("mms服务不在运行 不能进行订阅");
            return;
        }
        ConsumerFactory.getConsumer(new ConsumerGroup(consumerGroup, MmsConst.DEFAULT_CONSUMER, tags), properties, listener);
    }

    private void doSendOneway(String topic, SimpleMessage simpleMessage) {
        if (!running) {
            logger.error("mms服务不在运行 不能进行发送");
            return;
        }
        // todo 收集指标
        // ProducerFactory.getProducer(topic).oneway(new MmsMessage(simpleMessage));
    }

    private SendResult doSendSync(String topic, SimpleMessage simpleMessage, Properties properties) {
        if (!running) {
            logger.error("mms服务不在运行 不能进行发送");
            return SendResult.buildErrorResult("mms服务不在运行");
        }
        ProducerProxy producer = ProducerFactory.getProducer(topic, properties);
        SendResult ret = producer.syncSend(new MmsMessage(simpleMessage));
        logger.info("生产者代理对象{}发送结果{}", producer, ret);
        return ret;
    }

    private void doSendAsync(String topic, SimpleMessage simpleMessage, Properties properties, SendCallback callBack) {
        if (!running) {
            logger.error("mms服务不在运行 不能进行发送");
            return;
        }
        ProducerFactory.getProducer(topic, properties).asyncSend(new MmsMessage(simpleMessage), callBack);
    }
}