/***********************************************************
 * Author：潭潭
 * 样例说明：
 *  基于表格存储的时序模型[Timestream]创建的快递轨迹管理系统
 * 功能：
 *  1、快递的meta管理、查询；
 *  2、快递轨迹路线追踪；
 *  3、快递员meta管理、查询；
 * *********************************************************/
package com.aliyun.tablestore;

import com.alicloud.openservices.tablestore.AsyncClient;
import com.alicloud.openservices.tablestore.model.*;
import com.alicloud.openservices.tablestore.model.search.*;
import com.alicloud.openservices.tablestore.model.search.query.MatchAllQuery;
import com.alicloud.openservices.tablestore.model.search.query.Query;
import com.alicloud.openservices.tablestore.model.search.sort.FieldSort;
import com.alicloud.openservices.tablestore.model.search.sort.Sort;
import com.alicloud.openservices.tablestore.model.search.sort.SortOrder;
import com.alicloud.openservices.tablestore.timestream.TimestreamDBClient;
import com.alicloud.openservices.tablestore.timestream.TimestreamDBConfiguration;
import com.alicloud.openservices.tablestore.timestream.TimestreamDataTable;
import com.alicloud.openservices.tablestore.timestream.TimestreamMetaTable;
import com.alicloud.openservices.tablestore.timestream.model.AttributeIndexSchema;
import com.alicloud.openservices.tablestore.timestream.model.Point;
import com.alicloud.openservices.tablestore.timestream.model.TimeRange;
import com.alicloud.openservices.tablestore.timestream.model.TimestreamIdentifier;
import com.alicloud.openservices.tablestore.timestream.model.TimestreamMeta;
import com.alicloud.openservices.tablestore.timestream.model.filter.*;
import com.aliyun.tablestore.common.Conf;
import com.aliyun.tablestore.model.Car;
import com.aliyun.tablestore.model.MonitorPoint;
import com.aliyun.tablestore.model.Order;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.alicloud.openservices.tablestore.timestream.model.filter.FilterFactory.and;
import static com.alicloud.openservices.tablestore.timestream.model.filter.FilterFactory.or;
import static com.aliyun.tablestore.common.Util.*;
import static org.apache.commons.codec.digest.DigestUtils.md5Hex;


public class ApiService {
    private String carMetaTableName = "sharingCarMeta";
    private String carDataTableName = "sharingCarData";
    private String carOrderTableName = "sharingCarOrderName";
    private String carOrderIndexName = "sharingCarOrderIndex";
    private TimestreamDBClient shareCarTable;

    private AsyncClient asyncClient = null;
    private String seperator = "/";

    ApiService(String carMetaTableName, String carDataTableName) {
        this.carMetaTableName = carMetaTableName;
        this.carDataTableName = carDataTableName;
    }

    public void init() {
        String os = System.getProperty("os.name");
        if (os.toLowerCase().startsWith("win")) {
            seperator = "\\";
        }
        Conf conf = Conf.newInstance(System.getProperty("user.home") + seperator + "tablestoreConf.json");
        asyncClient = new AsyncClient(
                conf.getEndpoint(),
                conf.getAccessId(),
                conf.getAccessKey(),
                conf.getInstanceName());

        TimestreamDBConfiguration mailConf = new TimestreamDBConfiguration(carMetaTableName);
        shareCarTable = new TimestreamDBClient(asyncClient, mailConf);
    }


    /**************************************** management of mails ****************************************/
    public void createShareCarTable() {
        shareCarTable.createMetaTable(Arrays.asList(
                new AttributeIndexSchema("carNo", AttributeIndexSchema.Type.KEYWORD),
                new AttributeIndexSchema("company", AttributeIndexSchema.Type.KEYWORD),
                new AttributeIndexSchema("province", AttributeIndexSchema.Type.KEYWORD),
                new AttributeIndexSchema("type", AttributeIndexSchema.Type.KEYWORD),
                new AttributeIndexSchema("status", AttributeIndexSchema.Type.KEYWORD),
                new AttributeIndexSchema("seats", AttributeIndexSchema.Type.LONG),
                new AttributeIndexSchema("remain", AttributeIndexSchema.Type.DOUBLE),
                new AttributeIndexSchema("expected", AttributeIndexSchema.Type.DOUBLE),
                new AttributeIndexSchema("location", AttributeIndexSchema.Type.GEO_POINT)
        ));
        shareCarTable.createDataTable(carDataTableName);
    }

    public void createOrderTableAndIndex() {
        this.createOrderTable();
        this.createOrderIndex();
    }

