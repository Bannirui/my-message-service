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

    @Override
    public String toString() {
        return "MmsMetadata{type='" + this.type + '\'' + ", name='" + this.name + '\'' + ", clusterMetadata=" + this.clusterMetadata.toString() + ", domain='" + this.domain + '\'' + ", gatedCluster='" + (this.gatedCluster != null ? this.gatedCluster.toString() : "") + '\'' + ", gatedIps='" + this.gatedIps + '\'' + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            MmsMetadata that = (MmsMetadata) o;
            return Objects.equals(this.type, that.type) && Objects.equals(this.name, that.name) && Objects.equals(this.clusterMetadata, that.clusterMetadata) && Objects.equals(this.domain, that.domain) && Objects.equals(this.gatedIps, that.gatedIps) && Objects.equals(this.gatedCluster, that.gatedCluster) && Objects.equals(this.isEncrypt, that.isEncrypt);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(new Object[]{this.type, this.name, this.clusterMetadata, this.domain, this.gatedIps, this.gatedCluster, this.isEncrypt});
    }
}
