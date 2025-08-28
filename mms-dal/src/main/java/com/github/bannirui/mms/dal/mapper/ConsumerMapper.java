package com.github.bannirui.mms.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.bannirui.mms.dal.model.Consumer;
import com.github.bannirui.mms.dal.model.ConsumerExtTopicAndEnv;
import com.github.bannirui.mms.dal.model.Topic;
import com.github.bannirui.mms.dal.model.TopicEnvHostServerExt;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ConsumerMapper extends BaseMapper<Consumer> {
    List<ConsumerExtTopicAndEnv> selectExtTopicAndEnvs(@Param("consumerIds") List<Long> consumerIds);
}