    private void createOrderTable() {
        TableMeta tablemeta = new TableMeta(carOrderTableName);
        tablemeta.addPrimaryKeyColumn("hash", PrimaryKeyType.STRING);
        tablemeta.addPrimaryKeyColumn("orderId", PrimaryKeyType.STRING);
        CreateTableRequest createTableRequest = new CreateTableRequest(tablemeta, new TableOptions(-1, 1));
        asyncClient.asSyncClient().createTable(createTableRequest);
    }

    private void createOrderIndex() {
        IndexSchema indexSchema = new IndexSchema();
        indexSchema.setFieldSchemas(Arrays.asList(
                new FieldSchema("orderId", FieldType.KEYWORD).setIndex(true).setEnableSortAndAgg(true).setStore(true),
                new FieldSchema("carNo", FieldType.KEYWORD).setIndex(true).setEnableSortAndAgg(true).setStore(true),
                new FieldSchema("mobile", FieldType.KEYWORD).setIndex(true).setEnableSortAndAgg(true).setStore(true),
                new FieldSchema("from", FieldType.LONG).setIndex(true).setEnableSortAndAgg(true).setStore(true),
                new FieldSchema("to", FieldType.LONG).setIndex(true).setEnableSortAndAgg(true).setStore(true),
                new FieldSchema("userName", FieldType.KEYWORD).setIndex(true).setEnableSortAndAgg(true).setStore(true),
                new FieldSchema("expense", FieldType.DOUBLE).setIndex(true).setEnableSortAndAgg(true).setStore(true),
                new FieldSchema("status", FieldType.KEYWORD).setIndex(true).setEnableSortAndAgg(true).setStore(true)
        ));


        CreateSearchIndexRequest request = new CreateSearchIndexRequest();
        request.setTableName(carOrderTableName);
        request.setIndexName(carOrderIndexName);
        request.setIndexSchema(indexSchema);

        asyncClient.asSyncClient().createSearchIndex(request);
    }

    public void deleteShareCarTable() {
        shareCarTable.deleteDataTable(carDataTableName);
        shareCarTable.deleteMetaTable();
    }

    public void deleteOrderIndexAndTable() {
        try {
            DeleteSearchIndexRequest deleteSearchIndexRequest = new DeleteSearchIndexRequest();
            deleteSearchIndexRequest.setTableName(carOrderTableName);
            deleteSearchIndexRequest.setIndexName(carOrderIndexName);
            asyncClient.asSyncClient().deleteSearchIndex(deleteSearchIndexRequest);
        } catch (Exception e) {}

        try {
            DeleteTableRequest deleteTableRequest = new DeleteTableRequest(carOrderTableName);
            asyncClient.asSyncClient().deleteTable(deleteTableRequest);
        } catch (Exception e) {}

    }

    public void insertCarAndMonitor() {
        TimestreamDataTable dataWriter = shareCarTable.dataTable(carDataTableName);
        TimestreamMetaTable metaWriter = shareCarTable.metaTable();


        TimestreamIdentifier identifier = new TimestreamIdentifier.Builder("car")
                .addTag("carNo", "浙A00000")
                .build();


        /**
         * Insert a new share car.
         * */
        Car car = new Car()
                .setCarNo("浙A00001")
                .setProvince("浙江")
                .setCompany("平台1")
                .setType("型号1")
                .setStatus("available")
                .setLocation("36.771002,126.671263")
                .setRemain(99)
                .setExpected(500)
                .setSeats(5);

        TimestreamMeta meta = new TimestreamMeta(identifier)
                .addAttribute("carNo", car.getCarNo())
                .addAttribute("province", car.getProvince())
                .addAttribute("company", car.getCompany())
                .addAttribute("type", car.getType())
                .addAttribute("seats", car.getSeats())
                .addAttribute("remain", car.getRemain())
                .addAttribute("expected", car.getExpected())
                .addAttribute("location", car.getLocation())
                .addAttribute("status", car.getStatus());

        metaWriter.put(meta);

        /**
         * Add monitor data from 0 to 100 minute.
         * */
        long from = 0;
        long to = 100;
        double remain = 100;
        double expected = 500;
        double total = 1000;

        for (long time = from; time <= to; time++) {
            MonitorPoint point = new MonitorPoint()
                    .setTime(time)
                    .setLocation(getMailRandomLocation())
                    .setSpeed(60)
                    .setRemain(remain -= 0.1)
                    .setExpected(expected -= 1)
                    .setTotal(total += 1);

            Point.Builder builder = new Point.Builder(point.getTime(), TimeUnit.MINUTES)
                    .addField("location", point.getLocation())
                    .addField("speed", point.getSpeed())
                    .addField("remain", point.getRemain())
                    .addField("expected", point.getExpected())
                    .addField("total", point.getTotal());

            dataWriter.asyncWrite(meta.getIdentifier(), builder.build());
        }

    }


