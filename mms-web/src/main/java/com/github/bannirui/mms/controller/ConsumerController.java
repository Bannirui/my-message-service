package com.github.bannirui.mms.controller;

import com.github.bannirui.mms.common.ResourceStatus;
import com.github.bannirui.mms.dal.mapper.ConsumerMapper;
import com.github.bannirui.mms.dal.mapper.TopicMapper;
import com.github.bannirui.mms.dal.model.Consumer;
import com.github.bannirui.mms.dal.model.ConsumerExtTopicAndEnv;
import com.github.bannirui.mms.dal.model.TopicEnvHostServerExt;
import com.github.bannirui.mms.dto.consumer.ConsumerExtDTO;
import com.github.bannirui.mms.dto.topic.EnvExtDTO;
import com.github.bannirui.mms.req.consumer.ApplyConsumerReq;
import com.github.bannirui.mms.req.consumer.ConsumerPageReq;
import com.github.bannirui.mms.resp.consumer.ApplyConsumerResp;
import com.github.bannirui.mms.result.PageResult;
import com.github.bannirui.mms.result.Result;
import com.github.bannirui.mms.service.consumer.ConsumerService;
import com.github.bannirui.mms.service.manager.MmsContextManager;
import com.github.bannirui.mms.service.router.ZkRouter;
import com.github.bannirui.mms.util.Assert;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/api/consumer")
public class ConsumerController {

    @Autowired
    private ConsumerService consumerService;
    @Autowired
    private ConsumerMapper consumerMapper;
    @Autowired
    private TopicMapper topicMapper;
    @Autowired
    ZkRouter zkRouter;

    /**
     * 申请consumer
     *
     * @return consumer的id
     */
    @PostMapping(path = "/add")
    public Result<ApplyConsumerResp> addConsumer(@RequestBody ApplyConsumerReq req) {
        Assert.that(StringUtils.isNotEmpty(req.getName()), "consumer必填");
        Assert.that(Objects.nonNull(req.getUserId()), "申请人必填");
        Assert.that(Objects.nonNull(req.getAppId()), "申请给哪个服务");
        Long topicId = req.getTopicId();
        Assert.that(Objects.nonNull(topicId), "topic必填");
        Assert.that(StringUtils.isNotEmpty(req.getRemark()), "remark必填");

        Assert.that(this.consumerService.uniqueConsumerCheck(req.getName()), "consumer名称不能重复");

        List<TopicEnvHostServerExt> topicExts = this.topicMapper.topicExtEnvByTopicId(topicId);
        Assert.that(CollectionUtils.isNotEmpty(topicExts), "topic不存在");
        Consumer e = new Consumer();
        e.setUserId(req.getUserId());
        e.setName(req.getName());
        e.setTopicId(topicId);
        e.setAppId(req.getAppId());
        e.setRemark(req.getRemark());
        e.setStatus(ResourceStatus.CREATE_NEW.getCode());
        e.setStatus(ResourceStatus.CREATE_NEW.getCode());
        e.setConsumerBroadcast(req.isConsumerBroadcast() ? 1 : 0);
        e.setConsumerFromMin(req.isConsumerFromMin() ? 1 : 0);
        this.consumerMapper.insert(e);
        ApplyConsumerResp ret = new ApplyConsumerResp();
        ret.setConsumerId(e.getId());
        List<EnvExtDTO> envs = new ArrayList<>();
        topicExts.stream().sorted(Comparator.comparing(TopicEnvHostServerExt::getEnvSort)).forEach(x -> {
            EnvExtDTO env = new EnvExtDTO();
            env.setEnvId(x.getEnvId());
            env.setEnvName(x.getEnvName());
            envs.add(env);
        });
        ret.setConsumerEnvs(envs);
        return Result.success(ret);
    }

    /**
     * 审批consumer
     */
    @PutMapping(value = "/{consumerId}/approve")
    public Result<Void> approveConsumer(@PathVariable Long consumerId) {
        Assert.that(Objects.nonNull(consumerId), "consumer id必填");
        List<ConsumerExtTopicAndEnv> consumers = this.consumerMapper.extTopicAndEnvsByConsumerId(consumerId);
        Assert.that(CollectionUtils.isNotEmpty(consumers), "consumer不存在");
        Integer status = consumers.get(0).getConsumerStatus();
        Assert.that(!Objects.equals(status, ResourceStatus.DELETE.getCode()), "consumer不存在");
        Assert.that((status & ResourceStatus.ENABLE_MASK) == 0, "consumer可用 不需要审批");
        // 分环境注册
        consumers.forEach(x -> {
            MmsContextManager.setEnv(x.getEnvId());
            // todo
        });
        this.consumerMapper.updateById(new Consumer() {{
            setId(consumerId);
            setStatus(ResourceStatus.CREATE_APPROVED.getCode());
        }});
        return Result.success(null);
    }

    @GetMapping(value = "/pageList")
    public PageResult<ConsumerExtDTO> pageList(ConsumerPageReq req) {
        AtomicReference<Long> cnt = new AtomicReference<>((long) 0);
        List<ConsumerExtTopicAndEnv> consumers = this.consumerService.queryPage(req, cnt::set);
        List<ConsumerExtDTO> ret = new ArrayList<>(consumers.size());
        if (CollectionUtils.isEmpty(consumers)) {
            return PageResult.success(cnt.get(), ret);
        }
        // todo 关联user和app信息
        Map<Long, List<ConsumerExtTopicAndEnv>> consumerMap = consumers.stream()
                .peek(x -> {
                    x.setConsumerUserName("dingrui");
                    x.setConsumerAppName("mss");
                })
                .collect(Collectors.groupingBy(ConsumerExtTopicAndEnv::getConsumerId));
        consumerMap.forEach((k, v) -> {
            ConsumerExtTopicAndEnv consumer = v.get(0);
            List<EnvExtDTO> envs = new ArrayList<>();
            v.stream().sorted(Comparator.comparing(ConsumerExtTopicAndEnv::getEnvSort)).forEach(x -> {
                EnvExtDTO e = new EnvExtDTO();
                e.setEnvId(x.getEnvId());
                e.setEnvName(x.getEnvName());
                envs.add(e);
            });
            ConsumerExtDTO e = new ConsumerExtDTO();
            e.setConsumerId(consumer.getConsumerId());
            e.setConsumerName(consumer.getConsumerName());
            e.setConsumerUserId(consumer.getConsumerUserId());
            e.setConsumerUserName(consumer.getConsumerUserName());
            e.setConsumerAppId(consumer.getConsumerAppId());
            e.setConsumerAppName(consumer.getConsumerAppName());
            e.setConsumerStatus(consumer.getConsumerStatus());
            e.setConsumerRemark(consumer.getConsumerRemark());
            e.setTopicId(consumer.getTopicId());
            e.setTopicName(consumer.getTopicName());
            e.setTopicType(consumer.getTopicType());
            e.setConsumerEnvs(envs);
            e.setConsumerBroadcast(Objects.equals(1, consumer.getConsumerBroadcast()));
            e.setConsumerFromMin(Objects.equals(1, consumer.getConsumerFromMin()));
            ret.add(e);
        });
        return PageResult.success(cnt.get(), ret);
    }
}
