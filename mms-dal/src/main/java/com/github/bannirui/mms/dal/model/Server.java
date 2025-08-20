package com.github.bannirui.mms.dal.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "server")
public class Server {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private Integer type;
    private Integer port;
    private Integer status;
    private Long hostId;
}
