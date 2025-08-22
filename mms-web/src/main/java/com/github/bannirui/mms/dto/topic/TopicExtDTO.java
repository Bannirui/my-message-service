package com.github.bannirui.mms.dto.topic;

import lombok.Data;

import java.util.List;

@Data
public class TopicExtDTO {

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

    private List<EnvExtDTO> envs;
}

