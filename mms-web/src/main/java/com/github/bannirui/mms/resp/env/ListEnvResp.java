package com.github.bannirui.mms.resp.env;

import lombok.Data;

@Data
public class ListEnvResp {
    private Long id;
    private String name;
    private Integer sortId;
    private Integer status;

    private Long zkId;
    private String zkName;
    private String zkHost;
    private Integer zkPort;

    public ListEnvResp() {
    }

    public ListEnvResp(Long id, String name, Integer sortId, Integer status) {
        this.id = id;
        this.name = name;
        this.sortId = sortId;
        this.status = status;
    }
}
