package com.github.bannirui.mms.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.bannirui.mms.dal.model.TopicEnvServer;
import com.github.bannirui.mms.dal.model.TopicEnvServerRef;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TopicEnvServerMapper extends BaseMapper<TopicEnvServer> {
    List<TopicEnvServerRef> getByTopicId(@Param("topicId") Long topicId);

    List<TopicEnvServerRef> getByTopicIds(@Param("topicIds") List<Long> topicIds);
}
