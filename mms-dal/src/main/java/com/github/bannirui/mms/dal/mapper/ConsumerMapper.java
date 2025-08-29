package com.github.bannirui.mms.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.bannirui.mms.dal.model.Consumer;
import com.github.bannirui.mms.dal.model.ConsumerExtTopicAndEnv;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ConsumerMapper extends BaseMapper<Consumer> {
    List<ConsumerExtTopicAndEnv> extTopicAndEnvsByConsumerId(@Param("consumerId") Long consumerId);
    List<ConsumerExtTopicAndEnv> extTopicAndEnvsByConsumerIds(@Param("consumerIds") List<Long> consumerIds);
}
