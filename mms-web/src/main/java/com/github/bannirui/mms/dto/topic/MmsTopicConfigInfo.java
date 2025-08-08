package com.github.bannirui.mms.dto.topic;

import lombok.Data;
import org.apache.commons.collections.MapUtils;

import java.util.Map;

/**
 * 集群维度 一个集群可能批量多个topic
 */
@Data
public class MmsTopicConfigInfo {

    /**
     * 集群
     */
    private String clusterName;

    /**
     * key is topic name
     */
    private Map<String, Integer> partitions;

    /**
     * key is topic name
     */
    private Map<String, Integer> replications;

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("[ ");
        if (MapUtils.isNotEmpty(this.partitions)) {
            for (Map.Entry<String, Integer> tvEntry : this.partitions.entrySet()) {
                stringBuilder.append(tvEntry.getKey());
                stringBuilder.append(":");
                stringBuilder.append(tvEntry.getValue().toString());
                stringBuilder.append(",");
            }
        }
        String topicsStr = stringBuilder.substring(0, stringBuilder.length() - 1) + " ]";
        return "MmsTopicConfigInfo{" +
                "clusterName='" + this.clusterName + '\'' +
                ", partitions=" + topicsStr +
                '}';
    }
}

