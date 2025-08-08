package com.github.bannirui.mms.req;

import lombok.Data;

import java.util.List;

@Data
public class ApproveTopicReq extends ApplyTopicReq {

    /**
     * {@link com.github.bannirui.mms.dal.model.Topic#id}
     */
    private Long topicId;
    /**
     * 审核员给topic分配的分区数
     */
    private Integer partitions;
    /**
     * 审核员给topic分配的副本数
     */
    private Integer replication;
    /**
     * 用户可能申请了多个环境
     * 审核员为每个环境分配一个集群
     */
    List<TopicEnvServerInfo> envServers;

    @Data
    public static class TopicEnvServerInfo extends TopicEnvInfo {
        /**
         * 集群id
         * {@link com.github.bannirui.mms.dal.model.Server#id}
         */
        private Long serverId;
    }
}

