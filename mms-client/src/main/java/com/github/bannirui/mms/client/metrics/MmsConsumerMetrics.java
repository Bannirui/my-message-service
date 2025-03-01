package com.github.bannirui.mms.client.metrics;

import com.codahale.metrics.Meter;
import com.codahale.metrics.Timer;
import com.github.bannirui.mms.stats.ConsumerStats;
import com.github.bannirui.mms.stats.StatsInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MmsConsumerMetrics extends MmsMetrics {
    public static final Logger logger = LoggerFactory.getLogger(MmsConsumerMetrics.class);

    private final Meter consumeSuccessRate;

    private final Meter consumeFailureRate;

    private final Timer userCostTimeMs;

    private static final String CONSUMER_METRIC_GROUP = "MmsConsumerMetrics";

    public MmsConsumerMetrics(String clientName, String mmsName) {
        super(clientName, mmsName);
        this.consumeSuccessRate = MmsMetricsRegistry.REGISTRY.meter(
            MmsMetricsRegistry.buildName(CONSUMER_METRIC_GROUP, "messageSuccessRate", clientName, mmsName));
        this.consumeFailureRate = MmsMetricsRegistry.REGISTRY.meter(
            MmsMetricsRegistry.buildName(CONSUMER_METRIC_GROUP, "consumeFailureRate", clientName, mmsName));
        this.userCostTimeMs = MmsMetricsRegistry.REGISTRY.timer(
            MmsMetricsRegistry.buildName(CONSUMER_METRIC_GROUP, "userCostTimeMs", clientName, mmsName));
    }

    public Meter consumeSuccessRate() {
        return consumeSuccessRate;
    }

    public Meter consumeFailureRate() {
        return consumeFailureRate;
    }

    public Timer userCostTimeMs() {
        return userCostTimeMs;
    }

    @Override
    public StatsInfo reportMessageStatistics() {
        ConsumerStats info = new ConsumerStats();
        info.setClientInfo(getClientInfo());
        info.getMeters().add(transfer(consumeSuccessRate, "consumeSuccessRate"));
        info.getMeters().add(transfer(consumeFailureRate, "consumeFailureRate"));
        info.getTimers().add(transfer(userCostTimeMs, "userCostTimeMs"));
        return info;
    }

    @Override
    public String reportLogStatistics() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.getClientName()).append("--").append(this.getMmsName()).append(":\n");
        stringBuilder.append("SuccessMessagePerSec     ");
        try {
            processMeter(this.consumeSuccessRate, stringBuilder);
            stringBuilder.append("FailureMessagePerSec");
            processMeter(this.consumeFailureRate, stringBuilder);
            stringBuilder.append("userCostTimeMs");
            processTimer(this.userCostTimeMs, stringBuilder);
        } catch (Exception e) {
            logger.error("output statistics error", e);
        }
        return stringBuilder.toString();
    }
}

