package com.aliyun.tablestore.example.grid.common;

import com.aliyun.tablestore.grid.TableStoreGrid;
import com.aliyun.tablestore.grid.TableStoreGridConfig;

public abstract class TableStoreGridExample {

    protected TableStoreGrid tableStoreGrid;
    private String pathSeperator = "/";

    public TableStoreGridExample(String dataTableName, String metaTableName) {
        String os = System.getProperty("os.name");
        if (os.toLowerCase().startsWith("win")) {
            pathSeperator = "\\";
        }
        TableStoreConf conf = TableStoreConf.newInstance(System.getProperty("user.home") + pathSeperator + "tablestoreConf.json");
        TableStoreGridConfig config = new TableStoreGridConfig();
        config.setTableStoreEndpoint(conf.getEndpoint());
        config.setAccessId(conf.getAccessId());
        config.setAccessKey(conf.getAccessKey());
        config.setTableStoreInstance(conf.getInstanceName());
        config.setDataTableName(dataTableName);
        config.setMetaTableName(metaTableName);
        tableStoreGrid = new TableStoreGrid(config);
    }

    public void close() {
        if (tableStoreGrid != null) {
            tableStoreGrid.close();
        }
    }
}
