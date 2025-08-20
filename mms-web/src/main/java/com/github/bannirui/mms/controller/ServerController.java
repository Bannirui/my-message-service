package com.github.bannirui.mms.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.bannirui.mms.common.ResourceStatus;
import com.github.bannirui.mms.dal.mapper.EnvMapper;
import com.github.bannirui.mms.dal.mapper.HostMapper;
import com.github.bannirui.mms.dal.mapper.ServerMapper;
import com.github.bannirui.mms.dal.model.Env;
import com.github.bannirui.mms.dal.model.Host;
import com.github.bannirui.mms.dal.model.Server;
import com.github.bannirui.mms.req.host.AddHostReq;
import com.github.bannirui.mms.req.server.AddServerReq;
import com.github.bannirui.mms.result.Result;
import com.github.bannirui.mms.util.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "api/server")
public class ServerController {

    @Autowired
    private HostMapper hostMapper;
    @Autowired
    private ServerMapper serverMapper;

    /**
     * 新增
     * @return 数据库id
     */
    @PostMapping(value = "/add/{hostId}")
    public Result<Long> add(@PathVariable Long hostId, @RequestBody AddServerReq req) {
        boolean hostExists = this.hostMapper.exists(new LambdaQueryWrapper<>(Host.class).eq(Host::getId, hostId));
        Assert.that(hostExists, "主机不存在");
        boolean serverExists = this.serverMapper.exists(new LambdaQueryWrapper<>(Server.class).eq(Server::getHostId, hostId).eq(Server::getPort, req.getPort()));
        Assert.that(!serverExists, "Server已经存在了");
        Server server = new Server();
        server.setHostId(hostId);
        server.setName(req.getName());
        server.setPort(req.getPort());
        server.setPort(req.getPort());
        server.setStatus(ResourceStatus.ENABLE.getCode());
        int insert = this.serverMapper.insert(server);
        return Result.success(server.getId());
    }
}
