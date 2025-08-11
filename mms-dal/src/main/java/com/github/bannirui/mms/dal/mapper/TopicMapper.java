package com.github.bannirui.mms.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.bannirui.mms.dal.model.Topic;
import com.github.bannirui.mms.dal.model.TopicEnvServerRef;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TopicMapper extends BaseMapper<Topic> {
    IPage<TopicEnvServerRef> pageAll(Page<?> page);
}
