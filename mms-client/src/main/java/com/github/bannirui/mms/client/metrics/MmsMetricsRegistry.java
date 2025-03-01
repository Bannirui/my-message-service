package com.github.bannirui.mms.client.metrics;

import com.codahale.metrics.MetricRegistry;
import com.github.bannirui.mms.common.MmsConst;

class MmsMetricsRegistry {

    static final MetricRegistry REGISTRY = new MetricRegistry();

    static final String buildName(String producerMetricGroup, String type, String clientName, String mmsName) {
        return producerMetricGroup + "--" + type + "--" + mmsName + "--" + MmsConst.MMS_IP.replace(".", "_") + "--" + clientName;
    }
}

