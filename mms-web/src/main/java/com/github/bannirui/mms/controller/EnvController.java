package com.github.bannirui.mms.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.bannirui.mms.common.ResourceStatus;
import com.github.bannirui.mms.dal.mapper.EnvMapper;
import com.github.bannirui.mms.dal.mapper.HostMapper;
import com.github.bannirui.mms.dal.mapper.ServerMapper;
import com.github.bannirui.mms.dal.model.Env;
import com.github.bannirui.mms.dal.model.EnvHostServerExt;
import com.github.bannirui.mms.dal.model.Host;
import com.github.bannirui.mms.dal.model.Server;
import com.github.bannirui.mms.req.env.AddEnvReq;
import com.github.bannirui.mms.req.env.UpdateEnvReq;
import com.github.bannirui.mms.req.env.UpdateStatusReq;
import com.github.bannirui.mms.req.env.UpdateZkReq;
import com.github.bannirui.mms.resp.env.ListEnvResp;
import com.github.bannirui.mms.resp.env.ListServerResp;
import com.github.bannirui.mms.resp.host.HostResp;
import com.github.bannirui.mms.resp.server.ServerResp;
import com.github.bannirui.mms.result.Result;
import com.github.bannirui.mms.util.Assert;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "api/env")
public class EnvController {

    @Autowired
    private EnvMapper envMapper;
    @Autowired
    private HostMapper hostMapper;
    @Autowired
    private ServerMapper serverMapper;

    /**
     * 添加环境
     * @return 新增环境的id
     */
    @PostMapping(value = "/add")
    public Result<Long> add(@RequestBody AddEnvReq req) {
        boolean exists = this.envMapper.exists(new LambdaQueryWrapper<>(Env.class).eq(Env::getName, req.getName()));
        Assert.that(!exists, "环境名称重复");
        Env env = new Env();
        env.setName(req.getName());
        env.setSortId(req.getSortId());
        env.setStatus(ResourceStatus.ENABLE.getCode());
        this.envMapper.insert(env);
        return Result.success(env.getId());
    }

    /**
     * 所有的环境列表
     */
    @GetMapping(value = "/allEnv")
    public Result<List<ListEnvResp>> allEnv() {
        List<EnvHostServerExt> envs = this.envMapper.selectNotDel();
        List<ListEnvResp> ret = new ArrayList<>();
        for (EnvHostServerExt env : envs) {
            ListEnvResp e = new ListEnvResp(env.getEnvId(), env.getEnvName(), env.getEnvSortId(), env.getEnvStatus());
            if(Objects.nonNull(env.getZkId())) {
                e.setZkId(env.getZkId());
                e.setZkName(env.getZkName());
                e.setZkHost(env.getZkHost());
                e.setZkPort(env.getZkPort());
            }
            ret.add(e);
        }
        return Result.success(ret);
    }

    /**
     * 可用环境
     */
    @GetMapping(value = "/allEnableEnv")
    public Result<List<ListEnvResp>> allEnableEnv() {
        List<Env> envs = this.envMapper.selectList(new LambdaQueryWrapper<>(Env.class).eq(Env::getStatus, ResourceStatus.ENABLE.getCode()));
        List<ListEnvResp> ret = new ArrayList<>();
        for (Env env : envs) {
            ret.add(new ListEnvResp(env.getId(), env.getName(), env.getSortId(), env.getStatus()));
        }
        if (CollectionUtils.isNotEmpty(ret)) {
            ret.sort((o1, o2) -> o2.getSortId().compareTo(o1.getSortId()));
        }
        return Result.success(ret);
    }

    /**
     * 逻辑删除
     */
    @DeleteMapping(value = "/del/{id}")
    public Result<Void> del(@PathVariable Long id) {
        return this.updateStatus(id, new UpdateStatusReq() {{
            setStatus(ResourceStatus.DELETE.getCode());
        }});
    }

    @PutMapping(value = "/updateStatus/{id}")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody UpdateStatusReq req) {
        Env env = this.envMapper.selectById(id);
        Assert.that(Objects.nonNull(env), "不存在记录");
        if (Objects.equals(env.getStatus(), req.getStatus())) {
            return Result.success(null);
        }
        this.envMapper.updateById(new Env() {{
            setId(id);
            setStatus(req.getStatus());
        }});
        return Result.success(null);
    }

    @PutMapping(value = "/update/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody UpdateEnvReq req) {
        Env env = this.envMapper.selectById(id);
        Assert.that(Objects.nonNull(env), "不存在记录");
        if (Objects.equals(env.getName(), req.getName()) && Objects.equals(env.getSortId(), req.getSortId())) {
            return Result.success(null);
        }
        boolean exists = this.envMapper.exists(new LambdaQueryWrapper<>(Env.class).ne(Env::getId, id).eq(Env::getName, req.getName()));
        Assert.that(!exists, "环境名称重复");
        this.envMapper.updateById(new Env() {{
            setId(id);
            setName(req.getName());
            setSortId(req.getSortId());
        }});
        return Result.success(null);
    }

    /**
     * 可用环境下的服务
     * 环境-实例-服务
     */
    @GetMapping(value = "/listServer")
    public Result<List<ListServerResp>> listServer() {
        List<Env> envs = this.envMapper.selectList(new LambdaQueryWrapper<>(Env.class).eq(Env::getStatus, ResourceStatus.ENABLE.getCode()));
        if (CollectionUtils.isEmpty(envs)) {
            return Result.success(new ArrayList<>());
        }
        List<ListServerResp> ret = new ArrayList<>();
        Set<Long> envIds = envs.stream().map(Env::getId).collect(Collectors.toSet());
        Map<Long, List<Host>> hostGroup8Env = this.hostMapper.selectList(new LambdaQueryWrapper<>(Host.class).eq(Host::getStatus, ResourceStatus.ENABLE.getCode()).in(Host::getEnvId, envIds)).stream().collect(Collectors.groupingBy(Host::getEnvId));
        for (Env env : envs) {
            ListServerResp e = new ListServerResp();
            e.setEnvId(env.getId());
            e.setEnvName(env.getName());
            if (hostGroup8Env.containsKey(env.getId())) {
                List<Host> hosts = hostGroup8Env.get(env.getId());
                List<HostResp> retHosts = new ArrayList<>();
                for (Host host : hosts) {
                    HostResp y = new HostResp();
                    y.setId(host.getId());
                    y.setName(host.getName());
                    List<ServerResp> retServers = this.serverMapper.selectList(new LambdaQueryWrapper<>(Server.class).eq(Server::getStatus, ResourceStatus.ENABLE.getCode()).in(Server::getHostId, host.getId())).stream().map(x -> {
                        ServerResp z = new ServerResp();
                        z.setId(x.getId());
                        z.setName(x.getName());
                        return z;
                    }).toList();
                    y.setServers(retServers);
                    retHosts.add(y);
                }
                e.setHosts(retHosts);
            }
            ret.add(e);
        }
        return Result.success(ret);
    }

    /**
     * 给环境绑定/换绑zk服务 用来作为环境的元数据中心
     */
    @PutMapping(value = "/updateDataSource/{envId}")
    public Result<Void> updateZkDataSource(@PathVariable Long envId, @RequestBody UpdateZkReq req) {
        Env env = this.envMapper.selectById(envId);
        Assert.that(Objects.nonNull(env), "环境不存在");
        this.envMapper.updateById(new Env() {{
            setId(envId);
            setZkId(req.getZkId());
        }});
        return Result.success(null);
    }
}
