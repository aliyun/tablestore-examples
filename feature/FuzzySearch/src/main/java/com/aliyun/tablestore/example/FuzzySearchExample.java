package com.aliyun.tablestore.example;

import com.alicloud.openservices.tablestore.model.*;
import com.alicloud.openservices.tablestore.model.search.*;
import com.alicloud.openservices.tablestore.model.search.analysis.FuzzyAnalyzerParameter;
import com.alicloud.openservices.tablestore.model.search.query.MatchQuery;
import com.alicloud.openservices.tablestore.model.search.query.PrefixQuery;
import com.alicloud.openservices.tablestore.model.search.query.WildcardQuery;
import com.aliyun.tablestore.example.model.OrderDO;
import com.aliyun.tablestore.example.utils.ClientAndConfig;
import com.aliyun.tablestore.example.utils.Utils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.atomic.AtomicLong;

import static com.aliyun.tablestore.example.consts.ColumnConsts.*;
import static com.aliyun.tablestore.example.consts.ColumnConsts.CONSUMER_ADDRESS;

public class FuzzySearchExample extends BaseExample {

    private AtomicLong orderIdGenerator = new AtomicLong();
    private AtomicLong productIdGenerator = new AtomicLong();
    private AtomicLong consumerIdGenerator = new AtomicLong();

    private static final String ORDER_ID_PREFIX = "O_";
    private static final int ORDER_ID_PAD_SIZE = 15;

    private static final String PRODUCT_ID_PREFIX = "P_";
    private static final int PRODUCT_ID_PAD_SIZE = 10;

    private static final String CONSUMER_ID_PREFIX = "C_";
    private static final int CONSUMER_ID_PAD_SIZE = 10;

    public FuzzySearchExample(ClientAndConfig clientAndConfig) {
        super(clientAndConfig);
    }

    public static void main(String[] args) {
        new FuzzySearchExample(Utils.getClientAndConfig(args)).main();
    }

    private void createTable() {
        TableMeta tableMeta = new TableMeta(tableName);
        tableMeta.addPrimaryKeyColumn(ORDER_ID_MD5, PrimaryKeyType.STRING);
        tableMeta.addPrimaryKeyColumn(ORDER_ID, PrimaryKeyType.STRING);
        // Set TTL to -1, never expire; Set maxVersions to 1, as one version is permitted
        TableOptions tableOptions = new TableOptions(-1, 1);
        CreateTableRequest createTableRequest = new CreateTableRequest(tableMeta, tableOptions);
        syncClient.createTable(createTableRequest);
        System.out.println("Create table success");
    }

    private void createSearchIndex() {
        CreateSearchIndexRequest createRequest = new CreateSearchIndexRequest(tableName, indexName);
        IndexSchema indexSchema = new IndexSchema();
        indexSchema.addFieldSchema(new FieldSchema(ORDER_ID, FieldType.KEYWORD));
        indexSchema.addFieldSchema(new FieldSchema(ORDER_STATUS, FieldType.LONG));
        indexSchema.addFieldSchema(new FieldSchema(ORDER_TIME, FieldType.LONG).setEnableSortAndAgg(true));
        indexSchema.addFieldSchema(new FieldSchema(PAY_TIME, FieldType.LONG).setEnableSortAndAgg(true));
        indexSchema.addFieldSchema(new FieldSchema(DELIVER_TIME, FieldType.LONG).setEnableSortAndAgg(true));
        indexSchema.addFieldSchema(new FieldSchema(RECEIVE_TIME, FieldType.LONG).setEnableSortAndAgg(true));
        indexSchema.addFieldSchema(new FieldSchema(PRODUCT_ID, FieldType.KEYWORD));
        indexSchema.addFieldSchema(new FieldSchema(PRODUCT_NAME, FieldType.TEXT));
        indexSchema.addFieldSchema(new FieldSchema(PRODUCT_TYPE, FieldType.TEXT)
                .setAnalyzer(FieldSchema.Analyzer.Fuzzy)
                .setAnalyzerParameter(new FuzzyAnalyzerParameter(1, 4)));
        indexSchema.addFieldSchema(new FieldSchema(CONSUMER_ID, FieldType.KEYWORD));
        indexSchema.addFieldSchema(new FieldSchema(CONSUMER_NAME, FieldType.TEXT));
        indexSchema.addFieldSchema(new FieldSchema(CONSUMER_CELL, FieldType.KEYWORD));
        indexSchema.addFieldSchema(new FieldSchema(CONSUMER_ADDRESS, FieldType.TEXT).setAnalyzer(FieldSchema.Analyzer.MaxWord));

        createRequest.setIndexSchema(indexSchema);

        syncClient.createSearchIndex(createRequest);
        System.out.println("Create SearchIndex success");
    }

