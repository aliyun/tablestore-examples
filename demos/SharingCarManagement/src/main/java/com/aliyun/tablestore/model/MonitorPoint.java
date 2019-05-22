package com.aliyun.tablestore.model;

public class MonitorPoint {
    private long time;
    private String location;
    private double speed;
    private double remain;
    private double expected;
    private double total;

    public long getTime() {
        return time;
    }

    public MonitorPoint setTime(long time) {
        this.time = time;
        return this;
    }

    public String getLocation() {
        return location;
    }

    public MonitorPoint setLocation(String location) {
        this.location = location;
        return this;
    }

    public double getSpeed() {
        return speed;
    }

    public MonitorPoint setSpeed(double speed) {
        this.speed = speed;
        return this;
    }

    public double getRemain() {
        return remain;
    }

    public MonitorPoint setRemain(double remain) {
        this.remain = remain;
        return this;
    }

    public double getExpected() {
        return expected;
    }

    public MonitorPoint setExpected(double expexted) {
        this.expected = expected;
        return this;
    }

    public double getTotal() {
        return total;
    }

    public MonitorPoint setTotal(double total) {
        this.total = total;
        return this;
    }
}
