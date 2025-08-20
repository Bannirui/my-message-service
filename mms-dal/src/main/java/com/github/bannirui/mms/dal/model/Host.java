package com.github.bannirui.mms.dal.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "host")
public class Host {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String host;
    private Long envId;
    private Integer status;
}
