package com.github.bannirui.mms.metadata;

import com.github.bannirui.mms.common.HostServerType;
import lombok.Data;

/**
 * 存在注册中心的mq集群信息
 */
@Data
public class ClusterMetadata {
    private String clusterName;
    /**
     * <ul>
     *     <li>RocketMQ name server, host:9876</li>
     * </ul>
     */
    private String bootAddr;
    /**
     * mq中间件类型
     * {@link HostServerType}
     */
    private Integer brokerType;
}
