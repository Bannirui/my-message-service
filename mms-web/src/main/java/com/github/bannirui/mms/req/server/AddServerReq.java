package com.github.bannirui.mms.req.server;

import lombok.Data;

@Data
public class AddServerReq {
    private String name;
    private Integer type;
    private Integer port;
}
