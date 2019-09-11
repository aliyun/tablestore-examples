package com.aliyun.tablestore.example.utils;

import com.alicloud.openservices.tablestore.SyncClient;

public class ClientAndConfig {

    private final String tableName;

    private final String indexName;

    private final SyncClient syncClient;


    public ClientAndConfig(String tableName, String indexName, SyncClient syncClient, int importDataCount) {
        this.tableName = tableName;
        this.indexName = indexName;
        this.syncClient = syncClient;
    }

    public String getTableName() {
        return tableName;
    }

    public String getIndexName() {
        return indexName;
    }

    public SyncClient getSyncClient() {
        return syncClient;
    }
}
