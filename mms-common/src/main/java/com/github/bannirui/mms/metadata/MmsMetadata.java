package com.github.bannirui.mms.metadata;

import com.github.bannirui.mms.common.MmsConst;
import com.github.bannirui.mms.common.MmsType;
import com.github.bannirui.mms.zookeeper.MmsZkClient;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * 注册中心存储的my message service的元数据
 * <ul>
 *     <li>topic</li>
 *     <li>consumer group</li>
 * </ul>
 */
@Data
public class MmsMetadata {

    /**
     * {@link MmsType}
     * topic还是consumer group
     */
    private String type;
    /**
     * topic的名称作为zk节点名称
     * <ul>
     *     <li>/mms/topic/${name}</li>
     *     <li>/mms/consumergroup/${name}</li>
     * </ul>
     */
    private String name;
    private ClusterMetadata clusterMetadata;
    private String domain;
    private String gatedIps;
    private ClusterMetadata gatedCluster;
    private String statisticsLogger;
    private Boolean isEncrypt;

    public String getMmsClusterPath() {
        return MmsZkClient.buildPath(MmsConst.ZK.CLUSTER_ZKPATH, this.clusterMetadata.getClusterName());
    }

    public String getMmsPath() {
        return this.isTopic() ? MmsZkClient.buildPath(MmsConst.ZK.TOPIC_ZKPATH, this.name) : MmsZkClient.buildPath(MmsConst.ZK.CONSUMERGROUP_ZKPATH, this.name);
    }

    public boolean isTopic() {
        return MmsType.TOPIC.getName().equalsIgnoreCase(this.type);
    }

    public boolean isGatedLaunch() {
        return StringUtils.isNotBlank(this.gatedIps) && this.gatedIps.contains(MmsConst.MMS_IP);
    }
}
