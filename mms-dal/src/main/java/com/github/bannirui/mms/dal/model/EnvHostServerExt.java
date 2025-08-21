package com.github.bannirui.mms.dal.model;

import lombok.Data;

/**
 * 环境-主机-服务
 */
@Data
public class EnvHostServerExt {
    private Long envId;
    private String envName;
    private Integer envStatus;
    private Integer envSortId;

    private Long zkId;
    private String zkName;
    private String zkHost;
    private Integer zkPort;

    private Long hostId;
    private String hostName;
    private String hostHost;
    private Integer hostStatus;

    private Long serverId;
    private String serverName;
    private Integer serverType;
    private Integer serverPort;
    private Integer serverStatus;
}
