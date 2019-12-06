package com.aliyun.tablestore.basic.dataManage.base;

import com.alicloud.openservices.tablestore.SyncClient;
import com.alicloud.openservices.tablestore.model.*;
import com.aliyun.tablestore.basic.common.Config;

import static com.aliyun.tablestore.basic.common.Consts.*;

/**
 * @Author wtt
 * @create 2019/11/28 4:22 PM
 */
public class BaseAutoIncManage {
    protected Config config;
    protected SyncClient syncClient;
    private String pathSeparator = "/";

    public BaseAutoIncManage() {
        String os = System.getProperty("os.name");
        if (os.toLowerCase().startsWith("win")) {
            pathSeparator = "\\";
        }
        config = Config.newInstance(System.getProperty("user.home") + pathSeparator + "tablestoreConf.json");

        syncClient = config.newClient();

        tryInitTable(syncClient);
    }


    /**
     * init table when start run
     **/
    private static void tryInitTable(SyncClient syncClient) {
        TableMeta tableMeta = new TableMeta(TABLE_NAME_INC);
        tableMeta.addPrimaryKeyColumn(PK1, PK1_TYPE);
        tableMeta.addPrimaryKeyColumn(PK2, PK2_TYPE);
        tableMeta.addPrimaryKeyColumn(PK3, PK3_TYPE, PrimaryKeyOption.AUTO_INCREMENT);

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

        System.out.println(String.format("Init Table Succeed!\n\tTableMeta: %s\n\tTableOptions: %s",
                tableMeta.toString(),
                tableOptions.toString()));
    }

    /**
     * safe close and drop table by user's param
     **/
    public void close(boolean deleteTable) {
        if (deleteTable) {
            DeleteTableRequest deleteTableRequest = new DeleteTableRequest(TABLE_NAME_INC);
            try {
                syncClient.deleteTable(deleteTableRequest);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
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
        BaseAutoIncManage baseManage = new BaseAutoIncManage();


        baseManage.close(true);
    }
}
