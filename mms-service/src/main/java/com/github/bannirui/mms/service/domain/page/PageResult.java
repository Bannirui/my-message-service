package com.github.bannirui.mms.service.domain.page;

import com.google.common.collect.Lists;
import lombok.Data;

import java.util.List;

@Data
public class PageResult<T> {

    private int currentPage;
    private int pageSize;
    private long total;
    private List<T> records;

    public PageResult(int currentPage, int pageSize, long total, List<T> records) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.total = total;
        this.records = records;
    }

    public PageResult(int currentPage, int pageSize) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.records = Lists.newArrayList();
    }

    private PageResult() {
    }
}

