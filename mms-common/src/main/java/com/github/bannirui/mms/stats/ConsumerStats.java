package com.github.bannirui.mms.stats;

import java.util.ArrayList;
import java.util.List;

public class ConsumerStats extends StatsInfo {

    private List<MeterInfo> meters = new ArrayList<>();

    private List<TimerInfo> timers = new ArrayList<>();

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
}

