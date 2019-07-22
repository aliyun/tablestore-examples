package com.aliyun.tablestore.chart.service;

public interface ITableService {

    public void createUserTable();

    public void dropUserTable();

    public void createRelationTables();

    public void dropRelationTables();

    public void createTimelineMetaAndStoreTables();

    public void dropTimelineMetaAndStoreTables();
}
