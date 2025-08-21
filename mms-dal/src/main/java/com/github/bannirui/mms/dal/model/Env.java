package com.github.bannirui.mms.dal.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "env")
public class Env {
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 环境名称
     */
    private String name;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 排序用
     */
    private Integer sortId;
    /**
     * 环境的zk 作为当前环境的元数据注册中心
     */
    private Long zkId;
}
