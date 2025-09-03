package com.github.bannirui.mms.client.producer;

import com.github.bannirui.mms.client.common.MmsMessage;
import com.github.bannirui.mms.client.config.MmsClientConfig;
import com.github.bannirui.mms.client.consumer.MsgConsumedStatus;
import com.github.bannirui.mms.client.crypto.MmsCryptoManager;
import com.github.bannirui.mms.common.MmsConst;
import com.github.bannirui.mms.common.MmsException;
import com.github.bannirui.mms.logger.MmsLogger;
import com.github.bannirui.mms.metadata.MmsMetadata;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.*;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.apache.rocketmq.remoting.exception.RemotingTimeoutException;

public class RocketmqProducerProxy extends ProducerProxy {
    private static final MessageQueueSelector hashSelector;
    DefaultMQProducer producer;

    static {
        hashSelector = (mqs, msg, arg) -> {
            int id = msg.getKeys().hashCode() % mqs.size();
            return id < 0 ? (MessageQueue) mqs.get(-id) : (MessageQueue) mqs.get(id);
        };
    }

    public RocketmqProducerProxy(MmsMetadata metadata, boolean order, String name) {
        super(metadata, order, name);
        this.instanceName = name;
        this.start();
    }

    public RocketmqProducerProxy(MmsMetadata metadata, boolean order, String name, Properties properties) {
        super(metadata, order, name, properties);
        this.instanceName = name;
        this.start();
    }

    @Override
    public void startProducer() {
        this.producer = new DefaultMQProducer("mms_" + System.currentTimeMillis());
        long now = System.currentTimeMillis();
        if (this.metadata.isGatedLaunch()) {
            this.producer.setNamesrvAddr(this.metadata.getGatedCluster().getBootAddr());
            this.producer.setClientIP("producer-client-id-" + this.metadata.getGatedCluster().getClusterName() + "-" + MmsConst.MMS_IP + "-" + now);
        } else {
            this.producer.setNamesrvAddr(this.metadata.getClusterMetadata().getBootAddr());
            this.producer.setClientIP("producer-client-id-" + this.metadata.getClusterMetadata().getClusterName() + "-" + MmsConst.MMS_IP + "-" + now);
        }
        int retries = 2;
        int timeout = 3000;
        if (this.customizedProperties != null) {
            if (this.customizedProperties.containsKey(MmsClientConfig.PRODUCER.RETRIES.getKey())) {
                retries = Integer.parseInt(String.valueOf(this.customizedProperties.get(MmsClientConfig.PRODUCER.RETRIES.getKey())));
            }
            if (this.customizedProperties.containsKey(MmsClientConfig.PRODUCER.SEND_TIMEOUT_MS.getKey())) {
                timeout = Integer.parseInt(String.valueOf(this.customizedProperties.get(MmsClientConfig.PRODUCER.SEND_TIMEOUT_MS.getKey())));
            }
        }
        this.producer.setRetryTimesWhenSendFailed(retries);
        this.producer.setRetryTimesWhenSendAsyncFailed(retries);
        this.producer.setSendMsgTimeout(timeout);
        this.producer.setVipChannelEnabled(false);
        ResetTimeoutAndRetries resetTimeoutAndRetries = super.resetTimeoutAndRetries(timeout, retries);
        if (resetTimeoutAndRetries != null) {
            this.producer.setSendMsgTimeout(resetTimeoutAndRetries.getResetTimeout());
            this.producer.setRetryTimesWhenSendFailed(resetTimeoutAndRetries.getResetRetries());
            this.producer.setRetryTimesWhenSendAsyncFailed(resetTimeoutAndRetries.getResetRetries());
        }
        try {
            this.producer.start();
        } catch (MQClientException e) {
            MmsLogger.log.error("producer {} start failed", this.metadata.getName(), e);
            throw MmsException.PRODUCER_START_EXCEPTION;
        }
    }

    @Override
    public void shutdownProducer() {
        this.producer.shutdown();
    }

