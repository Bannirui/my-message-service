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
    /**
     * 集群名称
     */
    private String name;
    /**
     * broker addr
     */
    private String address;
    /**
     * <ul>
     *     <li>1 kafka</li>
     *     <li>2 rocket</li>
     * </ul>
     */
    private Integer type;
    private Integer status;
}
