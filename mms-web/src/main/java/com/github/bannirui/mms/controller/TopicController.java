package com.github.bannirui.mms.controller;

import com.github.bannirui.mms.dto.topic.TopicDTO;
import com.github.bannirui.mms.result.Result;
import com.github.bannirui.mms.service.topic.TopicService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/topic")
public class TopicController {

    @Autowired
    TopicService topicService;

    /**
     * 申请topic
     */
    @PostMapping(value = "/add")
    public Result<Integer> addTopic(@RequestBody TopicDTO topicDto) {
        if (StringUtils.isEmpty(topicDto.getName())) {
            return Result.error("401", "topic name required");
        }
        if (CollectionUtils.isEmpty(topicDto.getEnvironments())) {
            return Result.error("401", "environments required");
        }
        return Result.success(topicService.addTopic(topicDto, "TODO"));
    }

    /**
     * 审批topic
     */
    @PutMapping(value = "/{id}/approve")
    public Result<List<Integer>> approveTopic(@RequestBody TopicDTO topicDto, @PathVariable Long id) {
        topicDto.setId(id);
        return Result.success(topicService.approveTopic(topicDto, ""));
    }
}
