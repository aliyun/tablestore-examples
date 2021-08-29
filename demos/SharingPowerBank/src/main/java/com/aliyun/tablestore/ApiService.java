package com.aliyun.tablestore;

import com.alicloud.openservices.tablestore.*;
import com.alicloud.openservices.tablestore.model.*;
import com.alicloud.openservices.tablestore.model.search.*;
import com.alicloud.openservices.tablestore.model.search.groupby.GroupByBuilders;
import com.alicloud.openservices.tablestore.model.search.groupby.GroupByFieldResultItem;
import com.alicloud.openservices.tablestore.model.search.query.QueryBuilders;
import com.alicloud.openservices.tablestore.model.search.sort.FieldSort;
import com.alicloud.openservices.tablestore.model.search.sort.GeoDistanceSort;
import com.alicloud.openservices.tablestore.model.search.sort.Sort;
import com.alicloud.openservices.tablestore.model.search.sort.SortOrder;
import com.alicloud.openservices.tablestore.writer.WriterConfig;
import com.aliyun.tablestore.common.Config;
import com.aliyun.tablestore.common.TablestoreConf;
import com.aliyun.tablestore.utils.CabinetData;
import com.aliyun.tablestore.utils.CabinetTimeData;
import com.aliyun.tablestore.utils.OrderData;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

public class ApiService {

    private AsyncClient asyncClient = null;
    private SyncClient syncClient = null;
    private String seperator = "/";

    public TableStoreWriter cabinetTableStoreWriter;
    public TableStoreWriter cabinetTimeTableStoreWriter;
    public TableStoreWriter orderTableStoreWriter;

    public void clientInit() {
        String os = System.getProperty("os.name");
        if (os.toLowerCase().startsWith("win")) {
            seperator = "\\";
        }
        TablestoreConf conf = TablestoreConf.newInstance(System.getProperty("user.home") + seperator + "tablestoreConf.json");
        asyncClient = new AsyncClient(
                conf.getEndpoint(),
                conf.getAccessId(),
                conf.getAccessKey(),
                conf.getInstanceName());
        syncClient = new SyncClient(
                conf.getEndpoint(),
                conf.getAccessId(),
                conf.getAccessKey(),
                conf.getInstanceName());
    }

    public void writerInit(){
        ClientConfiguration cc = new ClientConfiguration();
        cc.setRetryStrategy(new AlwaysRetryStrategy()); // 可定制重试策略，若需要保证数据写入成功率，可采用更激进的重试策略

        // 初始化
        WriterConfig config = new WriterConfig();
        config.setMaxBatchSize(4 * 1024 * 1024); // 配置一次批量导入请求的大小限制，默认是4MB
        config.setMaxColumnsCount(128); // 配置一行的列数的上限，默认128列
        config.setBufferSize(1024); // 配置内存中最多缓冲的数据行数，默认1024行，必须是2的指数倍
        config.setMaxBatchRowsCount(100); // 配置一次批量导入的行数上限，默认100
        config.setConcurrency(10); // 配置最大并发数，默认10
        config.setMaxAttrColumnSize(2 * 1024 * 1024); // 配置属性列的值大小上限，默认是2MB
        config.setMaxPKColumnSize(1024); // 配置主键列的值大小上限，默认1KB
        config.setFlushInterval(10000); // 配置缓冲区flush的时间间隔，默认10s

        // 配置一个callback，OTSWriter通过该callback反馈哪些导入成功，哪些行导入失败，该callback只简单的统计写入成功和失败的行数。
        AtomicLong succeedCount = new AtomicLong();
        AtomicLong failedCount = new AtomicLong();
        TableStoreCallback<RowChange, ConsumedCapacity> cabinetCallback = new CabinetCallback(succeedCount, failedCount);
        TableStoreCallback<RowChange, ConsumedCapacity> cabinetTimeCallback = new CabinetTimeCallback(succeedCount, failedCount);
        TableStoreCallback<RowChange, ConsumedCapacity> orderCallback = new OrderCallback(succeedCount, failedCount);
        ExecutorService cabinetExecutor = Executors.newFixedThreadPool(8);
        ExecutorService cabinetTimeExecutor = Executors.newFixedThreadPool(8);
        ExecutorService orderExecutor = Executors.newFixedThreadPool(8);
        cabinetTableStoreWriter = new DefaultTableStoreWriter(asyncClient, Config.CABINET_TABLENAME, config, cabinetCallback, cabinetExecutor);
        cabinetTimeTableStoreWriter = new DefaultTableStoreWriter(asyncClient, Config.CABINET_TIME_TABLENAME, config, cabinetTimeCallback, cabinetTimeExecutor);
        orderTableStoreWriter = new DefaultTableStoreWriter(asyncClient, Config.ORDER_TALENAME, config, orderCallback, orderExecutor);
    }



