package com.github.bannirui.mms.client.statistic.kafka;

import java.util.List;

public class KafkaProducerStatistics {
    private List<KafkaProducerInfo> kafkaProducerInfos;

    public List<KafkaProducerInfo> getKafkaProducerInfos() {
        return this.kafkaProducerInfos;
    }

    public void setKafkaProducerInfos(List<KafkaProducerInfo> kafkaProducerInfos) {
        this.kafkaProducerInfos = kafkaProducerInfos;
    }
}
