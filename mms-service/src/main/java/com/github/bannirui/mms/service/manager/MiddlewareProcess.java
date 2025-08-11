package com.github.bannirui.mms.service.manager;

public interface MiddlewareProcess {

    /**
     * 封装中间件差异性 委托向nameserv申请创建topic
     *
     * @param topicName   topic name
     * @param partitions  分区数
     * @param replication 副本数
     */
    void createTopic(String topicName, int partitions, Integer replication);

    boolean existTopic(String topicName);

    void updateTopic(String topicName, int partitions);
}

