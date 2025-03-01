package com.github.bannirui.mms.client;

import com.github.bannirui.mms.client.metrics.MmsMetrics;
import com.github.bannirui.mms.common.MmsConst;
import com.github.bannirui.mms.common.MmsType;
import com.github.bannirui.mms.logger.MmsLogger;
import com.github.bannirui.mms.metadata.MmsMetadata;
import com.github.bannirui.mms.zookeeper.MmsZkClient;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import org.apache.zookeeper.AddWatchMode;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;

public abstract class MmsProxy<K extends MmsMetrics> implements MmsService {

    protected MmsMetadata metadata;

    public String instanceName;

    protected K mmsMetrics;

    private String proxyName;

    protected boolean isOrderly;

    /**
     * zk监听器
     */
    Watcher zkDataListener = event -> {
        MmsMetadata newMetadata = null;
        if (MmsProxy.this.metadata.getType().equals(MmsType.TOPIC.getName())) {
            newMetadata = MmsProxy.this.getZkInstance().readTopicMetadata(MmsProxy.this.metadata.getName());
        } else {
            newMetadata = MmsProxy.this.getZkInstance().readConsumerGroupMetadata(MmsProxy.this.metadata.getName());
        }
        if(Objects.isNull(newMetadata)) {
            return;
        }
        MmsLogger.log.info("metadata {} change notified", newMetadata.toString());
        if (!MmsProxy.this.metadata.getClusterMetadata().getBrokerType().equals(((MmsMetadata)newMetadata).getClusterMetadata().getBrokerType())) {
            MmsLogger.log.error("BrokerType can't be change for topic or consumergroup when running");
        } else if (MmsProxy.this.metadata.equals(newMetadata)) {
            MmsLogger.log.info("ignore the change, for it's the same with before");
        } else {
            MmsMetadata oldMetadata = MmsProxy.this.metadata;
            MmsProxy.this.metadata = newMetadata;
            if (MmsProxy.this.changeConfigAndRestart(oldMetadata, newMetadata)) {
                MmsLogger.log.info("{} metadata change notify client restart", newMetadata.getName());
                MmsProxy.this.restart();
            }
        }
    };

    public MmsProxy(MmsMetadata metadata, boolean isOrderly, K metrics) {
        this.metadata = metadata;
        this.isOrderly = isOrderly;
        mmsMetrics = metrics;
    }

    public MmsZkClient getZkInstance() {
        return MmsZkClient.getInstance();
    }

    public void restart() {
    }

    private void registerWatcher() {
        try {
            this.getZkInstance().addWatch(this.metadata.getMmsPath(), this.zkDataListener, AddWatchMode.PERSISTENT);
        } catch (KeeperException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void registerName() {
        // 临时节点
        if (!this.isStatistic(this.metadata.getName())) {
            this.proxyName = String.join("/", this.metadata.getMmsPath(), MmsConst.MMS_IP + "||" + instanceName + "||" + MmsConst.MMS_VERSION + "||" + LocalDateTime.now() + "||" + ThreadLocalRandom.current().nextInt(100_000));
            try {
                this.getZkInstance().create(this.proxyName, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            } catch (KeeperException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void unregisterWatcher() {
        try {
            this.getZkInstance().removeWatches(this.metadata.getMmsClusterPath(), this.zkDataListener, Watcher.WatcherType.Any, true);
            this.getZkInstance().removeWatches(this.metadata.getMmsPath(), this.zkDataListener, Watcher.WatcherType.Any, true);
        } catch (InterruptedException | KeeperException e) {
            throw new RuntimeException(e);
        }
    }

    protected boolean isStatistic(String name) {
        return MmsConst.STATISTICS.PING_CONSUMER_NAME.equalsIgnoreCase(name) ||
            MmsConst.STATISTICS.PING_TOPIC_NAME.equalsIgnoreCase(name) ||
            MmsConst.STATISTICS.STATISTICS_CONSUMER_CONSUMERINFO.equalsIgnoreCase(name) ||
            MmsConst.STATISTICS.STATISTICS_CONSUMER_KAFKA_CONSUMERINFO.equalsIgnoreCase(name) ||
            MmsConst.STATISTICS.STATISTICS_CONSUMER_KAFKA_PRODUCERINFO.equalsIgnoreCase(name) ||
            MmsConst.STATISTICS.STATISTICS_CONSUMER_KAFKA_PRODUCERINFO.equalsIgnoreCase(name) ||
            MmsConst.STATISTICS.STATISTICS_CONSUMER_PRODUCERINFO.equalsIgnoreCase(name) ||
            MmsConst.STATISTICS.STATISTICS_TOPIC_CONSUMERINFO.equalsIgnoreCase(name) ||
            MmsConst.STATISTICS.STATISTICS_TOPIC_KAFKA_CONSUMERINFO.equalsIgnoreCase(name) ||
            MmsConst.STATISTICS.STATISTICS_TOPIC_KAFKA_PRODUCERINFO.equalsIgnoreCase(name) ||
            MmsConst.STATISTICS.STATISTICS_TOPIC_PRODUCERINFO.equalsIgnoreCase(name);
    }

    private void unregisterName() {
        if (!this.isStatistic(this.metadata.getName())) {
            try {
                this.getZkInstance().delete(this.proxyName, -1);
            } catch (InterruptedException | KeeperException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public abstract boolean changeConfigAndRestart(MmsMetadata oldMetadata, MmsMetadata newMetadata);

    @Override
    public void start() {
        registerWatcher();
        registerName();
        running = true;
    }

    @Override
    public void shutdown() {
        running = false;
        unregisterName();
        unregisterWatcher();
    }

    public MmsMetadata getMetadata() {
        return metadata;
    }

    protected volatile boolean running;

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
}

