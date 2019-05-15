package com.aliyun.tablestore.sporttrack.model;

public class SportTrackMeta {
    private String targetNearbyType;

    private long timestamp;

    private String location;

    public String getTargetNearbyType() {
        return targetNearbyType;
    }

    public void setTargetNearbyType(String targetNearbyType) {
        this.targetNearbyType = targetNearbyType;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
