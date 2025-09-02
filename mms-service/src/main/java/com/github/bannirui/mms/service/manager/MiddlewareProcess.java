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

    /**
     * 向mq申请创建消费组
     * @param consumerGroup 消费组名
     * @param broadcast 是否支持广播消费<t>TRUE</t>表示支持 <t>FALSE</t>表示不支持
     * @param consumerFromMin 是否支持最早消费<t>TRUE</t>表示支持 <t>FALSE</t>表示不支持
     */
    void createConsumerGroup(String consumerGroup, Boolean broadcast, Boolean consumerFromMin);
}

