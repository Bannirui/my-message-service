package com.github.bannirui.mms.client.statistic.kafka;

import java.util.List;

public class KafkaConsumerStatistics {
    List<KafkaConsumerInfo> kafkaConsumerInfos;

    public List<KafkaConsumerInfo> getKafkaConsumerInfos() {
        return this.kafkaConsumerInfos;
    }

    public void setKafkaConsumerInfos(List<KafkaConsumerInfo> kafkaConsumerInfos) {
        this.kafkaConsumerInfos = kafkaConsumerInfos;
    }
}
