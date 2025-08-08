package com.github.bannirui.mms.service.manager;

public interface MiddlewareProcess {

    void createTopic(String topic, int partitions, Integer replication);
}

