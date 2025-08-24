package com.github.bannirui.mms.resp.server;

import com.github.bannirui.mms.dal.model.EnvHostServerExt;
import java.util.List;
import lombok.Data;

@Data
public class ServerByTypeResp {

    private Long envId;
    private List<EnvHostServerExt> servers;

    public ServerByTypeResp() {
    }

    public ServerByTypeResp(Long envId, List<EnvHostServerExt> servers) {
        this.envId = envId;
        this.servers = servers;
    }
}
