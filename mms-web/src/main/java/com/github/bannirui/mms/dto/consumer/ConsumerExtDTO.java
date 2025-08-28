package com.github.bannirui.mms.dto.consumer;

import com.github.bannirui.mms.dto.topic.EnvExtDTO;
import lombok.Data;

import java.util.List;

@Data
public class ConsumerExtDTO {

    private Long consumerId;
    private String consumerName;
    private Long consumerUserId;
    private String consumerUserName;
    /**
     * topic给哪个应用的
     */
    private Long consumerAppId;
    private String consumerAppName;
    private Integer consumerStatus;
    private String consumerRemark;

    private Long topicId;
    private String topicName;

    private List<EnvExtDTO> consumerEnvs;
}

