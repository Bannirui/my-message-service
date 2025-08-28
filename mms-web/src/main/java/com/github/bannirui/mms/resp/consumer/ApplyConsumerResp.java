package com.github.bannirui.mms.resp.consumer;

import com.github.bannirui.mms.dto.topic.EnvExtDTO;
import lombok.Data;

import java.util.List;

@Data
public class ApplyConsumerResp {
    // 数据库id
    private Long consumerId;
    // consumer监听的topic有哪些环境 等创建好后让前端刷新到页面
    private List<EnvExtDTO> consumerEnvs;
}
