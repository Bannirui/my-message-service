package com.github.bannirui.mms.metadata;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class ConsumerGroupMetadata extends MmsMetadata {
    private String bindingTopic;
    private String consumeFrom;
    private String broadcast;
    private String suspend = "false";
    private String releaseStatus;


    public boolean needSuspend() {
        return StringUtils.isNotBlank(this.suspend) && "true".equalsIgnoreCase(this.suspend);
    }

    public boolean suspendChange(ConsumerGroupMetadata metadata) {
        return this.bindingTopic.equals(metadata.getBindingTopic()) && this.consumeFrom.equals(metadata.getConsumeFrom()) && this.broadcast.equals(metadata.getBroadcast()) && !this.suspend.equals(metadata.getSuspend()) && super.equals(metadata);
    }
}