    private static class CabinetTimeCallback implements TableStoreCallback<RowChange, ConsumedCapacity> {
        private AtomicLong succeedCount;
        private AtomicLong failedCount;

        public CabinetTimeCallback(AtomicLong succeedCount, AtomicLong failedCount) {
            this.succeedCount = succeedCount;
            this.failedCount = failedCount;
        }

        @Override
        public void onCompleted(RowChange req, ConsumedCapacity res) {
            succeedCount.incrementAndGet();
        }

        @Override
        public void onFailed(RowChange req, Exception ex) {
            ex.printStackTrace();
            failedCount.incrementAndGet();
        }
    }

    private static class CabinetCallback implements TableStoreCallback<RowChange, ConsumedCapacity> {
        private AtomicLong succeedCount;
        private AtomicLong failedCount;

        public CabinetCallback(AtomicLong succeedCount, AtomicLong failedCount) {
            this.succeedCount = succeedCount;
            this.failedCount = failedCount;
        }

        @Override
        public void onCompleted(RowChange req, ConsumedCapacity res) {
            succeedCount.incrementAndGet();
        }

        @Override
        public void onFailed(RowChange req, Exception ex) {
            ex.printStackTrace();
            failedCount.incrementAndGet();
        }
    }

    private static class OrderCallback implements TableStoreCallback<RowChange, ConsumedCapacity> {
        private AtomicLong succeedCount;
        private AtomicLong failedCount;

        public OrderCallback(AtomicLong succeedCount, AtomicLong failedCount) {
            this.succeedCount = succeedCount;
            this.failedCount = failedCount;
        }

        @Override
        public void onCompleted(RowChange req, ConsumedCapacity res) {
            succeedCount.incrementAndGet();
        }

        @Override
        public void onFailed(RowChange req, Exception ex) {
            ex.printStackTrace();
            failedCount.incrementAndGet();
        }
    }

