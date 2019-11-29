package com.aliyun.tablestore.basic.dataManage.base;

import com.alicloud.openservices.tablestore.SyncClient;
import com.alicloud.openservices.tablestore.model.*;
import com.aliyun.tablestore.basic.common.Config;

import static com.aliyun.tablestore.basic.common.Consts.*;

/**
 * @Author wtt
 * @create 2019/11/28 4:22 PM
 */
public class BaseManage {
    protected Config config;
    protected SyncClient syncClient;
    private String pathSeperator = "/";

    public BaseManage() {
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

        tryInitTable(syncClient);
    }


    /**
     * init table when start run
     **/
    private static void tryInitTable(SyncClient syncClient) {
        TableMeta tableMeta = new TableMeta(TABLE_NAME);
        tableMeta.addPrimaryKeyColumn(PK1, PK1_TYPE);
        tableMeta.addPrimaryKeyColumn(PK2, PK2_TYPE);

        TableOptions tableOptions = new TableOptions();
        tableOptions.setTimeToLive(TTL);
        tableOptions.setMaxVersions(MAX_VERSION);
        CreateTableRequest createTableRequest = new CreateTableRequest(tableMeta, tableOptions);

        try {
            syncClient.createTable(createTableRequest);
        } catch (Exception e) {
            if (!"Requested table already exists.".equals(e.getMessage())) {
                System.out.println("Init Table Exception: " + e.getMessage());
                e.printStackTrace();
                return;
            }
        }

//        TableMeta tableMeta_Inc = new TableMeta(TABLE_NAME_INC);
//        tableMeta_Inc.addPrimaryKeyColumn(PK1, PK1_TYPE);
//        tableMeta_Inc.addPrimaryKeyColumn(PK2, PK2_TYPE);
//        tableMeta_Inc.addPrimaryKeyColumn(PK3, PK3_TYPE, PrimaryKeyOption.AUTO_INCREMENT);
//
//        TableOptions tableOptions_Inc = new TableOptions();
//        tableOptions_Inc.setTimeToLive(TTL);
//        tableOptions_Inc.setMaxVersions(MAX_VERSION);
//        CreateTableRequest createTableRequest_Inc = new CreateTableRequest(tableMeta_Inc, tableOptions_Inc);
//
//        try {
//            syncClient.createTable(createTableRequest_Inc);
//        } catch (Exception e) {
//            if (!"Requested table already exists.".equals(e.getMessage())) {
//                System.out.println("Init Table Exception: " + e.getMessage());
//                e.printStackTrace();
//                return;
//            }
//        }

        System.out.println(String.format("Init Table Succeed!\n\tTableMeta: %s\n\tTableOptions: %s",
                tableMeta.toString(),
                tableOptions.toString()));
    }

    /**
     * safe close and drop table by user's param
     **/
    public void close(boolean deleteTable) {
        if (deleteTable) {
            DeleteTableRequest deleteTableRequest = new DeleteTableRequest(TABLE_NAME);
            try {
                syncClient.deleteTable(deleteTableRequest);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

//            DeleteTableRequest deleteTableRequest_Inc = new DeleteTableRequest(TABLE_NAME_INC);
//            try {
//                syncClient.deleteTable(deleteTableRequest_Inc);
//            } catch (Exception e) {
//                e.printStackTrace();
//                return;
//            }
//            System.out.println("Delete Table Succeed!");
        }
        close();
    }

    public void close() {
        if (syncClient != null) {
            syncClient.shutdown();
        }
        System.out.println("Save Close!");
    }

    /**
     * BaseManage Test main
     */
    public static void main(String[] args) {
        BaseManage baseManage = new BaseManage();


        baseManage.close(true);
    }
}
