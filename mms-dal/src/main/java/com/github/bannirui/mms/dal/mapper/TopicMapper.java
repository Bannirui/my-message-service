package com.github.bannirui.mms.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.bannirui.mms.dal.model.Topic;
import com.github.bannirui.mms.dal.model.TopicEnvHostServerExt;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TopicMapper extends BaseMapper<Topic> {
    List<TopicEnvHostServerExt> topicExtEnvByTopicIds(@Param("topicIds") List<Long> topicIds);
}
