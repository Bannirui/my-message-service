package com.github.bannirui.mms.client.metrics;

import com.codahale.metrics.Meter;
import com.codahale.metrics.Timer;
import com.github.bannirui.mms.stats.ProducerStats;
import com.github.bannirui.mms.stats.StatsInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProducerMetrics extends MmsMetrics {

    public static final Logger logger = LoggerFactory.getLogger(ProducerMetrics.class);
    private final Meter messageSuccessRate;
    private final Meter messageFailureRate;
    private final Timer sendCostRate;
    private Distribution msgBody;

    private static final String PRODUCER_METRIC_GROUP = "MmsProducerMetrics";

    public Distribution getDistribution() {
        return distribution;
    }

    private Distribution distribution;

    public ProducerMetrics(String clientName, String mmsName) {
        super(clientName, mmsName);
        this.messageSuccessRate = MmsMetricsRegistry.REGISTRY.meter(
            MmsMetricsRegistry.buildName(PRODUCER_METRIC_GROUP, "messageSuccessRate", clientName, mmsName));
        this.messageFailureRate = MmsMetricsRegistry.REGISTRY.meter(
            MmsMetricsRegistry.buildName(PRODUCER_METRIC_GROUP, "messageFailureRate", clientName, mmsName));
        this.sendCostRate = MmsMetricsRegistry.REGISTRY.timer(MmsMetricsRegistry.buildName(PRODUCER_METRIC_GROUP, "sendCostRate", clientName, mmsName));
        this.msgBody = Distribution.newDistribution(MmsMetricsRegistry.buildName(PRODUCER_METRIC_GROUP, "msgBody", clientName, mmsName));
        this.distribution = Distribution.newDistribution(MmsMetricsRegistry.buildName(PRODUCER_METRIC_GROUP, "distribution", clientName, mmsName));
    }

    public Meter messageSuccessRate() {
        return messageSuccessRate;
    }

    public Meter messageFailureRate() {
        return messageFailureRate;
    }

    public Timer sendCostRate() {
        return sendCostRate;
    }

    public Distribution msgBody() {
        return msgBody;
    }

    @Override
    public StatsInfo reportMessageStatistics() {
        ProducerStats info = new ProducerStats();
        Distribution old = distribution;
        Distribution oldMsgBody = msgBody;
        renewDistribution();
        info.setClientInfo(getClientInfo());
        info.getDistributions().add(transfer(old, "distribution"));
        info.getMeters().add(transfer(messageSuccessRate, "messageSuccessRate"));
        info.getMeters().add(transfer(messageFailureRate, "messageFailureRate"));
        info.getTimers().add(transfer(sendCostRate, "sendCostRate"));
        info.getDistributions().add(transfer(oldMsgBody, "msgBody"));
        return info;
    }

    private void renewDistribution() {
        distribution = Distribution.newDistribution(distribution.getName());
        msgBody = Distribution.newDistribution(msgBody.getName());
    }

    @Override
    public String reportLogStatistics() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.getClientName()).append("--").append(this.getMmsName()).append(":\n");
        try {
            stringBuilder.append("SuccessMessagePerSec     ");
            processMeter(this.messageSuccessRate, stringBuilder);
            stringBuilder.append("ProducerSendRateAndTimeMs");
            processTimer(this.sendCostRate, stringBuilder);
            stringBuilder.append("FailureMessagePerSec     ");
            processMeter(this.messageFailureRate, stringBuilder);
            Distribution old = distribution;
            Distribution oldMsgBody = msgBody;
            renewDistribution();
            stringBuilder.append(old.output());
            stringBuilder.append(oldMsgBody.output());
        } catch (Exception e) {
            logger.error("output statistics error", e);
        }
        return stringBuilder.toString();
    }
}

