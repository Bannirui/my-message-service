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
}
