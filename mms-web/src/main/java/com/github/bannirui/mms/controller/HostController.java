package com.github.bannirui.mms.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.bannirui.mms.common.ResourceStatus;
import com.github.bannirui.mms.dal.mapper.EnvMapper;
import com.github.bannirui.mms.dal.mapper.HostMapper;
import com.github.bannirui.mms.dal.model.Env;
import com.github.bannirui.mms.dal.model.Host;
import com.github.bannirui.mms.req.host.AddHostReq;
import com.github.bannirui.mms.result.Result;
import com.github.bannirui.mms.util.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "api/host")
public class HostController {

    @Autowired
    private HostMapper hostMapper;
    @Autowired
    private EnvMapper envMapper;

    /**
     * 新增
     * @return 数据库id
     */
    @PostMapping(value = "/add/{envId}")
    public Result<Long> add(@PathVariable Long envId, @RequestBody AddHostReq req) {
        boolean envExists = this.envMapper.exists(new LambdaQueryWrapper<>(Env.class).eq(Env::getId, envId));
        Assert.that(envExists, "环境不存在");
        boolean hostExists = this.hostMapper.exists(new LambdaQueryWrapper<>(Host.class).eq(Host::getEnvId, envId).eq(Host::getEnvId, req.getHost()));
        Assert.that(!hostExists, "Host已经存在了");
        Host host = new Host();
        host.setName(req.getName());
        host.setEnvId(envId);
        host.setHost(req.getHost());
        host.setStatus(ResourceStatus.ENABLE.getCode());
        int insert = this.hostMapper.insert(host);
        return Result.success(host.getId());
    }
}
