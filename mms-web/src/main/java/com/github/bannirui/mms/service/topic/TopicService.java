package com.github.bannirui.mms.service.topic;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.github.bannirui.mms.common.ResourceStatus;
import com.github.bannirui.mms.dal.mapper.TopicMapper;
import com.github.bannirui.mms.dal.model.Topic;
import com.github.bannirui.mms.dto.topic.TopicDTO;
import com.github.bannirui.mms.util.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class TopicService {

    @Autowired
    private TopicMapper topicMapper;

    @Transactional(rollbackFor = Exception.class)
    public int addTopic(TopicDTO topicDto, String operator) {
        boolean isUnique = this.uniqueTopicCheck(topicDto.getName());
        Assert.that(isUnique, "主题名称重复");
        Topic topic = new Topic();
        topic.setName(topicDto.getName());
        topic.setStatus(ResourceStatus.CREATE_NEW.getStatus());
        topic.setCreateDate(LocalDateTime.now());
        int count = topicMapper.insert(topic);
        return count;
    }

    // topic name不能重复
    private boolean uniqueTopicCheck(String topicName) {
        return new LambdaQueryChainWrapper<>(topicMapper)
                .apply("LOWER(name) = {0}", topicName.toLowerCase())
                .count() == 0;
    }
}
