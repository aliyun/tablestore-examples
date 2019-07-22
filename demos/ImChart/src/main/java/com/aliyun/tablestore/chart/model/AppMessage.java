package com.aliyun.tablestore.chart.model;

import com.alicloud.openservices.tablestore.timeline.model.TimelineEntry;
import com.alicloud.openservices.tablestore.timeline.model.TimelineMessage;

public class AppMessage {
    private String timelineId;
    private long sequenceId;
    private TimelineMessage timelineMessage;

    public AppMessage(String timelineId, TimelineEntry timelineEntry) {
        this.timelineId = timelineId;
        this.sequenceId = timelineEntry.getSequenceID();
        this.timelineMessage = timelineEntry.getMessage();
    }

    public AppMessage(String timelineId, TimelineMessage timelineMessage) {
        this.timelineId = timelineId;
        this.timelineMessage = timelineMessage;
    }

    public String getTimelineId() {
        return timelineId;
    }

    public void setTimelineId(String timelineId) {
        this.timelineId = timelineId;
    }

    public long getSequenceId() {
        return sequenceId;
    }

    public void setSequenceId(long sequenceId) {
        this.sequenceId = sequenceId;
    }

    public TimelineMessage getTimelineMessage() {
        return timelineMessage;
    }

    public void setTimelineMessage(TimelineMessage timelineMessage) {
        this.timelineMessage = timelineMessage;
    }
}