    private void mockData() {
        BatchWriteRowRequest batchWriteRowRequest = new BatchWriteRowRequest();
        {
            OrderDO orderDO = new OrderDO();
            orderDO.setOrderId(ORDER_ID_PREFIX + StringUtils.leftPad(Long.toString(orderIdGenerator.getAndIncrement()), ORDER_ID_PAD_SIZE, '0'));
            orderDO.setOrderStatus(RandomUtils.nextLong(0, 5));

            orderDO.setOrderTime(System.currentTimeMillis() - RandomUtils.nextLong(0, 5 * 365 * 24 * 60 * 60 * 1000L));
            orderDO.setPayTime(System.currentTimeMillis() - RandomUtils.nextLong(0, 5 * 365 * 24 * 60 * 60 * 1000L));
            orderDO.setDeliverTime(System.currentTimeMillis() - RandomUtils.nextLong(0, 5 * 365 * 24 * 60 * 60 * 1000L));
            orderDO.setReceiveTime(System.currentTimeMillis() - RandomUtils.nextLong(0, 5 * 365 * 24 * 60 * 60 * 1000L));

            orderDO.setProductId(PRODUCT_ID_PREFIX + StringUtils.leftPad(Long.toString(productIdGenerator.getAndIncrement()), PRODUCT_ID_PAD_SIZE, '0'));
            orderDO.setProductName("Huawei/华为P30 Pro曲面屏超感光徕卡四摄变焦双景录像980芯片智能手机p30pro");
            orderDO.setProductType("HUAWEI P30PRO");

            orderDO.setConsumerId(CONSUMER_ID_PREFIX + StringUtils.leftPad(Long.toString(consumerIdGenerator.getAndIncrement()), CONSUMER_ID_PAD_SIZE, '0'));
            orderDO.setConsumerName("王二");
            orderDO.setConsumerCell("13580669999");
            orderDO.setConsumerAddress("四川省成都市金牛区人民北路西三巷32栋2楼201室");

            batchWriteRowRequest.addRowChange(orderDO.toRowPutChange(tableName));
        }

        {
            OrderDO orderDO = new OrderDO();
            orderDO.setOrderId(ORDER_ID_PREFIX + StringUtils.leftPad(Long.toString(orderIdGenerator.getAndIncrement()), ORDER_ID_PAD_SIZE, '0'));
            orderDO.setOrderStatus(RandomUtils.nextLong(0, 5));

            orderDO.setOrderTime(System.currentTimeMillis() - RandomUtils.nextLong(0, 5 * 365 * 24 * 60 * 60 * 1000L));
            orderDO.setPayTime(System.currentTimeMillis() - RandomUtils.nextLong(0, 5 * 365 * 24 * 60 * 60 * 1000L));
            orderDO.setDeliverTime(System.currentTimeMillis() - RandomUtils.nextLong(0, 5 * 365 * 24 * 60 * 60 * 1000L));
            orderDO.setReceiveTime(System.currentTimeMillis() - RandomUtils.nextLong(0, 5 * 365 * 24 * 60 * 60 * 1000L));

            orderDO.setProductId(PRODUCT_ID_PREFIX + StringUtils.leftPad(Long.toString(productIdGenerator.getAndIncrement()), PRODUCT_ID_PAD_SIZE, '0'));
            orderDO.setProductName("Huawei/华为 Mate 20 Pro 曲面屏后置徕卡三镜头980芯片智能手机");
            orderDO.setProductType("HUAWEI Mate 20 Pro");

            orderDO.setConsumerId(CONSUMER_ID_PREFIX + StringUtils.leftPad(Long.toString(consumerIdGenerator.getAndIncrement()), CONSUMER_ID_PAD_SIZE, '0'));
            orderDO.setConsumerName("陈小明");
            orderDO.setConsumerCell("13609960996");
            orderDO.setConsumerAddress("浙江省杭州市西湖区龙井路1号");

            batchWriteRowRequest.addRowChange(orderDO.toRowPutChange(tableName));
        }

        {
            OrderDO orderDO = new OrderDO();
            orderDO.setOrderId(ORDER_ID_PREFIX + StringUtils.leftPad(Long.toString(orderIdGenerator.getAndIncrement()), ORDER_ID_PAD_SIZE, '0'));
            orderDO.setOrderStatus(RandomUtils.nextLong(0, 5));

            orderDO.setOrderTime(System.currentTimeMillis() - RandomUtils.nextLong(0, 5 * 365 * 24 * 60 * 60 * 1000L));
            orderDO.setPayTime(System.currentTimeMillis() - RandomUtils.nextLong(0, 5 * 365 * 24 * 60 * 60 * 1000L));
            orderDO.setDeliverTime(System.currentTimeMillis() - RandomUtils.nextLong(0, 5 * 365 * 24 * 60 * 60 * 1000L));
            orderDO.setReceiveTime(System.currentTimeMillis() - RandomUtils.nextLong(0, 5 * 365 * 24 * 60 * 60 * 1000L));

            orderDO.setProductId(PRODUCT_ID_PREFIX + StringUtils.leftPad(Long.toString(productIdGenerator.getAndIncrement()), PRODUCT_ID_PAD_SIZE, '0'));
            orderDO.setProductName("Xiaomi/小米redmi note 7 pro 红米索尼4800万智能老年学生商务大电量手机官方旗舰店");
            orderDO.setProductType("redmi note 7 pro");

            orderDO.setConsumerId(CONSUMER_ID_PREFIX + StringUtils.leftPad(Long.toString(consumerIdGenerator.getAndIncrement()), CONSUMER_ID_PAD_SIZE, '0'));
            orderDO.setConsumerName("谭先生");
            orderDO.setConsumerCell("15969696969");
            orderDO.setConsumerAddress("浙江省杭州市西湖区转塘街道云梦路3号楼小邮局");

            batchWriteRowRequest.addRowChange(orderDO.toRowPutChange(tableName));
        }

        syncClient.batchWriteRow(batchWriteRowRequest);

        try {
            Thread.sleep(30_000);
        } catch (InterruptedException e) {}
    }

