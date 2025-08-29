package com.github.bannirui.mms.req.consumer;

import lombok.Data;

@Data
public class ApplyConsumerReq {

    /**
     * 消费组名
     */
    private String name;
    /**
     * 申请人
     */
    private Long userId;
    /**
     * 给哪个应用服务的
     */
    private Long appId;
    /**
     * 订阅哪个topic
     */
    private Long topicId;
    private String remark;
    /**
     * 广播消费
     * 只有rocketmq有这个概念 kafka没有这个概念
     * 申请消费组时告诉broker是不是要支持广播消费
     */
    private boolean consumerBroadcast = false;
    /**
     * 最早消费
     * 只有rocketmq有这个概念 kafka没有这个概念
     * 申请消费组时告诉broker是不是要支持最早消费
     */
    private boolean consumerFromMin = false;
}
