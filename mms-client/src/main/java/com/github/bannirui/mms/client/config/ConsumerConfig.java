package com.github.bannirui.mms.client.config;

import java.util.Properties;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

public class ConsumerConfig {

    public static final class KAFKA {
        public static final Properties KAFKA_CONFIG = new Properties();

        static {
            KAFKA_CONFIG.put(org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getCanonicalName());//key反序列化方式
            KAFKA_CONFIG.put(org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class.getCanonicalName());//value反系列化方式
            KAFKA_CONFIG.put(org.apache.kafka.clients.consumer.ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);//自动提交
        }
    }
}