    @Override
    public SendResult syncSend(MmsMessage mmsMessage) {
        if (!this.running) {
            return SendResult.FAILURE_NOTRUNNING;
        }
        boolean succeed = false;
        SendResult ans;
        try {
            Message message = this.buildMessage(mmsMessage);
            if (mmsMessage.getDelayLevel() >= 1 && Arrays.stream(MsgConsumedStatus.values()).anyMatch((l) ->
                l.getLevel() == mmsMessage.getDelayLevel())) {
                message.setDelayTimeLevel(mmsMessage.getDelayLevel());
            }
            long startTime = System.currentTimeMillis();
            org.apache.rocketmq.client.producer.SendResult send;
            if (StringUtils.isEmpty(message.getKeys())) {
                send = this.producer.send(message);
            } else {
                send = this.producer.send(message, hashSelector, message.getKeys());
            }
            if (send.getSendStatus().equals(SendStatus.SEND_OK)) {
                long duration = System.currentTimeMillis() - startTime;
                this.mmsMetrics.sendCostRate().update(duration, TimeUnit.MILLISECONDS);
                succeed = true;
                this.mmsMetrics.getDistribution().markTime(duration);
                return SendResult.buildSuccessResult(send.getQueueOffset(), send.getOffsetMsgId(), send.getMessageQueue().getTopic(), send.getMessageQueue().getQueueId());
            }
            if (!send.getSendStatus().equals(SendStatus.FLUSH_DISK_TIMEOUT) && !send.getSendStatus().equals(SendStatus.FLUSH_SLAVE_TIMEOUT)) {
                this.mmsMetrics.messageFailureRate().mark();
                MmsLogger.log.error("syncSend topic {} failed slave not exist ", this.metadata.getName());
                return SendResult.FAILURE_TIMEOUT;
            }
            this.mmsMetrics.messageFailureRate().mark();
            MmsLogger.log.error("syncSend topic {} timeout for {} ", this.metadata.getName(), send.getSendStatus().name());
            return SendResult.FAILURE_TIMEOUT;
        } catch (MQClientException e) {
            MmsLogger.log.error("send failed for ", e);
            ans = SendResult.buildErrorResult("syncSend message MQClientException: " + e.getMessage());
            return ans;
        } catch (RemotingTimeoutException e) {
            MmsLogger.log.error("send failed for ", e);
            ans = SendResult.FAILURE_TIMEOUT;
        } catch (RemotingException e) {
            MmsLogger.log.error("send failed for ", e);
            ans = SendResult.buildErrorResult("syncSend message RemotingException: " + e.getMessage());
            return ans;
        } catch (MQBrokerException e) {
            MmsLogger.log.error("send failed for ", e);
            ans = SendResult.buildErrorResult("syncSend message MQBrokerException: " + e.getMessage());
            return ans;
        } catch (InterruptedException e) {
            MmsLogger.log.error("send failed for ", e);
            MmsLogger.log.error("produce syncSend and wait interuptted", e);
            ans = SendResult.FAILURE_INTERUPRION;
            return ans;
        } finally {
            if (succeed) {
                this.mmsMetrics.messageSuccessRate().mark();
            } else {
                this.mmsMetrics.messageFailureRate().mark();
            }
        }
        return ans;
    }

    @Override
    public void asyncSend(MmsMessage mmsMessage, SendCallback mmsCallBack) {
        Message message = this.buildMessage(mmsMessage);
        if (mmsMessage.getDelayLevel() >= 1 && Arrays.stream(MsgConsumedStatus.values()).anyMatch((l) -> l.getLevel() == mmsMessage.getDelayLevel())) {
            message.setDelayTimeLevel(mmsMessage.getDelayLevel());
        }
        final long startTime = System.currentTimeMillis();
        this.mmsMetrics.msgBody().markSize((long) mmsMessage.getPayload().length);
        try {
            org.apache.rocketmq.client.producer.SendCallback sendCallback = new org.apache.rocketmq.client.producer.SendCallback() {
                @Override
                public void onSuccess(org.apache.rocketmq.client.producer.SendResult sendResult) {
                    long duration = System.currentTimeMillis() - startTime;
                    RocketmqProducerProxy.this.mmsMetrics.sendCostRate().update(duration, TimeUnit.MILLISECONDS);
                    RocketmqProducerProxy.this.mmsMetrics.messageSuccessRate().mark();
                    RocketmqProducerProxy.this.mmsMetrics.getDistribution().markTime(duration);
                    mmsCallBack.onResult(SendResult.buildSuccessResult(sendResult.getQueueOffset(), sendResult.getOffsetMsgId(), sendResult.getMessageQueue().getTopic(), sendResult.getMessageQueue().getQueueId()));
                }

                @Override
                public void onException(Throwable e) {
                    RocketmqProducerProxy.this.mmsMetrics.messageFailureRate().mark();
                    MmsLogger.log.error("aysnc send failed for ", e);
                    mmsCallBack.onException(e);
                }
            };
            if (StringUtils.isEmpty(message.getKeys())) {
                this.producer.send(message, sendCallback);
            } else {
                this.producer.send(message, hashSelector, message.getKeys(), sendCallback);
            }
        } catch (RemotingException | InterruptedException | MQClientException e) {
            MmsLogger.log.error("aysnc send failed for ", e);
        }
    }

    @Override
    public void oneway(MmsMessage mmsMessage) {
        Message message = this.buildMessage(mmsMessage);
        message.setFlag(0);
        message.setWaitStoreMsgOK(false);
        this.mmsMetrics.msgBody().markSize((long) mmsMessage.getPayload().length);
        try {
            this.producer.send(message);
        } catch (RemotingException | MQBrokerException | InterruptedException | MQClientException e) {
            MmsLogger.log.warn("exception was ignored for oneway", e);
        }
    }

    private Message buildMessage(MmsMessage mmsMessage) {
        Map<String, String> properties = new HashMap<>();
        if (mmsMessage.getProperties() != null && !mmsMessage.getProperties().isEmpty()) {
            properties.putAll(mmsMessage.getProperties());
        }
        if (this.metadata.getIsEncrypt()) {
            properties.put("encrypt_mark", "#%$==");
            mmsMessage.setPayload(MmsCryptoManager.encrypt(this.metadata.getName(), mmsMessage.getPayload()));
        }
        if (StringUtils.isNotBlank(MQ_TAG)) {
            properties.put("mqTag", MQ_TAG);
        }
        if (StringUtils.isNotBlank(MQ_COLOR)) {
            properties.put("mqColor", MQ_COLOR);
        }
        Message message = new Message(this.metadata.getName(), mmsMessage.getTags(), mmsMessage.getKey(), mmsMessage.getPayload());
        message.getProperties().putAll(properties);
        return message;
    }
}
