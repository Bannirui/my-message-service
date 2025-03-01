package com.github.bannirui.mms.client.consumer;

import com.github.bannirui.mms.client.common.ConsumeFromWhere;
import com.github.bannirui.mms.client.crypto.MmsCryptoManager;
import com.github.bannirui.mms.common.MmsConst;
import com.github.bannirui.mms.common.MmsException;
import com.github.bannirui.mms.metadata.ConsumerGroupMetadata;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.protocol.heartbeat.MessageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springside.modules.utils.collection.CollectionUtil;

public class RocketmqConsumerProxy extends MmsConsumerProxy<MessageExt> {

    public static final Logger logger = LoggerFactory.getLogger(RocketmqConsumerProxy.class);

    private DefaultMQPushConsumer consumer;
    private Set<String> tags;

    public RocketmqConsumerProxy(ConsumerGroupMetadata metadata, boolean isOrderly, String instanceName, Set<String> tags, Properties properties,
                                 MessageListener listener) {
        super(metadata, isOrderly, instanceName, properties, listener);
        this.instanceName = instanceName;
        this.tags = tags;
        start();
    }

    @Override
    public void register(MessageListener listener) {
        if (super.isOrderly) {
            this.consumer.registerMessageListener((MessageListenerOrderly) (msgs, context) -> {
                msgs = msgs.stream()
                    .filter((msgx) -> RocketmqConsumerProxy.super.msgFilter(RocketmqConsumerProxy.this.getMqTagValue(msgx)))
                    .filter((msgx) -> RocketmqConsumerProxy.super.msgFilterByColor(RocketmqConsumerProxy.this.getMqColorValue(msgx)))
                    .collect(Collectors.toList());
                if (msgs.size() < 1) {
                    return ConsumeOrderlyStatus.SUCCESS;
                }
                msgs.forEach((msgx) -> {
                    try {
                        RocketmqConsumerProxy.this.decryptMsgBodyIfNecessary(msgx);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                });
                long begin = System.currentTimeMillis();
                MsgConsumedStatus msgConsumedStatus = MsgConsumedStatus.SUCCEED;
                try {
                    if (!listener.isEasy() && !(listener instanceof RocketmqMessageListener)) {
                        RocketmqBatchMsgListener batchMsgListener = (RocketmqBatchMsgListener) listener;
                        msgConsumedStatus = batchMsgListener.onMessage(msgs);
                    } else {
                        for (MessageExt msg : msgs) {
                            MsgConsumedStatus consumeStatus;
                            if (listener.isEasy()) {
                                ConsumeMessage consumeMessage = ConsumeMessage.parse(msg);
                                consumeStatus = listener.onMessage(consumeMessage);
                                if (msgConsumedStatus.equals(MsgConsumedStatus.SUCCEED) && !consumeStatus.equals(MsgConsumedStatus.SUCCEED)) {
                                    msgConsumedStatus = consumeStatus;
                                }
                            } else {
                                RocketmqMessageListener rocketmqMessageListener = (RocketmqMessageListener) listener;
                                consumeStatus = rocketmqMessageListener.onMessage(msg);
                                if (msgConsumedStatus.equals(MsgConsumedStatus.SUCCEED) && !consumeStatus.equals(MsgConsumedStatus.SUCCEED)) {
                                    msgConsumedStatus = consumeStatus;
                                }
                            }
                        }
                    }
                    RocketmqConsumerProxy.this.mmsMetrics.consumeSuccessRate().mark();
                } catch (Throwable e) {
                    MmsConsumerProxy.logger.error("consumer msg failed for {} batch", msgs.get(0).getMsgId(), e);
                    msgConsumedStatus = MsgConsumedStatus.RETRY;
                    RocketmqConsumerProxy.this.mmsMetrics.consumeFailureRate().mark();
                }
                if (!msgConsumedStatus.equals(MsgConsumedStatus.SUCCEED)) {
                    RocketmqConsumerProxy.this.mmsMetrics.userCostTimeMs().update(System.currentTimeMillis() - begin, TimeUnit.MILLISECONDS);
                    return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
                } else {
                    RocketmqConsumerProxy.this.mmsMetrics.userCostTimeMs().update(System.currentTimeMillis() - begin, TimeUnit.MILLISECONDS);
                    return ConsumeOrderlyStatus.SUCCESS;
                }
            });
        } else {
            this.consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
                msgs = msgs.stream().filter((msgx) -> RocketmqConsumerProxy.this.msgFilter(RocketmqConsumerProxy.this.getMqTagValue(msgx)))
                    .filter((msgx) -> RocketmqConsumerProxy.this.msgFilterByColor(RocketmqConsumerProxy.this.getMqColorValue(msgx)))
                    .collect(Collectors.toList());
                if (msgs.size() < 1) {
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
                msgs.forEach((msgx) -> {
                    try {
                        RocketmqConsumerProxy.this.decryptMsgBodyIfNecessary(msgx);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                });
                long begin = System.currentTimeMillis();
                MsgConsumedStatus msgConsumedStatus = MsgConsumedStatus.SUCCEED;
                try {
                    if (!listener.isEasy() && !(listener instanceof RocketmqMessageListener)) {
                        RocketmqBatchMsgListener rocketmqMessageListener = (RocketmqBatchMsgListener) listener;
                        msgConsumedStatus = rocketmqMessageListener.onMessage(msgs);
                    } else {
                        for (MessageExt msg : msgs) {
                            MsgConsumedStatus consumeStatus;
                            if (listener.isEasy()) {
                                ConsumeMessage consumeMessage = ConsumeMessage.parse(msg);
                                consumeStatus = listener.onMessage(consumeMessage);
                                if (msgConsumedStatus.equals(MsgConsumedStatus.SUCCEED) && !consumeStatus.equals(MsgConsumedStatus.SUCCEED)) {
                                    msgConsumedStatus = consumeStatus;
                                }
                            } else {
                                RocketmqMessageListener rocketmqMessageListenerx = (RocketmqMessageListener) listener;
                                consumeStatus = rocketmqMessageListenerx.onMessage(msg);
                                if (msgConsumedStatus.equals(MsgConsumedStatus.SUCCEED) && !consumeStatus.equals(MsgConsumedStatus.SUCCEED)) {
                                    msgConsumedStatus = consumeStatus;
                                }
                            }
                        }
                    }
                    RocketmqConsumerProxy.this.mmsMetrics.consumeSuccessRate().mark();
                } catch (Throwable e) {
                    MmsConsumerProxy.logger.error("consumer msg failed for {} batch", ((MessageExt) msgs.get(0)).getMsgId(), e);
                    msgConsumedStatus = MsgConsumedStatus.RETRY;
                    RocketmqConsumerProxy.this.mmsMetrics.consumeFailureRate().mark();
                }
                if (!msgConsumedStatus.equals(MsgConsumedStatus.SUCCEED)) {
                    RocketmqConsumerProxy.this.mmsMetrics.userCostTimeMs().update(System.currentTimeMillis() - begin, TimeUnit.MILLISECONDS);
                    context.setDelayLevelWhenNextConsume(msgConsumedStatus.level);
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                } else {
                    RocketmqConsumerProxy.this.mmsMetrics.userCostTimeMs().update(System.currentTimeMillis() - begin, TimeUnit.MILLISECONDS);
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
            });
        }
        try {
            this.consumer.start();
            logger.info("ConsumerProxy started at {}, consumer group name:{}", System.currentTimeMillis(), this.metadata.getName());
        } catch (Exception e) {
            logger.error("RocketMQConsumer start error", e);
        }
    }

    @Override
    protected void consumerStart() {
        consumer = new DefaultMQPushConsumer(metadata.getName());
        if (metadata.isGatedLaunch()) {
            consumer.setNamesrvAddr(metadata.getGatedCluster().getBootAddr());
            consumer.setClientIP("consumer-client-id-" + metadata.getGatedCluster().getClusterName() + "-" + MmsConst.MMS_IP);
        } else {
            consumer.setNamesrvAddr(metadata.getClusterMetadata().getBootAddr());
            consumer.setClientIP("consumer-client-id-" + metadata.getClusterMetadata().getClusterName() + "-" + MmsConst.MMS_IP);
        }
        consumer.setVipChannelEnabled(false);
        String bindingTopic = ((ConsumerGroupMetadata) metadata).getBindingTopic();
        String consumeFrom = ((ConsumerGroupMetadata) metadata).getConsumeFrom();
        String broadCast = ((ConsumerGroupMetadata) metadata).getBroadcast();
        if (((ConsumerGroupMetadata) metadata).needSuspend()) {
            logger.error("consumer {} suspend is on, please set it to off first", metadata.getName());
            throw new RuntimeException("consumer {} suspend is on, please set it to off first");
        }
        if (StringUtils.isEmpty(consumeFrom)) {
            consumer.setConsumeFromWhere(org.apache.rocketmq.common.consumer.ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        } else {
            if (ConsumeFromWhere.EARLIEST.getName().equalsIgnoreCase(consumeFrom)) {
                consumer.setConsumeFromWhere(org.apache.rocketmq.common.consumer.ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
            } else {
                consumer.setConsumeFromWhere(org.apache.rocketmq.common.consumer.ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
            }
        }
        if (!StringUtils.isEmpty(broadCast) && Boolean.valueOf(broadCast)) {
            consumer.setMessageModel(MessageModel.BROADCASTING);
        }
        if (customizedProperties != null) {
            addUserDefinedProperties(customizedProperties);
        }
        logger.info("consumer {} start with param {}", instanceName, buildConsumerInfo(consumer));
        try {
            if (CollectionUtil.isNotEmpty(tags)) {
                String combinedTags = StringUtils.join(tags, "||");
                logger.info("consumer {} start with tags {}", instanceName, combinedTags);
                consumer.subscribe(bindingTopic, combinedTags);
            } else {
                consumer.subscribe(bindingTopic, "*");
            }
        } catch (MQClientException e) {
            logger.error("RocketMQConsumer register {} error", bindingTopic, e);
            throw MmsException.CONSUMER_START_EXCEPTION;
        }
    }

    @Override
    protected void consumerShutdown() {
        consumer.shutdown();
    }

    @Override
    public void addUserDefinedProperties(Properties properties) {
        if (properties.containsKey(MmsConst.CLIENT_CONFIG.CONSUME_MESSAGES_SIZE)) {
            consumer.setPullBatchSize(Integer.parseInt((String) properties.get(MmsConst.CLIENT_CONFIG.CONSUME_MESSAGES_SIZE)));
        }
        if (properties.containsKey(MmsConst.CLIENT_CONFIG.ROCKETMQ_CONSUME_BATCH)) {
            consumer.setConsumeMessageBatchMaxSize(Integer.parseInt((String) properties.get(MmsConst.CLIENT_CONFIG.ROCKETMQ_CONSUME_BATCH)));
        }
        if (properties.containsKey(MmsConst.CLIENT_CONFIG.CONSUME_THREAD_MIN)) {
            int consumeThreadMin = Integer.parseInt((String) properties.get(MmsConst.CLIENT_CONFIG.CONSUME_THREAD_MIN));
            consumer.setConsumeThreadMin(consumeThreadMin);
        }
        if (properties.containsKey(MmsConst.CLIENT_CONFIG.CONSUME_THREAD_MAX)) {
            int consumeThreadMax = Integer.parseInt((String) properties.get(MmsConst.CLIENT_CONFIG.CONSUME_THREAD_MAX));
            consumer.setConsumeThreadMax(consumeThreadMax);
        }
    }

    private String buildConsumerInfo(DefaultMQPushConsumer consumer) {
        StringBuilder consumerInfo = new StringBuilder();
        consumerInfo.append(" clientIP: ").append(consumer.getClientIP());
        consumerInfo.append(System.lineSeparator());
        consumerInfo.append(" nameSrv: ").append(consumer.getNamesrvAddr());
        consumerInfo.append(System.lineSeparator());
        consumerInfo.append(" batchSize: ").append(consumer.getPullBatchSize());
        consumerInfo.append(System.lineSeparator());
        consumerInfo.append(" consumeThreadMin: ").append(consumer.getConsumeThreadMin());
        consumerInfo.append(System.lineSeparator());
        consumerInfo.append(" consumeThreadMax: ").append(consumer.getConsumeThreadMax());
        return consumerInfo.toString();
    }

    private String getMqTagValue(MessageExt msg) {
        return msg.getProperties().get("mqTag");
    }

    private String getMqColorValue(MessageExt msg) {
        return msg.getProperties().get("mqColor");
    }

    private void decryptMsgBodyIfNecessary(MessageExt msg) {
        Map<String, String> properties = msg.getProperties();
        String encryptMarkValue = properties.get("encrypt_mark");
        if (StringUtils.isNotBlank(encryptMarkValue)) {
            msg.setBody(MmsCryptoManager.decrypt(msg.getTopic(), msg.getBody()));
        }
    }
}