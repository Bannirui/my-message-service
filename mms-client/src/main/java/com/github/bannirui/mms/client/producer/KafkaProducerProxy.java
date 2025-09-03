package com.github.bannirui.mms.client.producer;

import com.github.bannirui.mms.client.common.MmsMessage;
import com.github.bannirui.mms.client.config.MmsClientConfig;
import com.github.bannirui.mms.client.config.ProducerConfig;
import com.github.bannirui.mms.client.crypto.MmsCryptoManager;
import com.github.bannirui.mms.client.metrics.KafkaProducerStatusReporter;
import com.github.bannirui.mms.common.MmsConst;
import com.github.bannirui.mms.logger.MmsLogger;
import com.github.bannirui.mms.metadata.MmsMetadata;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.header.internals.RecordHeaders;

public class KafkaProducerProxy extends ProducerProxy {
    private static final Charset utf_8 = StandardCharsets.UTF_8;
    private KafkaProducer<String, byte[]> producer;
    private int sendTimeOut = 3000;

    public KafkaProducerProxy(MmsMetadata metadata, boolean order, String instanceName) {
        super(metadata, order, instanceName);
        this.instanceName = instanceName;
        this.start();
    }

    public KafkaProducerProxy(MmsMetadata metadata, boolean order, String instanceName, Properties properties) {
        super(metadata, order, instanceName, properties);
        this.instanceName = instanceName;
        this.start();
    }

    @Override
    public void statistics() {
        super.statistics();
        KafkaProducerStatusReporter.getInstance().reportProducerStatus();
    }

    @Override
    public void startProducer() {
        Properties kafkaProperties = new Properties();
        kafkaProperties.putAll(ProducerConfig.KAFKA.KAFKA_CONFIG);
        if (this.metadata.isGatedLaunch()) {
            kafkaProperties.put("bootstrap.servers", this.metadata.getGatedCluster().getBootAddr());
        } else {
            kafkaProperties.put("bootstrap.servers", this.metadata.getClusterMetadata().getBootAddr());
        }
        kafkaProperties.put("client.id", this.metadata.getName() + "--" + MmsConst.MMS_IP + "--" + ThreadLocalRandom.current().nextInt(100_000));
        if (this.customizedProperties != null) {
            kafkaProperties.putAll(this.customizedProperties);
        }
        this.reviseKafkaConfig(kafkaProperties);
        this.producer = new KafkaProducer<>(kafkaProperties);
    }

    private void reviseKafkaConfig(Properties properties) {
        if (properties.containsKey(MmsClientConfig.PRODUCER.SEND_TIMEOUT_MS.getKey())) {
            this.sendTimeOut = Integer.parseInt(String.valueOf(properties.get(MmsClientConfig.PRODUCER.SEND_TIMEOUT_MS.getKey())));
            properties.remove(MmsClientConfig.PRODUCER.SEND_TIMEOUT_MS.getKey());
        }
        properties.remove(MmsClientConfig.PRODUCER.RETRIES.getKey());
    }

    @Override
    public void shutdownProducer() {
        this.producer.close(Duration.ofSeconds(3L));
    }

    @Override
    public SendResult syncSend(MmsMessage mmsMessage) {
        if (!this.running) {
            return SendResult.FAILURE_NOTRUNNING;
        } else {
            ProducerRecord<String, byte[]> record = this.buildMessage(mmsMessage);
            long startTime = System.currentTimeMillis();
            boolean succeed = false;
            SendResult ans=null;
            try {
                Future<RecordMetadata> send = this.producer.send(record);
                this.mmsMetrics.msgBody().markSize((long)((byte[])record.value()).length);
                RecordMetadata metadata = send.get((long)this.sendTimeOut, TimeUnit.MILLISECONDS);
                long duration = System.currentTimeMillis() - startTime;
                this.mmsMetrics.sendCostRate().update(duration, TimeUnit.MILLISECONDS);
                succeed = true;
                this.mmsMetrics.getDistribution().markTime(duration);
                return SendResult.buildSuccessResult(metadata.offset(), "", metadata.topic(), metadata.partition());
            } catch (InterruptedException e) {
                MmsLogger.log.error("produce syncSend and wait interuptted", e);
                return SendResult.FAILURE_INTERUPRION;
            } catch (ExecutionException e) {
                MmsLogger.log.error("produce syncSend and wait got exception", e);
                if (e.getCause() instanceof TimeoutException) {
                    return SendResult.FAILURE_TIMEOUT;
                }
                String errMsg = "execution got exception when syncSend and wait message: ";
                if (e.getCause() != null && StringUtils.isNoneBlank(new CharSequence[]{e.getCause().getMessage()})) {
                    errMsg = errMsg + e.getCause().getMessage();
                }
                return SendResult.buildErrorResult(errMsg);
            } catch (java.util.concurrent.TimeoutException e) {
                MmsLogger.log.error("produce syncSend and wait timeout", e);
                ans = SendResult.FAILURE_TIMEOUT;
            } finally {
                if (succeed) {
                    this.mmsMetrics.messageSuccessRate().mark();
                } else {
                    this.mmsMetrics.messageFailureRate().mark();
                }
            }
            return ans;
        }
    }

    @Override
    public void asyncSend(MmsMessage mmsMessage, SendCallback callBack) {
        ProducerRecord<String, byte[]> record = this.buildMessage(mmsMessage);
        long startTime = System.currentTimeMillis();
        this.mmsMetrics.msgBody().markSize(record.value().length);
        this.producer.send(record, (metadata, exception) -> {
            if (exception != null) {
                this.mmsMetrics.messageFailureRate().mark();
                callBack.onException(exception);
            } else {
                long duration = System.currentTimeMillis() - startTime;
                SendResult sendResponse = SendResult.buildSuccessResult(metadata.offset(), "", metadata.topic(), metadata.partition());
                this.mmsMetrics.sendCostRate().update(duration, TimeUnit.MILLISECONDS);
                this.mmsMetrics.messageSuccessRate().mark();
                this.mmsMetrics.getDistribution().markTime(duration);
                callBack.onResult(sendResponse);
            }
        });
    }

    @Override
    public void oneway(MmsMessage mmsMessage) {
        this.mmsMetrics.msgBody().markSize(mmsMessage.getPayload().length);
        ProducerRecord<String, byte[]> producerRecord = this.buildMessage(mmsMessage);
        this.producer.send(producerRecord);
    }

    private ProducerRecord<String, byte[]> buildMessage(MmsMessage mmsMessage) {
        Headers headers = new RecordHeaders();
        if (mmsMessage.getProperties() != null && !mmsMessage.getProperties().isEmpty()) {
            mmsMessage.getProperties().forEach((k, v) -> {
                Header header = new RecordHeader(k, v.getBytes(utf_8));
                headers.add(header);
            });
        }
        RecordHeader header;
        if (this.metadata.getIsEncrypt()) {
            header = new RecordHeader("encrypt_mark", "#%$==".getBytes(utf_8));
            headers.add(header);
            mmsMessage.setPayload(MmsCryptoManager.encrypt(this.metadata.getName(), mmsMessage.getPayload()));
        }
        if (StringUtils.isNotBlank(MQ_TAG)) {
            header = new RecordHeader("mqTag", MQ_TAG.getBytes(utf_8));
            headers.add(header);
        }
        if (StringUtils.isNotBlank(MQ_COLOR)) {
            header = new RecordHeader("mqColor", MQ_COLOR.getBytes(utf_8));
            headers.add(header);
        }
        return new ProducerRecord<>(this.metadata.getName(), null, mmsMessage.getKey(), mmsMessage.getPayload(), headers);
    }
}
