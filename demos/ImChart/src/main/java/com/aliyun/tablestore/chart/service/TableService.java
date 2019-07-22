package com.aliyun.tablestore.chart.service;

import com.alicloud.openservices.tablestore.SyncClient;
import com.alicloud.openservices.tablestore.model.*;
import com.alicloud.openservices.tablestore.timeline.TimelineMetaStore;
import com.alicloud.openservices.tablestore.timeline.TimelineStore;
import com.aliyun.tablestore.chart.TimelineV2;
import com.aliyun.tablestore.chart.common.ExampleCons;

public class TableService implements ITableService {
    private SyncClient syncClient;
    private TimelineMetaStore timelineMetaStoreInstance;
    private TimelineStore timelineStoreTableInstance;
    private TimelineStore timelineSyncTableInstance;

    private String userTable = ExampleCons.USER_TABLE;
    private String userRelationTable = ExampleCons.USER_RELATION_TABLE;
    private String groupRelationTable = ExampleCons.GROUP_RELATION_TABLE;
    private String groupRelationGlobalIndex = ExampleCons.GROUP_RELATION_GLOBAL_INDEX;

    private int ttlForever = -1;
    private int maxVersion1 = 1;

    public TableService(SyncClient syncClient) {
        this.syncClient = syncClient;

        TimelineV2 timelineModel = new TimelineV2(syncClient);
        timelineMetaStoreInstance = timelineModel.getTimelineMetaStoreInstance();
        timelineStoreTableInstance = timelineModel.getTimelineStoreTableInstance();
        timelineSyncTableInstance = timelineModel.getTimelineSyncTableInstance();
    }

    public void createUserTable() {
        TableMeta userTableMeta = new TableMeta(userTable);
        userTableMeta.addPrimaryKeyColumn("user_id", PrimaryKeyType.STRING);

        TableOptions options = new TableOptions(ttlForever, maxVersion1);
        CreateTableRequest request = new CreateTableRequest(userTableMeta, options);

        try {
            syncClient.createTable(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dropUserTable() {
        DeleteTableRequest request = new DeleteTableRequest(userTable);

        try {
            syncClient.deleteTable(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createRelationTables() {
        {
            TableMeta userRelationMeta = new TableMeta(userRelationTable);

            userRelationMeta.addPrimaryKeyColumn("main_user", PrimaryKeyType.STRING);
            userRelationMeta.addPrimaryKeyColumn("sub_user", PrimaryKeyType.STRING);

            TableOptions options = new TableOptions(ttlForever, maxVersion1);
            CreateTableRequest request = new CreateTableRequest(userRelationMeta, options);

            try {
                syncClient.createTable(request);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        {
            TableMeta groupRelationMeta = new TableMeta(groupRelationTable);
            groupRelationMeta.addPrimaryKeyColumn("group_id", PrimaryKeyType.STRING);
            groupRelationMeta.addPrimaryKeyColumn("user_id", PrimaryKeyType.STRING);

            TableOptions options = new TableOptions(ttlForever, maxVersion1);

            IndexMeta indexMeta = new IndexMeta(groupRelationGlobalIndex);
            indexMeta.addPrimaryKeyColumn("user_id");

            CreateTableRequest request = new CreateTableRequest(groupRelationMeta, options);
            request.addIndex(indexMeta);

            try {
                syncClient.createTable(request);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void dropRelationTables() {

        {
            DeleteTableRequest request = new DeleteTableRequest(userRelationTable);

            try {
                syncClient.deleteTable(request);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        {
            DeleteIndexRequest request = new DeleteIndexRequest(groupRelationTable, groupRelationGlobalIndex);

            try {
                syncClient.deleteIndex(request);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        {
            DeleteTableRequest request = new DeleteTableRequest(groupRelationTable);

            try {
                syncClient.deleteTable(request);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void createTimelineMetaAndStoreTables() {
        timelineMetaStoreInstance.prepareTables();
        timelineStoreTableInstance.prepareTables();
        timelineSyncTableInstance.prepareTables();
    }

    public void dropTimelineMetaAndStoreTables() {
        timelineMetaStoreInstance.dropAllTables();
        timelineStoreTableInstance.dropAllTables();
        timelineSyncTableInstance.dropAllTables();
    }
}
