package com.github.bannirui.mms.service.env;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.bannirui.mms.common.BrokerType;
import com.github.bannirui.mms.common.ServerStatus;
import com.github.bannirui.mms.dal.mapper.ServerMapper;
import com.github.bannirui.mms.dal.model.Server;
import com.github.bannirui.mms.metadata.ClusterMetadata;
import com.github.bannirui.mms.zookeeper.MmsZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;

@Component
public class Tmp {

    @Autowired
    ServerMapper serverMapper;

    @PostConstruct
    public void init() throws IOException {
        // todo 集群信息需要自动写到zk 现在手动写一份模拟
        MmsZkClient zkClient = new MmsZkClient("127.0.0.1:2181", 20_000, null);
        List<Server> servers = this.serverMapper.selectList(new LambdaQueryWrapper<Server>().eq(Server::getStatus, ServerStatus.ENABLE.getCode()));
        for (Server server : servers) {
            ClusterMetadata cluster = new ClusterMetadata();
            cluster.setClusterName(server.getName());
            cluster.setBootAddr(server.getAddress());
            cluster.setBrokerType(BrokerType.ROCKETMQ.getCode());
            zkClient.writeClusterMetadata(cluster);
        }
    }
}
