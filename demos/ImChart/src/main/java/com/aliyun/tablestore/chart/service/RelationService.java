package com.aliyun.tablestore.chart.service;

import com.alicloud.openservices.tablestore.SyncClient;
import com.alicloud.openservices.tablestore.model.*;
import com.aliyun.tablestore.chart.common.ExampleCons;

public class RelationService implements IRelationService {
    private SyncClient syncClient;
    private String userTableName = ExampleCons.USER_TABLE;
    private String userRelationTable = ExampleCons.USER_RELATION_TABLE;
    private String groupRelationTable = ExampleCons.GROUP_RELATION_TABLE;

    public RelationService (SyncClient syncClient) {
        this.syncClient = syncClient;
    }

    public void establishFriendship(String userA, String userB, String timelineId) {
        PrimaryKey primaryKeyA = PrimaryKeyBuilder.createPrimaryKeyBuilder()
                .addPrimaryKeyColumn("main_user", PrimaryKeyValue.fromString(userA))
                .addPrimaryKeyColumn("sub_user", PrimaryKeyValue.fromString(userB))
                .build();

        RowPutChange rowPutChangeA = new RowPutChange(userRelationTable, primaryKeyA);
        rowPutChangeA.addColumn("timeline_id", ColumnValue.fromString(timelineId));

        PrimaryKey primaryKeyB = PrimaryKeyBuilder.createPrimaryKeyBuilder()
                .addPrimaryKeyColumn("main_user", PrimaryKeyValue.fromString(userB))
                .addPrimaryKeyColumn("sub_user", PrimaryKeyValue.fromString(userA))
                .build();

        RowPutChange rowPutChangeB = new RowPutChange(userRelationTable, primaryKeyB);
        rowPutChangeB.addColumn("timeline_id", ColumnValue.fromString(timelineId));

        BatchWriteRowRequest request = new BatchWriteRowRequest();
        request.addRowChange(rowPutChangeA);
        request.addRowChange(rowPutChangeB);

        syncClient.batchWriteRow(request);
    }

    public void breakupFriendship(String userA, String userB) {
        PrimaryKey primaryKeyA = PrimaryKeyBuilder.createPrimaryKeyBuilder()
                .addPrimaryKeyColumn("main_user", PrimaryKeyValue.fromString(userA))
                .addPrimaryKeyColumn("sub_user", PrimaryKeyValue.fromString(userB))
                .build();

        RowDeleteChange rowPutChangeA = new RowDeleteChange(userRelationTable, primaryKeyA);

        PrimaryKey primaryKeyB = PrimaryKeyBuilder.createPrimaryKeyBuilder()
                .addPrimaryKeyColumn("main_user", PrimaryKeyValue.fromString(userB))
                .addPrimaryKeyColumn("sub_user", PrimaryKeyValue.fromString(userA))
                .build();

        RowDeleteChange rowPutChangeB = new RowDeleteChange(userRelationTable, primaryKeyB);

        BatchWriteRowRequest request = new BatchWriteRowRequest();
        request.addRowChange(rowPutChangeA);
        request.addRowChange(rowPutChangeB);

        syncClient.batchWriteRow(request);
    }


    public void joinGroup(String userId, String groupTimelineId) {
        PrimaryKey primaryKey = PrimaryKeyBuilder.createPrimaryKeyBuilder()
                .addPrimaryKeyColumn("group_id", PrimaryKeyValue.fromString(groupTimelineId))
                .addPrimaryKeyColumn("user_id", PrimaryKeyValue.fromString(userId))
                .build();

        RowPutChange rowPutChange = new RowPutChange(groupRelationTable, primaryKey);
        rowPutChange.addColumn("join_time", ColumnValue.fromLong(System.currentTimeMillis()));

        PutRowRequest request = new PutRowRequest();
        request.setRowChange(rowPutChange);

        syncClient.putRow(request);
    }

    public void leaveGroup(String userId, String groupTimelineId) {
        PrimaryKey primaryKey = PrimaryKeyBuilder.createPrimaryKeyBuilder()
                .addPrimaryKeyColumn("group_id", PrimaryKeyValue.fromString(groupTimelineId))
                .addPrimaryKeyColumn("user_id", PrimaryKeyValue.fromString(userId))
                .build();

        RowDeleteChange rowPutChange = new RowDeleteChange(groupRelationTable, primaryKey);

        DeleteRowRequest request = new DeleteRowRequest();
        request.setRowChange(rowPutChange);

        syncClient.deleteRow(request);
    }

}
