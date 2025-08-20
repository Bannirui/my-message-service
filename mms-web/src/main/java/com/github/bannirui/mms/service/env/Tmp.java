package com.github.bannirui.mms.service.env;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.bannirui.mms.common.HostServerType;
import com.github.bannirui.mms.common.ServerStatus;
import com.github.bannirui.mms.dal.mapper.HostMapper;
import com.github.bannirui.mms.dal.mapper.ServerMapper;
import com.github.bannirui.mms.dal.model.Host;
import com.github.bannirui.mms.dal.model.Server;
import com.github.bannirui.mms.metadata.ClusterMetadata;
import com.github.bannirui.mms.zookeeper.MmsZkClient;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class Tmp {

    @Autowired
    private HostMapper hostMapper;
    @Autowired
    ServerMapper serverMapper;

    @PostConstruct
    public void init() throws IOException {
        // todo mq集群信息需要自动写到zk 现在手动写一份模拟
        MmsZkClient zkClient = new MmsZkClient("127.0.0.1:2181", 20_000, null);
        List<Server> mqServers = this.serverMapper.selectList(new LambdaQueryWrapper<Server>()
                .eq(Server::getStatus, ServerStatus.ENABLE.getCode())
                .in(Server::getType, HostServerType.KAFKA.getCode(), HostServerType.ROCKETMQ.getCode())
        );
        if (CollectionUtils.isEmpty(mqServers)) {
            return;
        }
        Set<Long> hostIds = mqServers.stream().map(Server::getHostId).collect(Collectors.toSet());
        Map<Long, Host> host8Id = this.hostMapper.selectBatchIds(hostIds).stream().collect(Collectors.toMap(Host::getId, x -> x, (x1, x2) -> x2));
        for (Server server : mqServers) {
            ClusterMetadata cluster = new ClusterMetadata();
            cluster.setClusterName(server.getName());
            cluster.setBootAddr(host8Id.get(server.getHostId()) + ":" + server.getPort());
            cluster.setBrokerType(HostServerType.ROCKETMQ.getCode());
            zkClient.writeClusterMetadata(cluster);
        }
    }
}
