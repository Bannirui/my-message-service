package com.github.bannirui.mms.dal.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "topic_ref")
public class TopicRef {
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * @see Topic#id
     */
    private Long topicId;
    /**
     * @see Env#id
     */
    private Long envId;
    /**
     * @see Server#id
     */
    private Long serverId;
}
