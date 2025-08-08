package com.github.bannirui.mms.dto.topic;

import com.github.bannirui.mms.service.domain.topic.TopicEnvironmentInfoVo;

import java.util.List;

public class TopicDTO {

    private Long id;
    private String name;

    private List<TopicEnvironmentInfoVo> environments;

    public TopicDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<TopicEnvironmentInfoVo> getEnvironments() {
        return environments;
    }

    public void setEnvironments(List<TopicEnvironmentInfoVo> environments) {
        this.environments = environments;
    }
}

