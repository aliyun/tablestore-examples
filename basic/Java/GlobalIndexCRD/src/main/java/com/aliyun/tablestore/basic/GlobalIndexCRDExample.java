package com.aliyun.tablestore.basic;

import com.alicloud.openservices.tablestore.model.*;
import com.alicloud.openservices.tablestore.model.search.SearchResponse;
import com.aliyun.tablestore.basic.common.Config;
import com.aliyun.tablestore.basic.model.CallDO;

import java.util.Arrays;

import static com.aliyun.tablestore.basic.common.Consts.*;

public class GlobalIndexCRDExample extends BaseExample {

    public GlobalIndexCRDExample(Config config) {
        super(config);
    }

    private void createTableWithGlobalIndex() {
        //二级索引IndexMeta
        IndexMeta indexMeta = new IndexMeta(INDEX_NAME);
        indexMeta.addPrimaryKeyColumn(CALLED_NUMBER);       //将主表的预定义列"called_number"作为二级索引表的pk列
        //此时会自动补齐二级索引表的剩余两个pk列: "cell_number", "start_time"
        indexMeta.addDefinedColumn(BASE_STATION_NUMBER);    //将主表的预定义列"base_station_number"作为二级索引表的属性列

        //创建主表时，同时创建索引表
        TableMeta tableMeta = new TableMeta(TABLE_NAME);

        tableMeta.addPrimaryKeyColumn(CELL_NUMBER, PrimaryKeyType.INTEGER);
        tableMeta.addPrimaryKeyColumn(START_TIME, PrimaryKeyType.INTEGER);

        tableMeta.addDefinedColumn(new DefinedColumnSchema(CALLED_NUMBER, DefinedColumnType.INTEGER));
        tableMeta.addDefinedColumn(new DefinedColumnSchema(BASE_STATION_NUMBER, DefinedColumnType.INTEGER));

        TableOptions tableOptions = new TableOptions(-1, 1);
        CreateTableRequest createTableRequest = new CreateTableRequest(tableMeta, tableOptions, Arrays.asList(indexMeta));
        syncClient.createTable(createTableRequest);
        System.out.println("Create table with global index success");

    }

    private void createTable() {
        TableMeta tableMeta = new TableMeta(TABLE_NAME);

        tableMeta.addPrimaryKeyColumn(CELL_NUMBER, PrimaryKeyType.INTEGER);
        tableMeta.addPrimaryKeyColumn(START_TIME, PrimaryKeyType.INTEGER);

        tableMeta.addDefinedColumn(new DefinedColumnSchema(CALLED_NUMBER, DefinedColumnType.INTEGER));
        tableMeta.addDefinedColumn(new DefinedColumnSchema(BASE_STATION_NUMBER, DefinedColumnType.INTEGER));

        // Set TTL to -1, never expire; Set maxVersions to 1, as one version is permitted
        TableOptions tableOptions = new TableOptions(-1, 1);
        CreateTableRequest createTableRequest = new CreateTableRequest(tableMeta, tableOptions);
        syncClient.createTable(createTableRequest);
        System.out.println("Create table success");
    }

    private void createGlobalIndex() {
        IndexMeta indexMeta = new IndexMeta(INDEX_NAME);
        indexMeta.addPrimaryKeyColumn(CALLED_NUMBER);       //将主表的预定义列"called_number"作为二级索引表的pk列
                                                            //此时会自动补齐二级索引表的剩余两个pk列: "cell_number", "start_time"
        indexMeta.addDefinedColumn(BASE_STATION_NUMBER);    //将主表的预定义列"base_station_number"作为二级索引表的属性列

        //全局二级索引索引创建请求(includeBaseData为true表示先同步主表全量数据，再同步增量数据; includeBaseData为false表示只同步增量数据)
        CreateIndexRequest request = new CreateIndexRequest(TABLE_NAME, indexMeta, true);

        //创建全局二级索引
        syncClient.createIndex(request);
        System.out.println("Create Global Index " + INDEX_NAME + " success");
    }

    private void deleteGlobalIndex() {
        DeleteIndexRequest request = new DeleteIndexRequest(TABLE_NAME, INDEX_NAME);
        syncClient.deleteIndex(request);
    }

    private void deleteTable() {
        DeleteTableRequest request = new DeleteTableRequest(TABLE_NAME);
        syncClient.deleteTable(request);
    }

