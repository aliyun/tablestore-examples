package com.aliyun.tablestore.example;

import com.alicloud.openservices.tablestore.SyncClient;
import com.aliyun.tablestore.example.utils.ClientAndConfig;

public abstract class BaseExample {

    protected final SyncClient syncClient;

    protected final String tableName;

    protected final String indexName;

    public BaseExample(ClientAndConfig clientAndConfig) {
        this.syncClient = clientAndConfig.getSyncClient();
        this.tableName = clientAndConfig.getTableName();
        this.indexName = clientAndConfig.getIndexName();
    }

    protected abstract void doMain();

    protected void main() {
        try {
            doMain();
        } finally {
            syncClient.shutdown();
        }

    }
}
