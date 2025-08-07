package com.github.bannirui.mms.client.config;

import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Properties;

public class ConsumerConfig {

    public static final class KAFKA {
        public static final Properties KAFKA_CONFIG = new Properties();

        static {
            // key反序列化方式
            KAFKA_CONFIG.put(org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getCanonicalName());
            // value反系列化方式
            KAFKA_CONFIG.put(org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class.getCanonicalName());
            // 自动提交
            KAFKA_CONFIG.put(org.apache.kafka.clients.consumer.ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        }
    }
}


