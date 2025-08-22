package com.github.bannirui.mms.dal.model;

import lombok.Data;

@Data
public class TopicEnvServerRef {
    /**
     * @see TopicRef#id
     */
    private Long tesId;

    /**
     * @see Topic
     */
    private Long topicId;
    private String topicName;
    private Integer topicStatus;
    private Long userId;
    private Long appId;
    private Integer tps;
    private Integer msgSz;
    private Integer partitions;
    private String remark;

    /**
     * @see Env
     */
    private Long envId;
    private String envName;

    /**
     * @see Server
     */
    private Long serverId;
    private String serverName;
    private Integer serverType;
}
