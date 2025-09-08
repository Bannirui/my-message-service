package com.github.bannirui.mms.dal.model;

import lombok.Data;

@Data
public class MqMetaDataExt {
    // broker
    private String clusterName;
    private String clusterHost;
    private int clusterPort;
    private int clusterType;
    private int clusterStatus;
    // topic
    private String topicName;
    private Integer topicStatus;
    // consumer
    private String consumerName;
    private Integer consumerStatus;
    private Integer consumerBroadcast;
    private Integer consumerFromMin;
}
