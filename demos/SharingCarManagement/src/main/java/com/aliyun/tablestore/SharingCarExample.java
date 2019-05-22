package com.aliyun.tablestore;

public class SharingCarExample {

    public static void main(String[] args) {
        ApiService demo = new ApiService("sharingCarMeta", "sharingCarData");
        /**
         * init TimeStream and Order Client:
         * the TimeStream of sharingCar: meta and monitor data of each sharing car;
         * */
        demo.init();

        /**
         * 1. Create meta table and data table for sharing car by TimeStream;
         * 2. Create order table and index by TableStore api;
         * */
        demo.createShareCarTable();
        demo.createOrderTableAndIndex();



        demo.insertCarAndMonitor();
        demo.insertCarOrder();


        /**
         * Wait for a wile for the sync of index;
         * */
        demo.waitForSync(30000);

        /**
         * 1. Search car by params;
         * 2. Search car order;
         * 3. Get car monitor within an order;
         *
         * */
        demo.searchCar();
        demo.searchOrder();
        demo.getOrderMonitor();

        /**
         * delete all the tables and index
         * */
        demo.deleteShareCarTable();
        demo.deleteOrderIndexAndTable();

        /**
         * shut down the connections;
         * */
        demo.close();
    }
}
