package com.github.bannirui.mms.req;

import lombok.Data;

import java.util.List;

@Data
public class ApplyTopicReq {

    /**
     * 申请人
     */
    private Long userId;
    /**
     * @see com.github.bannirui.mms.dal.model.Topic#name
     */
    private String name;
    /**
     * 给哪个应用服务的
     */
    private Long appId;
    /**
     * 发送速度
     * 条/秒
     */
    private Integer tps;
    /**
     * 消息体大小
     * 字节
     */
    private Integer msgSz;
    /**
     * 集群类型
     * {@link com.github.bannirui.mms.common.BrokerType}
     */
    private Integer clusterType;
    /**
     * 需要申请哪些环境的topic
     */
    private List<TopicEnvInfo> envs;
    private String remark;

    @Data
    public static class TopicEnvInfo {
        /**
         * @see com.github.bannirui.mms.dal.model.Env#id
         */
        private Long envId;
    }
}

