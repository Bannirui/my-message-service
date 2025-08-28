package com.github.bannirui.mms.dal.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "consumer")
public class Consumer {
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 消费组名
     */
    private String name;
    /**
     * 申请人
     */
    private Long userId;
    /**
     * 订阅哪个topic
     */
    private Long topicId;
    /**
     * 给哪个应用服务的
     */
    private Long appId;
    private Integer status;
    /**
     * 申请时的备注信息
     */
    private String remark;
}