    /**
     * 创建数据表
     * 元数据表    tablename : cabinet、
     * 订单表      tablename : order、
     * 元数据时序表 tablename : cabinet_time
     */
    public void prepareTables() {

        /**创建元数据表**/
        //设置表属性
        TableOptions cabinetTableOptions = new TableOptions();
        cabinetTableOptions.setMaxVersions(1);//设置最大版本数为1
        cabinetTableOptions.setTimeToLive(-1);//设置数据生命周期为永久
        //设置表结构
        TableMeta cabinetTableMeta = new TableMeta(Config.CABINET_TABLENAME);
        cabinetTableMeta.addPrimaryKeyColumn("cabinet_Md5ID", PrimaryKeyType.STRING);//设置分区键，第一个添加的为分区键
        cabinetTableMeta.addPrimaryKeyColumn("cabinet_ID", PrimaryKeyType.STRING);//设置主键
        //构造创建表请求
        CreateTableRequest createTableRequest_cabinet = new CreateTableRequest(cabinetTableMeta, cabinetTableOptions);
        //发送创建表请求
        syncClient.createTable(createTableRequest_cabinet);
        System.out.println("元数据表创建成功，表名：cabinet");
        /**创建元数据表**/

        /**创建订单表**/
        TableOptions orderTableOptions = new TableOptions();
        orderTableOptions.setMaxVersions(1);
        orderTableOptions.setTimeToLive(-1);
        TableMeta orderTableMeta = new TableMeta(Config.ORDER_TALENAME);
        orderTableMeta.addPrimaryKeyColumn("order_Md5ID", PrimaryKeyType.STRING);
        orderTableMeta.addPrimaryKeyColumn("order_ID", PrimaryKeyType.STRING);
        CreateTableRequest createTableRequestOrder = new CreateTableRequest(orderTableMeta, orderTableOptions);
        syncClient.createTable(createTableRequestOrder);
        System.out.println("订单数据表创建成功，表名：order");
        /**创建订单表**/

        /**创建元数据时序表**/
        TableOptions cabinetTimeTableOptions = new TableOptions();
        cabinetTimeTableOptions.setMaxVersions(1);
        cabinetTimeTableOptions.setTimeToLive(-1);
        TableMeta cabinetTimeTableMeta = new TableMeta(Config.CABINET_TIME_TABLENAME);
        cabinetTimeTableMeta.addPrimaryKeyColumn("cabinet_Md5ID", PrimaryKeyType.STRING);
        cabinetTimeTableMeta.addPrimaryKeyColumn("cabinet_ID", PrimaryKeyType.STRING);
        cabinetTimeTableMeta.addPrimaryKeyColumn("cabinet_state_timestamp", PrimaryKeyType.INTEGER);
        CreateTableRequest createTableRequestCabinetTime = new CreateTableRequest(cabinetTimeTableMeta, cabinetTimeTableOptions);
        syncClient.createTable(createTableRequestCabinetTime);
        System.out.println("元数据时序表创建成功，表名：cabinet_name");
        /**创建元数据时序表**/

        //刚创建的表会初始化一段时间。需要等待
        System.out.println("wait 30s ,表初始化");
        try {
            Thread.sleep(30 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除表
     * @param client
     */
    public void deleteTables(SyncClient client){
        DeleteTableRequest deleteTableRequest1 = new DeleteTableRequest(Config.CABINET_TABLENAME);
        DeleteTableRequest deleteTableRequest2 = new DeleteTableRequest(Config.CABINET_TIME_TABLENAME);
        DeleteTableRequest deleteTableRequest3 = new DeleteTableRequest(Config.ORDER_TALENAME);

        client.deleteTable(deleteTableRequest1);
        client.deleteTable(deleteTableRequest2);
        client.deleteTable(deleteTableRequest3);

        System.out.println("删除表："+Config.CABINET_TABLENAME);
        System.out.println("删除表："+Config.CABINET_TIME_TABLENAME);
        System.out.println("删除表："+Config.ORDER_TALENAME);

    }

    public void cabinetAdd(CabinetData cabinetData){
        RowPutChange rowChange = new RowPutChange(Config.CABINET_TABLENAME);
        rowChange.setPrimaryKey(PrimaryKeyBuilder.createPrimaryKeyBuilder()
                .addPrimaryKeyColumn("cabinet_Md5ID",PrimaryKeyValue.fromString(cabinetData.getCabinetMd5ID()))
                .addPrimaryKeyColumn("cabinet_ID",PrimaryKeyValue.fromString(cabinetData.getCabinetID()))
                .build());
        rowChange.addColumn("cabinet_geo",ColumnValue.fromString(cabinetData.getCabinetGeo()));
        rowChange.addColumn("cabinet_location",ColumnValue.fromString(cabinetData.getCabinetLocation()));
        rowChange.addColumn("cabinet_province",ColumnValue.fromString(cabinetData.getCabinetProvince()));
        rowChange.addColumn("cabinet_available_size",ColumnValue.fromLong(cabinetData.getCabinetAvailableSize()));
        rowChange.addColumn("cabinet_damage_size",ColumnValue.fromLong(cabinetData.getCabinetDamageSize()));
        rowChange.addColumn("cabinet_powerbank_size",ColumnValue.fromLong(cabinetData.getCabinetPowerbankSize()));
        rowChange.addColumn("cabinet_isonline",ColumnValue.fromString(cabinetData.getCabinetIsOnline()));
        rowChange.addColumn("cabinet_powerPercent",ColumnValue.fromDouble(cabinetData.getCabinetPowerPercent()));
        rowChange.addColumn("cabinet_type",ColumnValue.fromString(cabinetData.getCabinetType()));
        rowChange.addColumn("cabinet_manufacturers",ColumnValue.fromString(cabinetData.getCabinetManufacturers()));
        rowChange.addColumn("cabinet_overhaul_time",ColumnValue.fromLong(cabinetData.getCabinetOverhaulTime()));
        rowChange.addColumn("cabinet_pricePerHour",ColumnValue.fromDouble(cabinetData.getCabinetPricePerHour()));
        this.cabinetTimeTableStoreWriter.addRowChangeWithFuture(rowChange);
    }
    public void cabinetTimeAdd(CabinetTimeData cabinetTimeData){

        RowPutChange rowChange = new RowPutChange(Config.CABINET_TIME_TABLENAME);
        CabinetData cabinetData = cabinetTimeData.getCabinetData();
        rowChange.setPrimaryKey(PrimaryKeyBuilder.createPrimaryKeyBuilder()
                .addPrimaryKeyColumn("cabinet_Md5ID",PrimaryKeyValue.fromString(cabinetData.getCabinetMd5ID()))
                .addPrimaryKeyColumn("cabinet_ID",PrimaryKeyValue.fromString(cabinetData.getCabinetID()))
                .addPrimaryKeyColumn("cabinet_state_timestamp",PrimaryKeyValue.fromLong(cabinetTimeData.getCabinetStateTimestamp()))
                .build());
        rowChange.addColumn("cabinet_geo",ColumnValue.fromString(cabinetData.getCabinetGeo()));
        rowChange.addColumn("cabinet_location",ColumnValue.fromString(cabinetData.getCabinetLocation()));
        rowChange.addColumn("cabinet_province",ColumnValue.fromString(cabinetData.getCabinetProvince()));
        rowChange.addColumn("cabinet_available_size",ColumnValue.fromLong(cabinetData.getCabinetAvailableSize()));
        rowChange.addColumn("cabinet_damage_size",ColumnValue.fromLong(cabinetData.getCabinetDamageSize()));
        rowChange.addColumn("cabinet_powerbank_size",ColumnValue.fromLong(cabinetData.getCabinetPowerbankSize()));
        rowChange.addColumn("cabinet_isonline",ColumnValue.fromString(cabinetData.getCabinetIsOnline()));
        rowChange.addColumn("cabinet_powerPercent",ColumnValue.fromDouble(cabinetData.getCabinetPowerPercent()));
        rowChange.addColumn("cabinet_type",ColumnValue.fromString(cabinetData.getCabinetType()));
        rowChange.addColumn("cabinet_manufacturers",ColumnValue.fromString(cabinetData.getCabinetManufacturers()));
        rowChange.addColumn("cabinet_overhaul_time",ColumnValue.fromLong(cabinetData.getCabinetOverhaulTime()));
        rowChange.addColumn("cabinet_pricePerHour",ColumnValue.fromDouble(cabinetData.getCabinetPricePerHour()));
        this.cabinetTimeTableStoreWriter.addRowChangeWithFuture(rowChange);
    }
    public void orderAdd(OrderData orderData){
        RowPutChange rowChange = new RowPutChange(Config.ORDER_TALENAME);
        rowChange.setPrimaryKey(PrimaryKeyBuilder.createPrimaryKeyBuilder()
                .addPrimaryKeyColumn("order_Md5ID",PrimaryKeyValue.fromString(orderData.getOrderMd5ID()))
                .addPrimaryKeyColumn("order_ID",PrimaryKeyValue.fromString(orderData.getOrderID()))
                .build());
        rowChange.addColumn("order_start_time",ColumnValue.fromLong(orderData.getOrderStartTime()));
        rowChange.addColumn("order_end_time",ColumnValue.fromLong(orderData.getOrderEndTime()));
        rowChange.addColumn("order_isRevert",ColumnValue.fromBoolean(orderData.isOrderIsRevert()));
        rowChange.addColumn("order_lose_pay",ColumnValue.fromDouble(orderData.getOrderLosePay()));
        rowChange.addColumn("cabinet_ID",ColumnValue.fromString(orderData.getCabinetID()));
        rowChange.addColumn("order_phone",ColumnValue.fromString(orderData.getOrderPhone()));
        rowChange.addColumn("cabinet_pricePerHour",ColumnValue.fromDouble(orderData.getCabinetPricePerHour()));
        rowChange.addColumn("cabinet_type",ColumnValue.fromString(orderData.getCabinetType()));
        rowChange.addColumn("cabinet_geo",ColumnValue.fromString(orderData.getCabinetGeo()));
        rowChange.addColumn("cabinet_province",ColumnValue.fromString(orderData.getCabinetProvince()));
        this.orderTableStoreWriter.addRowChangeWithFuture(rowChange);
    }

    /**
     * 创建多元索引
     */
    public void createSearchIndex(){
        /**
         * 在元数据表上建立多元索引
         */
        CreateSearchIndexRequest csir = new CreateSearchIndexRequest();
        csir.setTableName(Config.CABINET_TABLENAME);
        csir.setIndexName(Config.CABINET_TABLENAME_INDEX);
        IndexSchema indexSchema = new IndexSchema();
        indexSchema.setFieldSchemas(Arrays.asList(
                new FieldSchema("cabinet_Md5ID", FieldType.KEYWORD),
                new FieldSchema("cabinet_ID", FieldType.KEYWORD),
                new FieldSchema("cabinet_available_size", FieldType.LONG),
                new FieldSchema("cabinet_location", FieldType.KEYWORD),
                new FieldSchema("cabinet_overhaul_time", FieldType.LONG),
                new FieldSchema("cabinet_type", FieldType.KEYWORD),
                new FieldSchema("cabinet_geo", FieldType.GEO_POINT),
                new FieldSchema("cabinet_powerPercent", FieldType.DOUBLE),
                new FieldSchema("cabinet_province", FieldType.KEYWORD),
                new FieldSchema("cabinet_pricePerHour", FieldType.DOUBLE),
                new FieldSchema("cabinet_powerbank_size", FieldType.LONG),
                new FieldSchema("cabinet_damage_size", FieldType.LONG),
                new FieldSchema("cabinet_manufacturers", FieldType.KEYWORD),
                new FieldSchema("cabinet_isonline", FieldType.KEYWORD)
        ));
        csir.setIndexSchema(indexSchema);
        syncClient.createSearchIndex(csir);
        System.out.println("多元索引cabinet_index创建成功");
    }

    /**
     * 删除索引
     */
    public void deleteSearchIndex(){
        DeleteSearchIndexRequest deleteSearchIndexRequest = new DeleteSearchIndexRequest();
        deleteSearchIndexRequest.setTableName(Config.CABINET_TABLENAME);
        deleteSearchIndexRequest.setIndexName(Config.CABINET_TABLENAME_INDEX);
        DeleteSearchIndexResponse deleteSearchIndexResponse = syncClient.deleteSearchIndex(deleteSearchIndexRequest);
        System.out.println("删除索引："+Config.CABINET_TABLENAME_INDEX);
    }

    //场景一。用户需要查询有可租用充电宝的机柜信息，并先按照距离远近倒序排序，再按照机柜时价倒序排序。
    public void searchDemo01(){
        System.out.println("场景一。用户需要查询有可租用充电宝的机柜信息，并先按照距离远近倒序排序，再按照机柜时价倒序排序。");
        SearchRequest searchRequest = SearchRequest.newBuilder()
                .tableName(Config.CABINET_TABLENAME)//设置表名
                .indexName(Config.CABINET_TABLENAME_INDEX)//设置多元索引名
                .searchQuery(SearchQuery.newBuilder()
                        .query(QueryBuilders.bool()
                                .must(QueryBuilders.range("cabinet_available_size").greaterThan(0))//可用充电宝个数>0
                                .must(QueryBuilders.range("cabinet_powerPercent").greaterThan(50))//电量>50
                                .must(QueryBuilders.term("cabinet_isonline","online"))//状态=在线
                        )
                        .sort(new Sort(Arrays.asList(
                                new GeoDistanceSort("cabinet_geo",Arrays.asList("30.17110,120.29937")),//先按照距离远近升序
                                new FieldSort("cabinet_pricePerHour", SortOrder.ASC)//再按照价格升序
                        )))
                        .build())
                .addColumnsToGet(Arrays.asList("cabinet_manufacturers","cabinet_type","cabinet_pricePerHour","cabinet_location"))//指定返回哪些字段
                .build();
        SearchResponse response = syncClient.search(searchRequest);
        System.out.println(response.getRows());
    }

    //场景二。用户需要查询租赁时价在2元/小时之内，并且有可租用充电宝的机柜。按照距离远近排序。
    public void searchDemo02(){
        System.out.println("场景二。用户需要查询租赁时价在2元/小时之内，并且有可租用充电宝的机柜。按照距离远近排序。");
        SearchRequest searchRequest = SearchRequest.newBuilder()
                .tableName(Config.CABINET_TABLENAME)//设置表名
                .indexName(Config.CABINET_TABLENAME_INDEX)//设置多元索引名
                .searchQuery(SearchQuery.newBuilder()
                        .query(QueryBuilders.bool()
                                .must(QueryBuilders.range("cabinet_pricePerHour").greaterThan(0).lessThanOrEqual(2))//时价在0-2元内
                                .must(QueryBuilders.range("cabinet_available_size").greaterThan(0))//可用充电宝个数>0
                                .must(QueryBuilders.term("cabinet_isonline","online"))//状态=在线
                        )
                        .sort(new Sort(Arrays.asList(
                                new GeoDistanceSort("cabinet_geo",Arrays.asList("30.17110,120.29937"))//按照距离远近升序
                        )))
                        .build())
                .addColumnsToGet(Arrays.asList("cabinet_manufacturers","cabinet_type","cabinet_pricePerHour","cabinet_location"))//指定返回哪些字段
                .build();
        SearchResponse response = syncClient.search(searchRequest);
        System.out.println(response.getRows());
    }

    //场景三。运维人员需要查询浙江省内，机柜检修时间戳在半年之前或者已经下线的机柜。取十条记录。
    public void searchDemo03(){
        System.out.println("场景三。运维人员需要查询浙江省内，机柜检修时间戳在半年之前或者已经下线的机柜。取十条记录。");
        SearchRequest searchRequest = SearchRequest.newBuilder()
                .tableName(Config.CABINET_TABLENAME)//设置表名
                .indexName(Config.CABINET_TABLENAME_INDEX)//设置多元索引名
                .searchQuery(SearchQuery.newBuilder()
                        .query(QueryBuilders.bool()
                                .must(QueryBuilders.term("cabinet_province","浙江省"))
                                .must(QueryBuilders.bool()
                                        .should(QueryBuilders.range("cabinet_overhaul_time").lessThan(2592000000L))
                                        .should(QueryBuilders.term("cabinet_isonline","offline"))
                                        .minimumShouldMatch(1)
                                ))
                        .limit(10)
                        .sort(new Sort(Arrays.asList(
                                new GeoDistanceSort("cabinet_geo",Arrays.asList("30.17110,120.29937"))//按照距离远近升序
                        )))
                        .build())
                .addColumnsToGet(Arrays.asList("cabinet_manufacturers","cabinet_type","cabinet_pricePerHour","cabinet_location"))//指定返回哪些字段
                .build();
        SearchResponse response = syncClient.search(searchRequest);
        System.out.println(response.getRows());
    }

    //场景四。运维人员需要统计每个省份已经上线的机柜个数，并按照机柜型号分组，取出前五个型号的数据。
    public void searchDemo04(){
        System.out.println("场景四。运维人员需要统计每个省份已经上线的机柜个数，并按照机柜型号分组，取出前五个型号的数据。");
        SearchRequest searchRequest = SearchRequest.newBuilder()
                .tableName(Config.CABINET_TABLENAME)//设置表名
                .indexName(Config.CABINET_TABLENAME_INDEX)//设置多元索引名
                .searchQuery(SearchQuery.newBuilder()
                        .query(QueryBuilders.matchAll())
                        .addGroupBy(GroupByBuilders.groupByField("groupByProvince","cabinet_province")
                                .addSubGroupBy(GroupByBuilders.groupByField("groupByType","cabinet_type").size(5)))
                        .build())
                .build();
        SearchResponse searchResponse = syncClient.search(searchRequest);
        List<GroupByFieldResultItem> list = searchResponse.getGroupByResults().getAsGroupByFieldResult("groupByProvince").getGroupByFieldResultItems();
        for(GroupByFieldResultItem groupByFieldResultItem : list){
            System.out.println("省份：【"+groupByFieldResultItem.getKey()+"】 机柜数量：【"+groupByFieldResultItem.getRowCount()+"】");
            List<GroupByFieldResultItem> subList = groupByFieldResultItem.getSubGroupByResults().getAsGroupByFieldResult("groupByType").getGroupByFieldResultItems();
            for(GroupByFieldResultItem subItem : subList){
                System.out.print("型号："+subItem.getKey()+"，机柜数量："+subItem.getRowCount()+" 。 ");
            }
            System.out.println();
        }
    }

    /**
     * 关闭writer
     */
    public void shutdown(){
        syncClient.shutdown();
        cabinetTableStoreWriter.close();
        cabinetTimeTableStoreWriter.close();
        orderTableStoreWriter.close();
    }

}
