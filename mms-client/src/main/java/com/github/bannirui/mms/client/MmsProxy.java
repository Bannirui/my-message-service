package com.github.bannirui.mms.client;

import com.github.bannirui.mms.client.metrics.MmsMetrics;
import com.github.bannirui.mms.common.MmsConst;
import com.github.bannirui.mms.common.MmsException;
import com.github.bannirui.mms.common.MmsType;
import com.github.bannirui.mms.logger.MmsLogger;
import com.github.bannirui.mms.metadata.MmsMetadata;
import com.github.bannirui.mms.zookeeper.MmsZkClient;
import org.apache.zookeeper.*;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * mms的代理
 * mms屏蔽了mq的类型 因此提供一个对mms服务的代理
 *
 * @param <K>
 */
public abstract class MmsProxy<K extends MmsMetrics> implements LifeCycle {

    protected MmsMetadata metadata;

    /**
     * mq服务的代理对象
     * <ul>
     *     <li>可能是生产者</li>
     *     <li>可能是消费者</li>
     * </ul>
     * 给生产者代理对象或者消费者代理对象起的名字
     */
    public String instanceName;

    protected K mmsMetrics;

    private String proxyName;

    protected boolean isOrderly;

    /**
     * zk监听器
     */
    Watcher zkDataListener = event -> {
        MmsMetadata newMetadata = null;
        if (Objects.equals(MmsProxy.this.metadata.getType(), MmsType.TOPIC.getName())) {
            newMetadata = MmsProxy.this.getZkInstance().readTopicMetadata(MmsProxy.this.metadata.getName());
        } else if (Objects.equals(MmsProxy.this.metadata.getType(), MmsType.CONSUMER_GROUP.getName())) {
            newMetadata = MmsProxy.this.getZkInstance().readConsumerGroupMetadata(MmsProxy.this.metadata.getName());
        } else {
            throw new MmsException("mq类型未知");
        }
        if (Objects.isNull(newMetadata)) {
            return;
        }
        if (!Objects.equals(MmsProxy.this.metadata.getClusterMetadata().getBrokerType(), newMetadata.getClusterMetadata().getBrokerType())) {
            MmsLogger.log.info("zk中mq元数据 mq类型发生了变化 当前的元数据是{}", newMetadata);
        } else if (MmsProxy.this.metadata.equals(newMetadata)) {
            // ignore
        } else {
            MmsMetadata oldMetadata = MmsProxy.this.metadata;
            MmsProxy.this.metadata = newMetadata;
            if (MmsProxy.this.changeConfigAndRestart(oldMetadata, newMetadata)) {
                MmsLogger.log.info("zk中注册的mq元数据发生了变化 让{}根据最新的mq信息{}进行重启", newMetadata.getName(), newMetadata);
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
                MmsConst.STATISTICS.STATISTICS_CONSUMER_PRODUCER_INFO.equalsIgnoreCase(name) ||
                MmsConst.STATISTICS.STATISTICS_CONSUMER_CONSUMER_INFO.equalsIgnoreCase(name) ||
                MmsConst.STATISTICS.STATISTICS_TOPIC_PRODUCER_INFO.equalsIgnoreCase(name) ||
                MmsConst.STATISTICS.STATISTICS_TOPIC_CONSUMER_INFO.equalsIgnoreCase(name)
                ;
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

