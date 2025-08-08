package com.github.bannirui.mms.service.domain.topic;

import lombok.Data;

@Data
public class TopicEnvironmentInfoVo {

    /**
     * 集群
     * @see com.github.bannirui.mms.dal.model.Server#id
     */
    private Long serverId;
    /**
     * @see com.github.bannirui.mms.dal.model.Env#id
     */
    private Long envId;
}
