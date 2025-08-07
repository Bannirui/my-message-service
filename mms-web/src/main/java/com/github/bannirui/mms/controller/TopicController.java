package com.github.bannirui.mms.controller;

import com.github.bannirui.mms.dto.topic.TopicDTO;
import com.github.bannirui.mms.result.Result;
import com.github.bannirui.mms.service.topic.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "api/topic")
public class TopicController {

    @Autowired
    TopicService topicService;

    @PostMapping(value = "/add")
    public Result<Integer> addTopic(@RequestBody TopicDTO topicDto) {
        return Result.success(topicService.addTopic(topicDto, "TODO"));
    }
}
