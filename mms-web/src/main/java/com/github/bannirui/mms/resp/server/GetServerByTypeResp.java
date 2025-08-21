package com.github.bannirui.mms.resp.server;

import lombok.Data;

@Data
public class GetServerByTypeResp {

    private Long serverId;
    private String serverName;
    private String host;
    private Integer port;
}