    public void insertCarOrder() {
        Order order = new Order("O_00001")
                .setCarNo("浙A00000")
                .setFrom(80)
                .setTo(90)
                .setMobile("18800000001")
                .setUserName("用户名")
                .setStatus("unpaid")
                .setExpense(199.8);


        PrimaryKey primaryKey = PrimaryKeyBuilder.createPrimaryKeyBuilder()
                .addPrimaryKeyColumn("hash", PrimaryKeyValue.fromString(order.getHash()))
                .addPrimaryKeyColumn("orderId", PrimaryKeyValue.fromString(order.getOrderId()))
                .build();
        RowPutChange rowPutChange = new RowPutChange(carOrderTableName, primaryKey);

        rowPutChange.addColumn("carNo", ColumnValue.fromString(order.getCarNo()));
        rowPutChange.addColumn("mobile", ColumnValue.fromString(order.getMobile()));
        rowPutChange.addColumn("userName", ColumnValue.fromString(order.getUserName()));

        rowPutChange.addColumn("from", ColumnValue.fromLong(order.getFrom()));
        rowPutChange.addColumn("to", ColumnValue.fromLong(order.getTo()));
        rowPutChange.addColumn("total", ColumnValue.fromDouble(order.getTotal()));
        rowPutChange.addColumn("expense", ColumnValue.fromDouble(order.getExpense()));

        PutRowRequest request = new PutRowRequest();
        request.setRowChange(rowPutChange);

        asyncClient.asSyncClient().putRow(request);
    }

    public void searchCar() {
        System.out.println("[Search Car]");

        Filter filter = new AndFilter(
                Attribute.equal("status", "available"),
                Attribute.inGeoDistance("location", "36.771002,126.671263", 10000)
        );

        Iterator<TimestreamMeta> metaIterator = shareCarTable.metaTable()
                .filter(filter)
                .returnAll()
                .fetchAll();

        while (metaIterator.hasNext()) {
            System.out.println("\t" + metaIterator.next().getIdentifier().getTags().get("carNo"));
        }
    }

    public void searchOrder() {
        System.out.println("[Search Order]");

        SearchQuery searchQuery = new SearchQuery();
        Query query = new MatchAllQuery();// self designed query
        searchQuery.setQuery(query);

        searchQuery.setOffset(0);
        searchQuery.setLimit(10);
        searchQuery.setGetTotalCount(true);


        List<Sort.Sorter> fieldSorts = new ArrayList<Sort.Sorter>();
        FieldSort fieldSort = new FieldSort("from");
        fieldSort.setOrder(SortOrder.ASC);
        fieldSorts.add(fieldSort);
        searchQuery.setSort(new Sort(fieldSorts));


        SearchRequest searchRequest = new SearchRequest(carOrderTableName, carOrderIndexName, searchQuery);

        SearchRequest.ColumnsToGet columnsToGet = new SearchRequest.ColumnsToGet();
        columnsToGet.setReturnAll(true);
        searchRequest.setColumnsToGet(columnsToGet);


        SearchResponse response = asyncClient.asSyncClient().search(searchRequest);

        for (Row row : response.getRows()) {
            String orderId = row.getPrimaryKey().getPrimaryKeyColumn("orderId").getValue().asString();
            String carNo = row.getColumn("carNo").get(0).getValue().asString();
            System.out.println("\torderId: " + orderId + ", carNo: " + carNo);
        }
    }

    public void getOrderMonitor() {
        System.out.println("[Get Order Monitor]");

        Order order = new Order("O_00001")
                .setCarNo("浙A00000")
                .setFrom(80)
                .setTo(90)
                .setMobile("18800000001")
                .setUserName("用户名")
                .setStatus("unpaid")
                .setExpense(199.8);

        TimestreamIdentifier identifier = new TimestreamIdentifier.Builder("car")
                .addTag("carNo", "浙A00000")
                .build();

        Iterator<Point> iterator = shareCarTable.dataTable(carDataTableName)
                .get(identifier)
                .timeRange(TimeRange.range(order.getFrom(), order.getTo(), TimeUnit.MINUTES))
                .fetchAll();

        while (iterator.hasNext()) {
            System.out.println("\t" + iterator.next().getFields());
        }

    }


    public void close() {
        shareCarTable.close();
    }

    /**************************************** private tool functions ****************************************/
    public static void waitForSync() {
        waitForSync(10000);
    }

    public static void waitForSync(long time) {
        try {
            System.out.println("Wait for a moment: " + time + " ms");
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
