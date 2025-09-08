package com.github.bannirui.mms.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.bannirui.mms.dal.model.Env;
import com.github.bannirui.mms.dal.model.EnvHostServerExt;
import com.github.bannirui.mms.dal.model.MqMetaDataExt;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface EnvMapper extends BaseMapper<Env> {
    List<EnvHostServerExt> selectByEnvIds(@Param("envIds") List<Long> envIds);
    EnvHostServerExt selectByEnvId(@Param("envId") Long envId);
    List<EnvHostServerExt> selectNotDel();
    List<EnvHostServerExt> selectEnvExtEnable();
    List<MqMetaDataExt> mqMetaDataByEnvId(@Param("envId") Long envId);
}
