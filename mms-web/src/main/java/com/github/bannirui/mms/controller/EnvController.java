package com.github.bannirui.mms.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.bannirui.mms.common.ResourceStatus;
import com.github.bannirui.mms.dal.mapper.EnvMapper;
import com.github.bannirui.mms.dal.mapper.HostMapper;
import com.github.bannirui.mms.dal.mapper.ServerMapper;
import com.github.bannirui.mms.dal.model.Env;
import com.github.bannirui.mms.dal.model.EnvHostServerExt;
import com.github.bannirui.mms.req.env.AddEnvReq;
import com.github.bannirui.mms.req.env.UpdateEnvReq;
import com.github.bannirui.mms.req.env.UpdateStatusReq;
import com.github.bannirui.mms.req.env.UpdateZkReq;
import com.github.bannirui.mms.resp.env.ListEnvResp;
import com.github.bannirui.mms.resp.env.ListServerResp;
import com.github.bannirui.mms.resp.host.HostResp;
import com.github.bannirui.mms.resp.server.ServerResp;
import com.github.bannirui.mms.result.Result;
import com.github.bannirui.mms.service.env.EnvDatasourceService;
import com.github.bannirui.mms.util.Assert;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "api/env")
public class EnvController {

    @Autowired
    private EnvMapper envMapper;
    @Autowired
    private EnvDatasourceService envDatasourceService;

    /**
     * 添加环境
     *
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
            if (Objects.nonNull(env.getZkId())) {
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
        List<Env> envs = this.envMapper.selectList(new LambdaQueryWrapper<>(Env.class)
                .in(Env::getStatus, ResourceStatus.ENABLE.getCode(), ResourceStatus.CREATE_APPROVED.getCode(), ResourceStatus.UPDATE_APPROVED));
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
        List<EnvHostServerExt> envExts = this.envMapper.selectEnvExtEnable();
        if (CollectionUtils.isEmpty(envExts)) {
            return Result.success(new ArrayList<>());
        }
        // key is envId
        Map<Long, ListServerResp> envMap = new HashMap<>();
        for (EnvHostServerExt x : envExts) {
            // env
            ListServerResp env = envMap.computeIfAbsent(x.getEnvId(), id -> {
                ListServerResp e = new ListServerResp();
                e.setEnvId(x.getEnvId());
                e.setEnvName(x.getEnvName());
                e.setHosts(new ArrayList<>());
                return e;
            });
            if (Objects.isNull(x.getHostId())) {
                continue;
            }
            // host
            Map<Long, HostResp> hostMap = env.getHosts().stream()
                .collect(Collectors.toMap(HostResp::getId, h -> h, (a, b) -> a));
            HostResp host = hostMap.computeIfAbsent(x.getHostId(), id -> {
                HostResp h = new HostResp();
                h.setId(x.getHostId());
                h.setName(x.getHostName());
                h.setServers(new ArrayList<>());
                env.getHosts().add(h);
                return h;
            });
            // server
            if (Objects.nonNull(x.getServerId())) {
                ServerResp server = new ServerResp();
                server.setId(x.getServerId());
                server.setName(x.getServerName());
                host.getServers().add(server);
            }
        }
        List<ListServerResp> ret = envMap.values().stream().toList();
        return Result.success(ret);
    }

    /**
     * 给环境绑定/换绑zk服务 用来作为环境的元数据中心
     */
    @PutMapping(value = "/updateDataSource/{envId}")
    public Result<Void> updateZkDataSource(@PathVariable Long envId, @RequestBody UpdateZkReq req) {
        Assert.that(Objects.nonNull(req.getZkId()), "zk必填");
        Env env = this.envMapper.selectById(envId);
        Assert.that(Objects.nonNull(env), "环境不存在");
        if (Objects.equals(env.getZkId(), req.getZkId())) {
            return Result.success(null);
        }
        this.envMapper.updateById(new Env() {{
            setId(envId);
            setZkId(req.getZkId());
        }});
        // 绑定之后更新zk
        this.envDatasourceService.reloadEnvZkClient(env.getId());
        // todo 重新注册元数据到zk
        return Result.success(null);
    }
}
