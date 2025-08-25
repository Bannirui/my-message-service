package com.github.bannirui.mms.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.bannirui.mms.common.HostServerType;
import com.github.bannirui.mms.common.ResourceStatus;
import com.github.bannirui.mms.common.ZkRegister;
import com.github.bannirui.mms.dal.mapper.HostMapper;
import com.github.bannirui.mms.dal.mapper.ServerMapper;
import com.github.bannirui.mms.dal.model.EnvHostServerExt;
import com.github.bannirui.mms.dal.model.Server;
import com.github.bannirui.mms.metadata.ClusterMetadata;
import com.github.bannirui.mms.req.server.AddServerReq;
import com.github.bannirui.mms.resp.server.ServerByTypeResp;
import com.github.bannirui.mms.result.Result;
import com.github.bannirui.mms.service.manager.MmsContextManager;
import com.github.bannirui.mms.service.router.ZkRouter;
import com.github.bannirui.mms.util.Assert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(path = "api/server")
public class ServerController {

    @Autowired
    private HostMapper hostMapper;
    @Autowired
    private ServerMapper serverMapper;
    @Autowired
    private ZkRegister zkRegister;

    /**
     * 新增
     * 把新增的服务元数据注册到zk上
     *
     * @param hostId 服务在哪个机器上
     * @return 数据库id
     */
    @PostMapping(value = "/add/{hostId}")
    public Result<Long> add(@PathVariable Long hostId, @RequestBody AddServerReq req) {
        EnvHostServerExt hostExt = this.hostMapper.hostExtEnvAndZkByHostId(hostId);
        Assert.that(Objects.nonNull(hostExt), "主机不存在");
        boolean serverExists =
                this.serverMapper.exists(new LambdaQueryWrapper<>(Server.class).eq(Server::getHostId, hostId).eq(Server::getPort, req.getPort()));
        if (serverExists) {
            return Result.success(null);
        }
        // 添加的是zk服务
        Integer serverType = req.getType();
        if (!HostServerType.isZk(serverType)) {
            // 确保环境里面绑定了zk 服务服务添加完要写到zk
            Long zk = hostExt.getZkId();
            Assert.that(Objects.nonNull(zk) && zk > 0, "给主机添加服务之前确保已经给当前环境绑定了zk注册中心");
        }
        Server server = new Server();
        server.setHostId(hostId);
        server.setName(req.getName());
        server.setType(serverType);
        server.setPort(req.getPort());
        server.setStatus(ResourceStatus.ENABLE.getCode());
        this.serverMapper.insert(server);
        // 只把mq的服务元数据信息写到zk里面
        if (HostServerType.isMQ(serverType)) {
            // 下面要拿zk注册中心 指定环境
            MmsContextManager.setEnv(hostExt.getEnvId());
            log.info("添加的服务是mq 元数据写到zk里面 当前环境是{}", hostExt.getEnvName());
            this.zkRegister.registerCluster2Zk(req.getName(), hostExt.getHostHost()+":"+req.getPort(), serverType);
        }
        return Result.success(server.getId());
    }

    @GetMapping(value = "/{serverType}")
    public Result<List<ServerByTypeResp>> getServer8Type(@PathVariable Integer serverType) {
        List<EnvHostServerExt> servers = this.serverMapper.serverExtByServerType(serverType);
        List<ServerByTypeResp> ret = new ArrayList<>();
        if (CollectionUtils.isEmpty(servers)) {
            return Result.success(ret);
        }
        // 环境分组
        Map<Long, List<EnvHostServerExt>> map = servers.stream().collect(Collectors.groupingBy(EnvHostServerExt::getEnvId));
        map.forEach((envId, serverList) -> {
            ret.add(new ServerByTypeResp(envId, serverList));
        });
        return Result.success(ret);
    }
}
