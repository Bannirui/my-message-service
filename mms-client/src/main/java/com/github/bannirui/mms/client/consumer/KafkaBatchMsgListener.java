package com.github.bannirui.mms.client.consumer;

import java.util.List;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public abstract class KafkaBatchMsgListener implements MessageListener {

    public MsgConsumedStatus onMessage(ConsumerRecord msg) {
        throw RUNTIME_EXCEPTION;
    }
    public MsgConsumedStatus onMessage(List<ConsumerRecord<String, byte[]>> msgs) {
        throw RUNTIME_EXCEPTION;
    }
}

