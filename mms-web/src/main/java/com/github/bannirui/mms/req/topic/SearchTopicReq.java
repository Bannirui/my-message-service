package com.github.bannirui.mms.req.topic;

import lombok.Data;

@Data
public class SearchTopicReq {
    /**
     * 根据name模糊搜索
     */
    private String topicName;
}

