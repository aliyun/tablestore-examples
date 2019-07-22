package com.aliyun.tablestore.example;

import com.alicloud.openservices.tablestore.SyncClient;
import com.aliyun.tablestore.chart.common.Config;

public abstract class BaseExample {
    protected Config config;
    protected SyncClient syncClient;
    private String pathSeperator = "/";

    public BaseExample() {
        String os = System.getProperty("os.name");
        if (os.toLowerCase().startsWith("win")) {
            pathSeperator = "\\";
        }
        config = Config.newInstance(System.getProperty("user.home") + pathSeperator + "tablestoreConf.json");

        syncClient = new SyncClient(
                config.getEndpoint(),
                config.getAccessId(),
                config.getAccessKey(),
                config.getInstanceName()
        );
    }
}
