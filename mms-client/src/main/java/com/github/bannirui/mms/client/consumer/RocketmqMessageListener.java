package com.github.bannirui.mms.client.consumer;

import org.apache.rocketmq.common.message.MessageExt;

public interface RocketmqMessageListener extends MessageListener {

    default MsgConsumedStatus onMessage(MessageExt msg) {
        if (this.isEasy()) {
            throw RUNTIME_EXCEPTION;
        } else {
            return MsgConsumedStatus.SUCCEED;
        }
    }
}