    private void printResult(SearchResponse searchResponse) {
        System.out.println("Total counts: " + searchResponse.getTotalCount());
        for (Row row : searchResponse.getRows()) {
            System.out.println(OrderDO.fromRow(row));
        }
    }

    /**
     * query consumer's cell number by prefix
     *
     * data type：String
     * index type：KEYWORD
     * query type：PrefixQuery
     */
    private void prefixSearchExample() {
        SearchQuery searchQuery = new SearchQuery();

        // get total count
        searchQuery.setGetTotalCount(true);

        // use prefix query
        PrefixQuery prefixQuery = new PrefixQuery();
        prefixQuery.setFieldName(CONSUMER_CELL);
        prefixQuery.setPrefix("13580");
        searchQuery.setQuery(prefixQuery);

        SearchRequest searchRequest = new SearchRequest(tableName, indexName, searchQuery);

        // return all columns
        SearchRequest.ColumnsToGet columnsToGet = new SearchRequest.ColumnsToGet();
        columnsToGet.setReturnAll(true);
        searchRequest.setColumnsToGet(columnsToGet);

        SearchResponse searchResponse = syncClient.search(searchRequest);
        printResult(searchResponse);
    }

    /**
     * query consumer's cell number by wildcard
     *
     * data type：String
     * index type：KEYWORD
     * query type：WildcardQuery
     */
    private void wildcardSearchExample() {
        SearchQuery searchQuery = new SearchQuery();

        // get total count
        searchQuery.setGetTotalCount(true);

        // use wildcard query
        WildcardQuery wildcardQuery = new WildcardQuery();
        wildcardQuery.setFieldName(CONSUMER_CELL);
        wildcardQuery.setValue("136*996");
        searchQuery.setQuery(wildcardQuery);

        SearchRequest searchRequest = new SearchRequest(tableName, indexName, searchQuery);

        // return all columns
        SearchRequest.ColumnsToGet columnsToGet = new SearchRequest.ColumnsToGet();
        columnsToGet.setReturnAll(true);
        searchRequest.setColumnsToGet(columnsToGet);

        SearchResponse searchResponse = syncClient.search(searchRequest);
        printResult(searchResponse);
    }

    /**
     * query product name using SingleWord analyzer
     *
     * data type：String
     * index type：TEXT
     * query type：MatchQuery / MatchPhraseQuery
     * analyzer: SingleWord
     */
    private void singleWordSearchExample() {
        SearchQuery searchQuery = new SearchQuery();

        // get total count
        searchQuery.setGetTotalCount(true);

        // use match query
        MatchQuery matchQuery = new MatchQuery();
        matchQuery.setFieldName(PRODUCT_NAME);
        matchQuery.setText("华为 徕卡");
        searchQuery.setQuery(matchQuery);

        SearchRequest searchRequest = new SearchRequest(tableName, indexName, searchQuery);

        // return all columns
        SearchRequest.ColumnsToGet columnsToGet = new SearchRequest.ColumnsToGet();
        columnsToGet.setReturnAll(true);
        searchRequest.setColumnsToGet(columnsToGet);

        SearchResponse searchResponse = syncClient.search(searchRequest);
        printResult(searchResponse);
    }

    /**
     * query product type using Fuzzy analyzer
     *
     * data type：String
     * index type：TEXT
     * query type：MatchQuery / MatchPhraseQuery
     * analyzer: Fuzzy
     */
    private void fuzzySearchExample() {
        SearchQuery searchQuery = new SearchQuery();

        // get total count
        searchQuery.setGetTotalCount(true);

        // use match query
        MatchQuery matchQuery = new MatchQuery();
        matchQuery.setFieldName(PRODUCT_TYPE);
        matchQuery.setText("P30");
        searchQuery.setQuery(matchQuery);

        SearchRequest searchRequest = new SearchRequest(tableName, indexName, searchQuery);

        // return all columns
        SearchRequest.ColumnsToGet columnsToGet = new SearchRequest.ColumnsToGet();
        columnsToGet.setReturnAll(true);
        searchRequest.setColumnsToGet(columnsToGet);

        SearchResponse searchResponse = syncClient.search(searchRequest);
        printResult(searchResponse);
    }

    @Override
    protected void doMain() {
        createTable();
        createSearchIndex();
        mockData();

        prefixSearchExample();
        wildcardSearchExample();
        singleWordSearchExample();
        fuzzySearchExample();
    }
}
