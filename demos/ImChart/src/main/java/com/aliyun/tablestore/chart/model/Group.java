package com.aliyun.tablestore.chart.model;

public class Group {
    private String timelineId;
    private String groupName;
    private long createdTime;

    public Group(String timelineId, String groupName) {
        this.timelineId = timelineId;
        this.groupName = groupName;
    }

    public String getTimelineId() {
        return timelineId;
    }

    public Group setTimelineId(String timelineId) {
        this.timelineId = timelineId;
        return this;
    }

    public String getGroupName() {
        return groupName;
    }

    public Group setGroupName(String groupName) {
        this.groupName = groupName;
        return this;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public Group setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
        return this;
    }
}
