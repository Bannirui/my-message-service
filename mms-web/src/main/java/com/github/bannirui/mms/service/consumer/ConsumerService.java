package com.github.bannirui.mms.service.consumer;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.bannirui.mms.common.ResourceStatus;
import com.github.bannirui.mms.dal.mapper.ConsumerMapper;
import com.github.bannirui.mms.dal.model.Consumer;
import com.github.bannirui.mms.dal.model.ConsumerExtTopicAndEnv;
import com.github.bannirui.mms.req.consumer.ConsumerPageReq;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(ConsumerService.class);

    @Autowired
    private ConsumerMapper consumerMapper;

    /**
     * consumer name不能重复
     */
    public boolean uniqueConsumerCheck(String consumerName) {
        return new LambdaQueryChainWrapper<>(this.consumerMapper)
                .apply("LOWER(name) = {0}", consumerName.toLowerCase())
                .count() == 0;
    }

    public List<ConsumerExtTopicAndEnv> queryPage(ConsumerPageReq req, java.util.function.Consumer<Long> cnt) {
        // 所有的id
        Page<Consumer> consumers = this.consumerMapper.selectPage(new Page<>(req.getPage(), req.getSize()), new LambdaQueryWrapper<>(Consumer.class)
                .select(Consumer::getId)
                .ne(Consumer::getStatus, ResourceStatus.DELETE.getCode())
                .eq(StringUtils.isNotEmpty(req.getConsumerName()), Consumer::getName, req.getConsumerName())
                .eq(Objects.nonNull(req.getUserId()), Consumer::getUserId, req.getUserId())
        );
        if (Objects.nonNull(cnt)) {
            cnt.accept(consumers.getTotal());
        }
        Set<Long> consumerIds = consumers.getRecords().stream().map(Consumer::getId).collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(consumerIds)) {
            return Collections.emptyList();
        }
        // consumer的详情
        return this.consumerMapper.selectExtTopicAndEnvs(new ArrayList<>(consumerIds));
    }
}
