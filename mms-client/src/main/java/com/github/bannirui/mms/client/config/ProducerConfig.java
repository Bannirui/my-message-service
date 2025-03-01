package com.github.bannirui.mms.client.config;

import java.util.Properties;

public class ProducerConfig {

    public static final String TIMEOUT = "timeout";
    public static final String RETRIES = "retries";

    public static final class KAFKA {
        public static final Properties KAFKA_CONFIG = new Properties();

        static {
            KAFKA_CONFIG.put("acks", "all");
            KAFKA_CONFIG.put(RETRIES, 0);
            KAFKA_CONFIG.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            KAFKA_CONFIG.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");
            KAFKA_CONFIG.put("max.in.flight.requests.per.connection", "1");
        }
    }
}


