package com.aliyun.tablestore;

public class MailManagement {

    public static void main(String[] args) {
        ManageService demo = new ManageService("timestream_mail_meta", "timestream_mail_data");
        /**
         * init timestream:
         * the timestream of mail: meta and track data of each mail;
         * */
        demo.init();

        /**
         * create meta table and data table for mail:
         * 1. mailMetaTableName: which stores the mail meta such as mailing id, mail address and mailing destination;
         * 2. mailDataTableName: which stores the location data changing with time;
         * */
        demo.createMailTable();
        demo.writeMail();


        demo.searchMail();
        demo.scanMailTrace();

        /**
         * delete all the dataTables
         * */
        demo.deleteMailTable();

        /**
         * shut down the connections;
         * */
        demo.close();
    }
}
