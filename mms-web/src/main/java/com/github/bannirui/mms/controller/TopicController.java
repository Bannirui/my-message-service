package com.github.bannirui.mms.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.bannirui.mms.dal.model.TopicEnvServerRef;
import com.github.bannirui.mms.req.ApplyTopicReq;
import com.github.bannirui.mms.req.ApproveTopicReq;
import com.github.bannirui.mms.req.topic.TopicPageReq;
import com.github.bannirui.mms.resp.topic.TopicPageResp;
import com.github.bannirui.mms.result.PageResult;
import com.github.bannirui.mms.result.Result;
import com.github.bannirui.mms.service.topic.TopicService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping(path = "/api/topic")
public class TopicController {

    @Autowired
    TopicService topicService;

    /**
     * 申请topic
     */
    @PostMapping(path = "/add")
    public Result<Integer> addTopic(@RequestBody ApplyTopicReq req) {
        if (Objects.isNull(req.getUserId())) {
            return Result.error(401, "申请人必填");
        }
        if (StringUtils.isEmpty(req.getName())) {
            return Result.error(401, "topic name required");
        }
        if (Objects.isNull(req.getClusterType())) {
            return Result.error(401, "集群类型必填");
        }
        if (Objects.isNull(req.getAppId())) {
            return Result.error(401, "申请给哪个服务必填");
        }
        if (Objects.isNull(req.getTps())) {
            return Result.error(401, "tps必填");
        }
        if (Objects.isNull(req.getMsgSz())) {
            return Result.error(401, "消息体大小必填");
        }
        if (CollectionUtils.isEmpty(req.getEnvs())) {
            return Result.error(401, "环境必填");
        }
        return Result.success(this.topicService.addTopic(req, "TODO"));
    }

    /**
     * 审批topic
     *
     * @return 审批topic初始化失败的env
     */
    @PutMapping(value = "/{topicId}/approve")
    public Result<List<Long>> approveTopic(@RequestBody ApproveTopicReq req, @PathVariable Long topicId) {
        if (Objects.isNull(topicId)) {
            return Result.error(401, "topic id必填");
        }
        if (Objects.isNull(req.getPartitions()) || req.getPartitions() <= 0) {
            return Result.error(401, "partitions非法");
        }
        if (Objects.isNull(req.getReplication()) || req.getReplication() <= 0) {
            return Result.error(401, "replication非法");
        }
        return Result.success(topicService.approveTopic(topicId, req, ""));
    }

    @GetMapping(value = "/querypage")
    public PageResult<TopicPageResp> queryTopicsPage(TopicPageReq req) {
        IPage<TopicEnvServerRef> pageRet = topicService.queryTopicsPage(req);
        TopicPageResp ret = new TopicPageResp();
        List<TopicPageResp> ls = new ArrayList<>();
        return PageResult.success(pageRet.getTotal(), ls);
    }
}
