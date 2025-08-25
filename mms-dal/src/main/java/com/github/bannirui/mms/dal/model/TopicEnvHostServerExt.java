package com.github.bannirui.mms.dal.model;

import lombok.Data;

@Data
public class TopicEnvHostServerExt {

    /**
     * @see Topic
     */
    private Long topicId;
    private String topicName;
    private Integer topicType;
    private Integer topicStatus;
    private Integer topicTps;
    private Integer topicMsgSz;
    private Integer topicPartitions;
    private Integer topicReplication;
    private String topicRemark;

    /**
     * 谁申请的topic
     */
    private Long userId;
    private String userName;
    /**
     * topic给哪个应用的
     */
    private Long appId;
    private String appName;

    /**
     * @see Env
     */
    private Long envId;
    private Integer envSort;
    private String envName;

    /**
     * {@link Host}
     */
    private Long hostId;
    private String hostName;
    private String hostHost;
    /**
     * @see Server
     */
    private Long serverId;
    private String serverName;
    private Integer serverType;
    private Integer serverPort;
}
