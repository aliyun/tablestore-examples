package com.aliyun.tablestore.basic;

import com.alicloud.openservices.tablestore.SyncClient;
import com.aliyun.tablestore.basic.common.Config;

public abstract class BaseExample {

    protected final SyncClient syncClient;


    public BaseExample(Config config) {
        syncClient = config.newClient();
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
