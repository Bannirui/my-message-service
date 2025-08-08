package com.github.bannirui.mms.service.manager;

import com.github.bannirui.mms.common.MmsConst;
import com.github.bannirui.mms.metadata.ClusterMetadata;
import com.github.bannirui.mms.zookeeper.MmsZkClient;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public abstract class AbstractMessageMiddlewareProcessor implements MiddlewareProcess {

    private static final Logger logger = LoggerFactory.getLogger(AbstractMessageMiddlewareProcessor.class);

    public interface RollBack {
        /**
         * 移除消息管理类
         */
        void destroy();
    }

    protected final MmsZkClient zkClient;
    protected final ClusterMetadata clusterMetadata;
    protected final String clusterName;
    protected final RollBack rollback;

    protected AbstractMessageMiddlewareProcessor(MmsZkClient zkClient, ClusterMetadata clusterMetadata, RollBack rollback) {
        this.zkClient = zkClient;
        this.clusterMetadata = clusterMetadata;
        this.clusterName = clusterMetadata.getClusterName();
        this.rollback = rollback;
        this.init();
    }

    private void init() {
        this.create();
        //监听节点修改
        this.nodeChangeListener();
    }

    protected abstract void create();

    protected abstract void destroy();

    private void nodeChangeListener() {
        String targetPath = String.join("/", MmsConst.ZK.CLUSTER_ZKPATH, this.clusterName);
        this.zkClient.register(watchedEvent -> {
            String path = watchedEvent.getPath();
            if (Objects.isNull(path) || !path.startsWith(targetPath)) {
                return;
            }
            Watcher.Event.EventType type = watchedEvent.getType();
            switch (type) {
                case NodeDataChanged -> {
                    logger.info("Zookeeper node data change");
                    this.destroy();
                }
                case NodeDeleted -> {
                    logger.info("Zookeeper node data delete");
                    this.destroy();
                }
                default -> logger.debug("Unhandled Zookeeper event: {}", watchedEvent.getType());
            }
        });
    }
}
