package com.github.bannirui.mms.resp.host;

import com.github.bannirui.mms.resp.server.ServerResp;
import lombok.Data;

import java.util.List;

@Data
public class HostResp {
    private Long id;
    private String name;
    private List<ServerResp> servers;
}
