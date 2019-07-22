package com.aliyun.tablestore.chart.model;


public class Conversation {
    private String timelineId;
    private TimelineType type;
    private User user;
    private Group group;

    public Conversation(String timelineId, User user) {
        this.timelineId = timelineId;
        this.type = TimelineType.SINGLE;
        this.user = user;
    }

    public Conversation(String timelineId, Group group) {
        this.timelineId = timelineId;
        this.type = TimelineType.GROUP;
        this.group = group;
    }


    public String getTimelineId() {
        return timelineId;
    }

    public TimelineType getType() {
        return type;
    }

    public User getUser() {
        return user;
    }

    public Group getGroup() {
        return group;
    }
}
