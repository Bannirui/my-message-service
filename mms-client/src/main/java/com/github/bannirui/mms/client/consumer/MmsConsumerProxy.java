package com.github.bannirui.mms.client.consumer;

import com.alibaba.fastjson.JSON;
import com.github.bannirui.mms.client.Mms;
import com.github.bannirui.mms.client.MmsProxy;
import com.github.bannirui.mms.client.common.SimpleMessage;
import com.github.bannirui.mms.client.common.StatsLoggerType;
import com.github.bannirui.mms.client.metrics.MmsConsumerMetrics;
import com.github.bannirui.mms.common.MmsConst;
import com.github.bannirui.mms.common.MmsException;
import com.github.bannirui.mms.logger.MmsLogger;
import com.github.bannirui.mms.metadata.ConsumerGroupMetadata;
import com.github.bannirui.mms.metadata.MmsMetadata;
import com.github.bannirui.mms.stats.StatsInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.util.Objects;
import java.util.Properties;
import java.util.Random;

/**
 * 消费者代理 屏蔽中间件差异和细节
 * <ul>
 *     <li>kafka消息是{@link org.apache.kafka.clients.consumer.ConsumerRecord}</li>
 *     <li>rocketmq消息是{@link org.apache.rocketmq.common.message.MessageExt}</li>
 * </ul>
 *
 * @param <T> 消息体类型
 */
public abstract class MmsConsumerProxy<T> extends MmsProxy<MmsConsumerMetrics> implements Consumer {

    public static final Logger logger = MmsLogger.log;

    protected MessageListener listener;
    protected Properties customizedProperties;

    protected static String MQ_TAG;
    protected static String MQ_COLOR;

    static {
        String mmsRewrite = System.getProperty("mmsRewrite");
        String mqTag = System.getProperty("mqTag");
        String mqColor = System.getProperty("mqColor");
        if (StringUtils.isNotBlank(mqTag) && StringUtils.isNotBlank(mqColor)) {
            throw MmsException.DEPLOY_EXCEPTION;
        } else if (StringUtils.isNotBlank(mqTag) && StringUtils.isBlank(mmsRewrite)) {
            throw MmsException.MQ_TAG_EXCEPTION;
        } else {
            MQ_TAG = mqTag == null ? "" : mqTag;
            MQ_COLOR = mqColor == null ? "" : mqColor;
        }
    }

    public MmsConsumerProxy(MmsMetadata metadata, boolean isOrderly, String name, Properties properties, MessageListener listener) {
        super(metadata, isOrderly, new MmsConsumerMetrics(metadata.getName(), name));
        this.listener = listener;
        this.customizedProperties = properties;
    }

    @Override
    public void start() {
        if (running) {
            logger.warn("Conumser {} has been started,cant'be started again", instanceName);
            return;
        }
        consumerStart();
        super.start();
        register(listener);
        logger.info("ConsumerProxy started at {}, consumer group name:{}", System.currentTimeMillis(), metadata.getName());
    }

    protected abstract void consumerStart();

    @Override
    public void restart() {
        logger.info("consumer {} begin to restart", instanceName);
        shutdown();
        try {
            Thread.sleep(new Random().nextInt(1000));
        } catch (InterruptedException ignored) {
        }
        start();
    }

    @Override
    public boolean changeConfigAndRestart(MmsMetadata oldMetadata, MmsMetadata newMetadata) {
        if (oldMetadata.isGatedLaunch() ^ newMetadata.isGatedLaunch()) {
            return true;
        }
        ConsumerGroupMetadata oldConsumerMeta = (ConsumerGroupMetadata) oldMetadata;
        ConsumerGroupMetadata newConsumerMeta = (ConsumerGroupMetadata) newMetadata;
        return !Objects.equals(oldConsumerMeta.getClusterMetadata(), newConsumerMeta.getClusterMetadata()) ||
                !Objects.equals(oldConsumerMeta.getBindingTopic(), newConsumerMeta.getBindingTopic()) ||
                !Objects.equals(oldConsumerMeta.getBroadcast(), newConsumerMeta.getBroadcast()) ||
                !Objects.equals(oldConsumerMeta.getConsumeFrom(), newConsumerMeta.getConsumeFrom());
    }

    @Override
    public void shutdown() {
        if (!running) {
            logger.warn("Consumer {} has been shutdown,cant'be shutdown again", instanceName);
            return;
        }
        running = false;
        super.shutdown();
        consumerShutdown();
        ConsumerFactory.recycle(metadata.getName(), instanceName);
        logger.info("Consumer {} shutdown", instanceName);
    }

    @Override
    public void statistics() {
        if (running && !isStatistic(mmsMetrics.getClientName())) {
            if (StringUtils.isEmpty(metadata.getStatisticsLogger()) || StatsLoggerType.MESSAGE.getName().equalsIgnoreCase(metadata.getStatisticsLogger())) {
                StatsInfo info = mmsMetrics.reportMessageStatistics();
                Mms.sendOneway(MmsConst.STATISTICS.STATISTICS_TOPIC_CONSUMERINFO, new SimpleMessage(JSON.toJSONBytes(info)));
            } else {
                MmsLogger.statisticLog.info(mmsMetrics.reportLogStatistics());
            }
        }
    }

    protected abstract void consumerShutdown();

    @Override
    abstract public void addUserDefinedProperties(Properties properties);

    protected boolean msgFilter(String mqTagValue) {
        return StringUtils.isBlank(MQ_TAG)
                && StringUtils.isBlank(mqTagValue) || MQ_TAG.equals(mqTagValue);
    }

    protected boolean msgFilterByColor(String mqColorValue) {
        String releaseColor = ((ConsumerGroupMetadata) this.metadata).getReleaseStatus();
        if (null != releaseColor && !releaseColor.equals("all")) {
            if (releaseColor.equals("default") && StringUtils.isBlank(mqColorValue)) {
                return true;
            } else {
                return StringUtils.isNotBlank(mqColorValue) && (releaseColor.equals("blue") || releaseColor.equals("green")) && MQ_COLOR.equals(mqColorValue);
            }
        } else {
            return true;
        }
    }
}

