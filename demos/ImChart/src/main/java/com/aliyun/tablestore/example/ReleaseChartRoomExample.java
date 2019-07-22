package com.aliyun.tablestore.example;

import com.aliyun.tablestore.chart.service.ITableService;
import com.aliyun.tablestore.chart.service.TableService;

public class ReleaseChartRoomExample extends BaseExample {

    public static void main(String[] args) {
        ReleaseChartRoomExample example = new ReleaseChartRoomExample();

        ITableService initTableService = new TableService(example.syncClient);

        initTableService.dropUserTable();
        System.out.println("[Drop Table]: im_user_table");

        initTableService.dropRelationTables();
        System.out.println("[Drop Table]: im_user_relation_table, im_group_relation_table");

        initTableService.dropTimelineMetaAndStoreTables();
        System.out.println("[Drop Table]: im_timeline_meta_table, im_timeline_store_table, im_timeline_sync_table");
    }
}
