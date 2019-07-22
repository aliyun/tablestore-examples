package com.aliyun.tablestore.example;

import com.aliyun.tablestore.chart.service.ITableService;
import com.aliyun.tablestore.chart.service.TableService;

public class InitChartRoomExample extends BaseExample {

    public static void main(String[] args) {
        InitChartRoomExample example = new InitChartRoomExample();

        ITableService initTableService = new TableService(example.syncClient);

        initTableService.createUserTable();
        System.out.println("[Create Table]: im_user_table");

        initTableService.createRelationTables();
        System.out.println("[Create Table]: im_user_relation_table, im_group_relation_table");

        initTableService.createTimelineMetaAndStoreTables();
        System.out.println("[Create Table]: im_timeline_meta_table, im_timeline_store_table, im_timeline_sync_table");
    }
}
