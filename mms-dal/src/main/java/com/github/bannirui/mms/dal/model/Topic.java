package com.github.bannirui.mms.dal.model;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;

@TableName(value = "topic")
public class Topic {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Integer status;
    private String name;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createDate;

    public Topic() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }
}
