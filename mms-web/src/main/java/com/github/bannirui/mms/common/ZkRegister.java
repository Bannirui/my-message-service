package com.github.bannirui.mms.common;

import com.github.bannirui.mms.client.common.ConsumeFromWhere;
import com.github.bannirui.mms.metadata.ClusterMetadata;
import com.github.bannirui.mms.metadata.ConsumerGroupMetadata;
import com.github.bannirui.mms.metadata.TopicMetadata;
import com.github.bannirui.mms.service.router.ZkRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class ZkRegister {

    private static final Logger logger = LoggerFactory.getLogger(ZkRegister.class);

    @Autowired
    private ZkRouter zkRouter;

    /**
     * mq服务信息写到zk
     * @param url 连接信息ip:port 集群用,隔开ip1:port1,ip2:port2
     */
    public void registerCluster2Zk(String clusterName, String url, Integer brokerType) {
        ClusterMetadata metadata = new ClusterMetadata();
        metadata.setClusterName(clusterName);
        metadata.setBootAddr(url);
        metadata.setBrokerType(brokerType);
        this.zkRouter.writeClusterInfo(metadata);
    }
    /**
     * @param brokerType {@link HostServerType}
     */
    public void registerTopic2Zk(String clusterName, String topicName, Integer brokerType) {
        TopicMetadata metadata = new TopicMetadata();
        metadata.setClusterMetadata(new ClusterMetadata() {{
            setClusterName(clusterName);
            setBrokerType(brokerType);
        }});
        metadata.setName(topicName);
        metadata.setType(MmsType.TOPIC.getName());
        this.zkRouter.writeTopicInfo(metadata);
    }
    public void registerConsumer2Zk(String clusterName, Integer clusterType, String consumerName, String topicName, boolean supportBroadcast, boolean supportConsumeFromMin) {
        ConsumerGroupMetadata consumerMetadata = new ConsumerGroupMetadata();
        consumerMetadata.setName(consumerName);
        consumerMetadata.setType(MmsType.CONSUMER_GROUP.getName());
        consumerMetadata.setBindingTopic(topicName);
        if (supportConsumeFromMin) {
            consumerMetadata.setConsumeFrom(ConsumeFromWhere.EARLIEST.getName());
        } else {
            consumerMetadata.setConsumeFrom(ConsumeFromWhere.LATEST.getName());
        }
        if (supportBroadcast) {
            consumerMetadata.setBroadcast("true");
        } else {
            consumerMetadata.setBroadcast("false");
        }
        ClusterMetadata clusterMetadata = new ClusterMetadata();
        clusterMetadata.setClusterName(clusterName);
        consumerMetadata.setClusterMetadata(clusterMetadata);
        clusterMetadata.setBrokerType(clusterType);
        this.zkRouter.writeConsumerInfo(consumerMetadata);
    }

    /**
     * 探测zk连接是否还存活
     */
    public boolean alive() {
        return this.zkRouter.currentZkClient().getState().isAlive();
    }
}
