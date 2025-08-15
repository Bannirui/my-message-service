package com.github.bannirui.mms.dto.topic;

import com.github.bannirui.mms.dal.model.Env;
import com.github.bannirui.mms.dal.model.Topic;
import lombok.Data;

import java.util.List;

@Data
public class TopicRefDTO {

    private Topic topic;
    private List<Env> envs;
}

