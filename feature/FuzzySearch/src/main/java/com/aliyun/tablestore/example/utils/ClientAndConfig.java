package com.aliyun.tablestore.example.utils;

import com.alicloud.openservices.tablestore.SyncClient;

public class ClientAndConfig {

    private final String tableName;

    private final String indexName;

    private final SyncClient syncClient;

    private final int importDataCount;


    public ClientAndConfig(String tableName, String indexName, SyncClient syncClient, int importDataCount) {
        this.tableName = tableName;
        this.indexName = indexName;
        this.syncClient = syncClient;
        this.importDataCount = importDataCount;
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

    public int getImportDataCount() {
        return importDataCount;
    }
}
