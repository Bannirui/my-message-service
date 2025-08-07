package com.github.bannirui.mms.client.consumer;

import com.github.bannirui.mms.common.MmsConst;
import com.google.common.collect.Sets;

import java.util.Set;

public class ConsumerGroup {

    public ConsumerGroup(String consumerGroup) {
        this.consumerGroup = consumerGroup;
    }

    public ConsumerGroup(String consumerGroup, String consumerName) {
        this.consumerGroup = consumerGroup;
        this.consumerName = consumerName;
    }

    public ConsumerGroup(String consumerGroup, String consumerName, Set<String> tags) {
        this.consumerGroup = consumerGroup;
        this.consumerName = consumerName;
        this.tags = tags;
    }

    private String consumerGroup;

    private String consumerName = MmsConst.DEFAULT_CONSUMER;

    private Set<String> tags = Sets.newHashSet();

    public String getConsumerGroup() {
        return consumerGroup;
    }

    public void setConsumerGroup(String consumerGroup) {
        this.consumerGroup = consumerGroup;
    }

    public String getConsumerName() {
        return consumerName;
    }

    public void setConsumerName(String consumerName) {
        this.consumerName = consumerName;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }
}

