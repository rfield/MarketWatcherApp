package com.rjfield.marketwatcher.models;

public class ChartDataPoint {
    private long timestampInSeconds; // Use seconds to avoid float precision issues
    private float valueY;

    public ChartDataPoint(long ts, float y) {
        timestampInSeconds = ts;
        valueY = y;
    }

    public long getTimestampInSeconds() {
        return timestampInSeconds;
    }

    public void setTimestampInSeconds(long timestampInSeconds) {
        this.timestampInSeconds = timestampInSeconds;
    }

    public float getValueY() {
        return valueY;
    }

    public void setValueY(float valueY) {
        this.valueY = valueY;
    }
}