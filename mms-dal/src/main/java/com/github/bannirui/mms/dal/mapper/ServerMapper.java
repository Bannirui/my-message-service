package com.github.bannirui.mms.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.bannirui.mms.dal.model.EnvHostServerExt;
import com.github.bannirui.mms.dal.model.Server;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ServerMapper extends BaseMapper<Server> {
    List<EnvHostServerExt> serverExtByServerType(@Param("serverType") Integer serverType);
}
