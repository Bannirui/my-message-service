package com.github.bannirui.mms.req.consumer;

import lombok.Data;

@Data
public class ConsumerPageReq {
    private Integer page;
    private Integer size;
    private String consumerName;
    private Long userId;
}

