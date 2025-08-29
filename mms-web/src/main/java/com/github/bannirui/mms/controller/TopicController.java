package com.github.bannirui.mms.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.bannirui.mms.common.ResourceStatus;
import com.github.bannirui.mms.dal.mapper.TopicMapper;
import com.github.bannirui.mms.dal.model.Topic;
import com.github.bannirui.mms.dal.model.TopicEnvHostServerExt;
import com.github.bannirui.mms.dto.topic.EnvExtDTO;
import com.github.bannirui.mms.dto.topic.TopicExtDTO;
import com.github.bannirui.mms.req.topic.ApplyTopicReq;
import com.github.bannirui.mms.req.topic.ApproveTopicReq;
import com.github.bannirui.mms.req.topic.SearchTopicReq;
import com.github.bannirui.mms.req.topic.TopicPageReq;
import com.github.bannirui.mms.result.PageResult;
import com.github.bannirui.mms.result.Result;
import com.github.bannirui.mms.service.topic.TopicService;
import com.github.bannirui.mms.util.Assert;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/api/topic")
public class TopicController {

    @Autowired
    TopicService topicService;
    @Autowired
    private TopicMapper topicMapper;

    /**
     * 申请topic
     *
     * @return topic的id
     */
    @PostMapping(path = "/add")
    public Result<Long> addTopic(@RequestBody ApplyTopicReq req) {
        Assert.that(Objects.nonNull(req.getUserId()), "申请人必填");
        Assert.that(StringUtils.isNotEmpty(req.getName()), "topic必填");
        Assert.that(Objects.nonNull(req.getClusterType()), "MQ类型必填");
        Assert.that(Objects.nonNull(req.getAppId()), "申请给哪个服务");
        Assert.that(Objects.nonNull(req.getTps()), "tps必填");
        Assert.that(Objects.nonNull(req.getMsgSz()), "消息体大小必填");
        Assert.that(CollectionUtils.isNotEmpty(req.getEnvs()), "环境必填");
        return Result.success(this.topicService.addTopic(req, "TODO"));
    }

    /**
     * 审批topic
     *
     * @return 审批topic初始化失败的env
     */
    @PutMapping(value = "/{topicId}/approve")
    public Result<List<Long>> approveTopic(@PathVariable Long topicId, @RequestBody ApproveTopicReq req) {
        Assert.that(Objects.nonNull(topicId), "topic id必填");
        Assert.that(Objects.nonNull(req.getPartitions()) && req.getPartitions() > 0, "partitions非法");
        Assert.that(Objects.nonNull(req.getReplication()) && req.getReplication() > 0, "getReplication非法");
        return Result.success(topicService.approveTopic(topicId, req, ""));
    }

    @GetMapping(value = "/querypage")
    public PageResult<TopicExtDTO> queryTopicsPage(TopicPageReq req) {
        AtomicReference<Long> cnt = new AtomicReference<>((long) 0);
        List<TopicEnvHostServerExt> topics = topicService.queryTopicsPage(req, cnt::set);
        List<TopicExtDTO> ret = new ArrayList<>(topics.size());
        if (CollectionUtils.isEmpty(topics)) {
            return PageResult.success(cnt.get(), ret);
        }
        // todo关联user和app信息
        Map<Long, List<TopicEnvHostServerExt>> topicMap = topics.stream()
                .peek(x -> {
                    x.setUserName("dingrui");
                    x.setAppName("mss");
                })
                .collect(Collectors.groupingBy(TopicEnvHostServerExt::getTopicId));
        topicMap.forEach((k, v) -> {
            TopicEnvHostServerExt topic = v.get(0);
            List<EnvExtDTO> envs = new ArrayList<>();
            v.stream().sorted(Comparator.comparing(TopicEnvHostServerExt::getEnvSort)).forEach(x -> {
                EnvExtDTO e = new EnvExtDTO();
                e.setEnvId(x.getEnvId());
                e.setEnvName(x.getEnvName());
                envs.add(e);
            });
            TopicExtDTO e = new TopicExtDTO();
            e.setTopicId(topic.getTopicId());
            e.setTopicName(topic.getTopicName());
            e.setTopicType(topic.getTopicType());
            e.setTopicStatus(topic.getTopicStatus());
            e.setTopicTps(topic.getTopicTps());
            e.setTopicMsgSz(topic.getTopicMsgSz());
            e.setTopicPartitions(topic.getTopicPartitions());
            e.setTopicReplication(topic.getTopicReplication());
            e.setTopicRemark(topic.getTopicRemark());
            e.setEnvs(envs);
            e.setUserId(topic.getUserId());
            e.setUserName(topic.getUserName());
            e.setAppId(topic.getAppId());
            e.setAppName(topic.getAppName());
            ret.add(e);
        });
        return PageResult.success(cnt.get(), ret);
    }

    /**
     * 模糊搜索topic
     *
     * @param req 搜索条件
     */
    @GetMapping(value = "/searchTopic")
    public Result<List<TopicExtDTO>> searchTopic(SearchTopicReq req) {
        List<TopicExtDTO> ret = new ArrayList<>();
        String topicName = null;
        if (StringUtils.isEmpty(topicName = req.getTopicName())) {
            return Result.success(ret);
        }
        List<Topic> topics = this.topicMapper.selectList(new LambdaQueryWrapper<>(Topic.class)
                .like(Topic::getName, topicName)
                .in(Topic::getStatus, ResourceStatus.CREATE_APPROVED.getCode(), ResourceStatus.UPDATE_APPROVED.getCode(), ResourceStatus.ENABLE.getCode())
        );
        topics.forEach(x -> ret.add(new TopicExtDTO(x.getId(), x.getName(), x.getClusterType())));
        return Result.success(ret);
    }
}