    private void mockData() {
        BatchWriteRowRequest batchWriteRowRequest = new BatchWriteRowRequest();
        {
            CallDO callDO = new CallDO();
            callDO.setCellNumber(123456L);
            callDO.setStartTime(1532574644L);
            callDO.setCalledNumber(654321L);
            callDO.setDuration(60L);
            callDO.setBaseStationNumber(1L);

            batchWriteRowRequest.addRowChange(callDO.toRowPutChange(TABLE_NAME));
        }
        {
            CallDO callDO = new CallDO();
            callDO.setCellNumber(234567L);
            callDO.setStartTime(1532574714L);
            callDO.setCalledNumber(765432L);
            callDO.setDuration(10L);
            callDO.setBaseStationNumber(1L);

            batchWriteRowRequest.addRowChange(callDO.toRowPutChange(TABLE_NAME));
        }
        {
            CallDO callDO = new CallDO();
            callDO.setCellNumber(234567L);
            callDO.setStartTime(1532574734L);
            callDO.setCalledNumber(123456L);
            callDO.setDuration(20L);
            callDO.setBaseStationNumber(3L);

            batchWriteRowRequest.addRowChange(callDO.toRowPutChange(TABLE_NAME));
        }
        {
            CallDO callDO = new CallDO();
            callDO.setCellNumber(345678L);
            callDO.setStartTime(1532574795L);
            callDO.setCalledNumber(123456L);
            callDO.setDuration(5L);
            callDO.setBaseStationNumber(2L);

            batchWriteRowRequest.addRowChange(callDO.toRowPutChange(TABLE_NAME));
        }
        {
            CallDO callDO = new CallDO();
            callDO.setCellNumber(345678L);
            callDO.setStartTime(1532574861L);
            callDO.setCalledNumber(123456L);
            callDO.setDuration(100L);
            callDO.setBaseStationNumber(2L);

            batchWriteRowRequest.addRowChange(callDO.toRowPutChange(TABLE_NAME));
        }
        {
            CallDO callDO = new CallDO();
            callDO.setCellNumber(456789L);
            callDO.setStartTime(1532584054L);
            callDO.setCalledNumber(345678L);
            callDO.setDuration(200L);
            callDO.setBaseStationNumber(3L);

            batchWriteRowRequest.addRowChange(callDO.toRowPutChange(TABLE_NAME));
        }

        syncClient.batchWriteRow(batchWriteRowRequest);

        try {
            Thread.sleep(30_000);
        } catch (InterruptedException e) {}
    }

    private void printResult(SearchResponse searchResponse) {
        System.out.println("Total counts: " + searchResponse.getTotalCount());
        for (Row row : searchResponse.getRows()) {
            System.out.println(CallDO.fromRow(row));
        }
    }

    /**
     * 查询号码123456的被叫话单
     */
    private void queryCalledNumber() {
        RangeRowQueryCriteria rangeRowQueryCriteria = new RangeRowQueryCriteria(INDEX_NAME);

        long calledNumber = 123456L;

        // 构造主键
        PrimaryKeyBuilder startPrimaryKeyBuilder = PrimaryKeyBuilder.createPrimaryKeyBuilder();
        startPrimaryKeyBuilder.addPrimaryKeyColumn(CALLED_NUMBER, PrimaryKeyValue.fromLong(calledNumber));
        startPrimaryKeyBuilder.addPrimaryKeyColumn(CELL_NUMBER, PrimaryKeyValue.INF_MIN);
        startPrimaryKeyBuilder.addPrimaryKeyColumn(START_TIME, PrimaryKeyValue.INF_MIN);
        rangeRowQueryCriteria.setInclusiveStartPrimaryKey(startPrimaryKeyBuilder.build());

        // 构造主键
        PrimaryKeyBuilder endPrimaryKeyBuilder = PrimaryKeyBuilder.createPrimaryKeyBuilder();
        endPrimaryKeyBuilder.addPrimaryKeyColumn(CALLED_NUMBER, PrimaryKeyValue.fromLong(calledNumber));
        endPrimaryKeyBuilder.addPrimaryKeyColumn(CELL_NUMBER, PrimaryKeyValue.INF_MAX);
        endPrimaryKeyBuilder.addPrimaryKeyColumn(START_TIME, PrimaryKeyValue.INF_MAX);
        rangeRowQueryCriteria.setExclusiveEndPrimaryKey(endPrimaryKeyBuilder.build());

        rangeRowQueryCriteria.setMaxVersions(1);
        rangeRowQueryCriteria.addColumnsToGet(BASE_STATION_NUMBER); //查询二级索引，返回pk列和属性列"base_station_number"

        System.out.println(String.format("号码 %d 的所有被叫话单: ", calledNumber));
        while (true) {
            GetRangeResponse getRangeResponse = syncClient.getRange(new GetRangeRequest(rangeRowQueryCriteria));
            for (Row row : getRangeResponse.getRows()) {
                System.out.println(row);
            }

            // 若nextStartPrimaryKey不为null, 则继续读取.
            if (getRangeResponse.getNextStartPrimaryKey() != null) {
                rangeRowQueryCriteria.setInclusiveStartPrimaryKey(getRangeResponse.getNextStartPrimaryKey());
            } else {
                break;
            }
        }
    }

    private void listGlobalIndex() {
        DescribeTableRequest request = new DescribeTableRequest(TABLE_NAME);
        DescribeTableResponse response = syncClient.describeTable(request);
        for (IndexMeta indexMeta : response.getIndexMeta()) {
            System.out.println(indexMeta.getIndexName());
        }
    }

    private void describeGlobalIndex() {
        DescribeTableResponse response = syncClient.describeTable(new DescribeTableRequest(INDEX_NAME));
        System.out.println(response.getTableMeta());
    }

    @Override
    protected void doMain() {
        //方式一：创建主表的同时创建全局二级索引表
        createTableWithGlobalIndex();

//        //方式二：先创建主表；再为已经存在的主表添加二级索引表
//        createTable();
//        createGlobalIndex();

        listGlobalIndex();
        describeGlobalIndex();

        mockData();

        queryCalledNumber();

//        deleteGlobalIndex();
//        deleteTable();
    }

    public static void main(String[] args) {
        new GlobalIndexCRDExample(Config.newInstance()).main();
    }
}
