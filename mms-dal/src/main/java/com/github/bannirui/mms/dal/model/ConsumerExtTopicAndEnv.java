package com.github.bannirui.mms.dal.model;

import lombok.Data;

@Data
public class ConsumerExtTopicAndEnv {

    /**
     * @see Consumer
     */
    private Long consumerId;
    private String consumerName;
    private Long consumerUserId;
    private String consumerUserName;
    /**
     * topic给哪个应用的
     */
    private Long consumerAppId;
    private String consumerAppName;
    private Integer consumerStatus;
    private String consumerRemark;
    private Integer consumerBroadcast;
    private Integer consumerFromMin;

    private Long topicId;
    private String topicName;
    private Integer topicType;

    /**
     * @see Env
     */
    private Long envId;
    private Integer envSort;
    private String envName;

    // mq集群名
    private String clusterName;
}
