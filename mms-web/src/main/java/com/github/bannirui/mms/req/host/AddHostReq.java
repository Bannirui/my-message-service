package com.github.bannirui.mms.req.host;

import lombok.Data;

@Data
public class AddHostReq {
    private String name;
    private String host;
    private Integer port;
    private Long envId;
}
