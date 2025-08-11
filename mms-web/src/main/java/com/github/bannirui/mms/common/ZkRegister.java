package com.github.bannirui.mms.common;

import com.github.bannirui.mms.metadata.ClusterMetadata;
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
     * @param brokerType {@link BrokerType}
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
}
