package com.github.bannirui.mms.dto.topic;

import com.github.bannirui.mms.service.domain.topic.TopicEnvironmentInfoVo;
import lombok.Data;

import java.util.List;

@Data
public class TopicDTO {

    private Long id;
    private String name;

    private Long clusterId;
    private String clusterName;
    private String clusterType;

    private List<TopicEnvironmentInfoVo> environments;
}

