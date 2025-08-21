package com.github.bannirui.mms.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.bannirui.mms.dal.model.Env;
import com.github.bannirui.mms.dal.model.EnvHostServerExt;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EnvMapper extends BaseMapper<Env> {
    List<EnvHostServerExt> selectByEnvIds(List<Long> envIds);
    List<EnvHostServerExt> selectNotDel();
}
