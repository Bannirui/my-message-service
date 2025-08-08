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
    private String name;
    private Integer status;
}
