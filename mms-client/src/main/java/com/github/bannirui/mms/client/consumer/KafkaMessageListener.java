package com.github.bannirui.mms.client.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface KafkaMessageListener extends MessageListener {

    @Override
    default boolean isEasy() {
        return false;
    }

    default MsgConsumedStatus onMessage(ConsumerRecord msg) {
        if (this.isEasy()) {
            throw RUNTIME_EXCEPTION;
        } else {
            return MsgConsumedStatus.SUCCEED;
        }
    }
}

