package com.aliyun.tablestore.chart;

import com.alicloud.openservices.tablestore.model.*;
import com.alicloud.openservices.tablestore.timeline.model.TimelineIdentifier;
import com.alicloud.openservices.tablestore.timeline.model.TimelineMeta;
import com.aliyun.tablestore.chart.model.Conversation;
import com.aliyun.tablestore.chart.model.Group;
import com.aliyun.tablestore.chart.model.Sexuality;
import com.aliyun.tablestore.chart.model.User;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Util {

    public static RowPutChange userToRowPutChange(String tableName, User user) {
        PrimaryKey primaryKey = PrimaryKeyBuilder.createPrimaryKeyBuilder()
                .addPrimaryKeyColumn("user_id", PrimaryKeyValue.fromString(user.getUserId()))
                .build();

        RowPutChange rowPutChange = new RowPutChange(tableName, primaryKey);
        rowPutChange.addColumn("user_name", ColumnValue.fromString(user.getUserName()));
        rowPutChange.addColumn("sexuality", ColumnValue.fromString(user.getSexuality().toString()));

        return rowPutChange;
    }

    public static User rowToUser(Row row) {
        User user = new User();
        user.setUserId(row.getPrimaryKey().getPrimaryKeyColumn("user_id").getValue().asString());
        user.setUserName(row.getColumn("user_name").get(0).getValue().asString());
        user.setSexuality(Sexuality.valueOf(row.getColumn("sexuality").get(0).getValue().asString()));

        return user;
    }

    public static Conversation timelineMetaToConversation(TimelineMeta timelineMeta) {
        String timelineId = timelineMeta.getIdentifier().getField(0).getValue().asString();

        String groupName = timelineMeta.getString("group_name");
        long createTime = timelineMeta.getLong("create_time");

        Group group = new Group(timelineId, groupName)
                .setCreatedTime(createTime);

        return new Conversation(timelineId, group);
    }

    public static Group timelineMetaToGroup(TimelineMeta timelineMeta) {
        String timelineId = timelineMeta.getIdentifier().getField(0).getValue().asString();

        String groupName = timelineMeta.getString("group_name");
        long createTime = timelineMeta.getLong("create_time");

        Group group = new Group(timelineId, groupName)
                .setCreatedTime(createTime);

        return group;
    }

    public static TimelineIdentifier timelineIdToTimelineIdentifier(String timelineId) {
        TimelineIdentifier identifier = new TimelineIdentifier.Builder()
                .addField("timeline_id", timelineId)
                .build();

        return identifier;
    }

    public static String longToDateString(long timestamp) {
        Date date = new Date(timestamp);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        return sdf.format(date);
    }
}
