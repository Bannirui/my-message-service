package com.github.bannirui.mms.stats;

import java.util.ArrayList;
import java.util.List;

public class ProducerStats extends StatsInfo {
    List<MeterInfo> meters = new ArrayList<>();

    List<TimerInfo> timers = new ArrayList<>();

    List<DistributionInfo> distributions = new ArrayList<>();

    public List<MeterInfo> getMeters() {
        return meters;
    }

    public void setMeters(List<MeterInfo> meters) {
        this.meters = meters;
    }

    public List<TimerInfo> getTimers() {
        return timers;
    }

    public void setTimers(List<TimerInfo> timers) {
        this.timers = timers;
    }

    public List<DistributionInfo> getDistributions() {
        return distributions;
    }

    public void setDistributions(List<DistributionInfo> distributions) {
        this.distributions = distributions;
    }
}

