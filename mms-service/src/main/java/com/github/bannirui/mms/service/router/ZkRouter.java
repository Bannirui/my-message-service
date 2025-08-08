package com.github.bannirui.mms.service.router;

import com.github.bannirui.mms.metadata.TopicMetadata;
import com.github.bannirui.mms.service.selector.ZkSelector;
import com.github.bannirui.mms.util.Assert;
import com.github.bannirui.mms.zookeeper.MmsZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class ZkRouter {

    private static final Logger logger = LoggerFactory.getLogger(ZkRouter.class);

    @Autowired(required = false)
    private ZkSelector zkSelector;

    public MmsZkClient currentZkClient() {
        MmsZkClient zkClient = this.zkSelector.select();
        Assert.that(Objects.nonNull(zkClient), "当前环境没有配置zookeeper数据源");
        return zkClient;
    }

    public void writeTopicInfo(TopicMetadata metadata) {
        this.currentZkClient().writeTopicMetadata(metadata);
    }
}
