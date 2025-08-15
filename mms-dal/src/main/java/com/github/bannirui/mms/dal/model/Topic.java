package com.github.bannirui.mms.dal.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "topic")
public class Topic {
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 申请人
     */
    private Long userId;
    /**
     * mq主题名
     */
    private String name;
    /**
     * 集群类型
     */
    private Integer clusterType;
    /**
     * 给哪个应用服务的
     */
    private Long appId;
    /**
     * 发送速度
     * 条/秒
     */
    private Integer tps;
    /**
     * 消息体大小
     * 字节
     */
    private Integer msgSz;

    /**
     * 申请 审批
     */
    private Integer status;
    private Integer partitions;
    private Integer replication;
    /**
     * 申请topic时的备注信息
     */
    private String remark;
}
