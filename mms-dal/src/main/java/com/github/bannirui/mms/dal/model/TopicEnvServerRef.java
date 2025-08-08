package com.github.bannirui.mms.dal.model;

import lombok.Data;

@Data
public class TopicEnvServerRef {
    /**
     * @see TopicEnvServer#id
     */
    private Long tesId;

    /**
     * @see Topic
     */
    private Long topicId;
    private String topicName;

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
