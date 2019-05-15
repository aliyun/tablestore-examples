package com.aliyun.tablestore.sporttrack.model;

public class SportTrackOrder {

    private String sportTrackType;

    private long startTime;

    private long endTime;

    private int distance;

    public String getSportTrackType() {
        return sportTrackType;
    }

    public void setSportTrackType(String sportTrackType) {
        this.sportTrackType = sportTrackType;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }
}
