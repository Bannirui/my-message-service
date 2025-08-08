package com.github.bannirui.mms.req;

import lombok.Data;

import java.util.List;

@Data
public class ApplyTopicReq {

    /**
     * 申请人
     */
    protected Long userId;
    /**
     * @see com.github.bannirui.mms.dal.model.Topic#name
     */
    protected String name;
    /**
     * 给哪个应用服务的
     */
    protected Long appId;
    /**
     * 发送速度
     * 条/秒
     */
    protected Integer tps;
    /**
     * 消息体大小
     * 字节
     */
    protected Integer msgSz;

    /**
     * 需要申请哪些环境的topic
     */
    protected List<TopicEnvInfo> envs;

    @Data
    public static class TopicEnvInfo {
        /**
         * @see com.github.bannirui.mms.dal.model.Env#id
         */
        protected Long envId;
    }
}

