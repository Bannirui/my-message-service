package com.github.bannirui.mms.client.consumer;

import java.util.List;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.rocketmq.common.message.MessageExt;

/**
 * 封装消息监听器 屏蔽中间件差异
 */
public interface MessageListener {

	RuntimeException RUNTIME_EXCEPTION = new RuntimeException("illegal messageListener type, should correct rocketmqMessageListener or kafkaMessageListener");

	default MsgConsumedStatus onMessage(ConsumeMessage msg) {
		return MsgConsumedStatus.SUCCEED;
	}

	default boolean isEasy() {
		return true;
	}
}

