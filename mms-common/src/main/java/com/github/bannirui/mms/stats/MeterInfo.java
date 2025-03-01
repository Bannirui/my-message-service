package com.github.bannirui.mms.stats;

public class MeterInfo {


    private long count;

    private double mean;

    private double min1Rate;

    private double min5Rate;

    private double min15Rate;

    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public double getMean() {
        return mean;
    }

    public void setMean(double mean) {
        this.mean = mean;
    }

    public double getMin1Rate() {
        return min1Rate;
    }

    public void setMin1Rate(double min1Rate) {
        this.min1Rate = min1Rate;
    }

    public double getMin5Rate() {
        return min5Rate;
    }

    public void setMin5Rate(double min5Rate) {
        this.min5Rate = min5Rate;
    }

    public double getMin15Rate() {
        return min15Rate;
    }

    public void setMin15Rate(double min15Rate) {
        this.min15Rate = min15Rate;
    }


}

