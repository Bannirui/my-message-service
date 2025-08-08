package com.github.bannirui.mms.service.domain.topic;

public class TopicEnvironmentInfoVo {

    // 环境
    private Integer environmentId;
    // 集群
    private Long serverId;

    public TopicEnvironmentInfoVo() {
    }

    public Integer getEnvironmentId() {
        return environmentId;
    }

    public void setEnvironmentId(Integer environmentId) {
        this.environmentId = environmentId;
    }

    public Long getServerId() {
        return serverId;
    }

    public void setServerId(Long serverId) {
        this.serverId = serverId;
    }
}
