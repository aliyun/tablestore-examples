package com.aliyun.tablestore;

public class WifiMonitor {

    public static void main(String[] args) {
        ManageService demo = new ManageService("timestream_monitor_meta", "timestream_monitor_data");
        /**
         * init the timestream of machine monitor;
         * */
        demo.init();
//        demo.deleteTable();
        /**
         /**
         * create meta table and data table for machine:
         * 1. metaTableName: which stores the mail meta such as monitor metric, region, machineId and running services;
         * 2. dataTableName: which stores the machine running status data;
         * */
//        demo.createTable();

        /**
         * 1. put some monitor meta info into metaTable;
         * 2. and some status data during a period of time, such as cpu, ram;
         * */
//        demo.writeData();

        /**
         * meta iterator：search by multi combined conditions ；
         * data iterator：search by meta identifier and time range；
         * */
        demo.readMetaAndData();

        /**
         * delete all the dataTables
         * */
//        demo.deleteTable();

        /**
         * shutdown the connections;
         * */
        demo.close();
    }
}
