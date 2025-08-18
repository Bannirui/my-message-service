package com.github.bannirui.mms.resp.env;

import com.github.bannirui.mms.resp.host.HostResp;
import lombok.Data;

import java.util.List;

@Data
public class ListServerResp {
    private Long envId;
    private String envName;
    List<HostResp> hosts;
}
