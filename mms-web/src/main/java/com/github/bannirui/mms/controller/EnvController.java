package com.github.bannirui.mms.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.bannirui.mms.common.EnvStatus;
import com.github.bannirui.mms.dal.mapper.EnvMapper;
import com.github.bannirui.mms.dal.model.Env;
import com.github.bannirui.mms.req.env.AddEnvReq;
import com.github.bannirui.mms.req.env.UpdateEnvReq;
import com.github.bannirui.mms.req.env.UpdateStatusReq;
import com.github.bannirui.mms.resp.env.ListEnvResp;
import com.github.bannirui.mms.result.Result;
import com.github.bannirui.mms.util.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping(path = "api/env")
public class EnvController {

    @Autowired
    private EnvMapper envMapper;

    /**
     * 新增
     */
    @PostMapping(value = "/add", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<Integer> add(@RequestBody AddEnvReq req) {
        boolean exists = this.envMapper.exists(new LambdaQueryWrapper<>(Env.class).eq(Env::getName, req.getName()));
        Assert.that(!exists, "环境名称重复");
        Env env = new Env();
        env.setName(req.getName());
        env.setSortId(req.getSortId());
        env.setStatus(EnvStatus.CREATE_NEW.getCode());
        int insert = this.envMapper.insert(env);
        return Result.success(insert);
    }

    /**
     * 所有的环境列表
     */
    @GetMapping(value = "/allEnv")
    public Result<List<ListEnvResp>> allEnv() {
        List<Env> envs = this.envMapper.selectList(new LambdaQueryWrapper<>(Env.class).ne(Env::getStatus, EnvStatus.DELETE.getCode()));
        List<ListEnvResp> ret = new ArrayList<>();
        for (Env env : envs) {
            ret.add(new ListEnvResp(env.getId(), env.getName(), env.getSortId(), env.getStatus()));
        }
        return Result.success(ret);
    }

    /**
     * 逻辑删除
     */
    @DeleteMapping(value = "/del/{id}")
    public Result<Void> del(@PathVariable Long id) {
        return this.updateStatus(id, new UpdateStatusReq() {{
            setStatus(EnvStatus.DELETE.getCode());
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
        boolean exists = this.envMapper.exists(new LambdaQueryWrapper<>(Env.class).eq(Env::getName, req.getName()));
        Assert.that(!exists, "环境名称重复");
        this.envMapper.updateById(new Env() {{
            setId(id);
            setName(req.getName());
            setSortId(req.getSortId());
        }});
        return Result.success(null);
    }
}
