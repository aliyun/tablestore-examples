package com.aliyun.tablestore.example;

import com.alicloud.openservices.tablestore.model.*;
import com.alicloud.openservices.tablestore.model.search.*;
import com.alicloud.openservices.tablestore.model.search.agg.AggregationBuilders;
import com.alicloud.openservices.tablestore.model.search.agg.AggregationResult;
import com.alicloud.openservices.tablestore.model.search.agg.AggregationResults;
import com.alicloud.openservices.tablestore.model.search.agg.AvgAggregationResult;
import com.alicloud.openservices.tablestore.model.search.groupby.*;
import com.alicloud.openservices.tablestore.model.search.query.QueryBuilders;
import com.aliyun.tablestore.example.model.PriceDO;
import com.aliyun.tablestore.example.utils.ClientAndConfig;
import com.aliyun.tablestore.example.utils.Utils;

import java.util.Arrays;

import static com.aliyun.tablestore.example.consts.ColumnConsts.*;

public class AggregationAndGroupBy extends BaseExample {

    public AggregationAndGroupBy(ClientAndConfig clientAndConfig) {
        super(clientAndConfig);
    }

    public static void main(String[] args) {
        new AggregationAndGroupBy(Utils.getClientAndConfig(args)).main();
    }

    private void createTable() {
        TableMeta tableMeta = new TableMeta(tableName);
        tableMeta.addPrimaryKeyColumn(ID, PrimaryKeyType.INTEGER);
        // Set TTL to -1, never expire; Set maxVersions to 1, as one version is permitted
        TableOptions tableOptions = new TableOptions(-1, 1);
        CreateTableRequest createTableRequest = new CreateTableRequest(tableMeta, tableOptions);
        syncClient.createTable(createTableRequest);
        System.out.println("Create table success");
    }

    private void createSearchIndex() {
        CreateSearchIndexRequest createRequest = new CreateSearchIndexRequest(tableName, indexName);    //创建多元索引请求
        IndexSchema indexSchema = new IndexSchema();    //多元索引schema
        indexSchema.setFieldSchemas(Arrays.asList(      //多元索引字段
                new FieldSchema(ID, FieldType.LONG).setStore(true).setIndex(true),      //商品ID
                new FieldSchema(NAME, FieldType.TEXT).setStore(true).setIndex(true),    //商品名
                new FieldSchema(PRICE, FieldType.DOUBLE).setStore(true).setIndex(true).setEnableSortAndAgg(true),   //商品报价
                new FieldSchema(BRAND, FieldType.KEYWORD).setStore(true).setIndex(true).setEnableSortAndAgg(true),  //商品品牌
                new FieldSchema(SELLER, FieldType.LONG).setStore(true).setIndex(true).setEnableSortAndAgg(true)));  //店家ID

        createRequest.setIndexSchema(indexSchema);  //设置多元索引schema

        syncClient.createSearchIndex(createRequest);    //创建多元索引
        System.out.println("Create SearchIndex success");
    }

    private void mockData() {
        BatchWriteRowRequest batchWriteRowRequest = new BatchWriteRowRequest(); //BatchWrite请求
        {
            PriceDO priceDO = new PriceDO();
            priceDO.setId(1L);
            priceDO.setName("vivo Z5x");
            priceDO.setPrice(1298.00);
            priceDO.setBrand("vivo");
            priceDO.setSeller(1L);
            batchWriteRowRequest.addRowChange(priceDO.toRowPutChange(tableName));
        }
        {
            PriceDO priceDO = new PriceDO();
            priceDO.setId(2L);
            priceDO.setName("Huawei P30");
            priceDO.setPrice(3688.00);
            priceDO.setBrand("Huawei");
            priceDO.setSeller(1L);
            batchWriteRowRequest.addRowChange(priceDO.toRowPutChange(tableName));
        }
        {
            PriceDO priceDO = new PriceDO();
            priceDO.setId(3L);
            priceDO.setName("Huawei P30");
            priceDO.setPrice(3999.00);
            priceDO.setBrand("Huawei");
            priceDO.setSeller(3L);
            batchWriteRowRequest.addRowChange(priceDO.toRowPutChange(tableName));
        }
        {
            PriceDO priceDO = new PriceDO();
            priceDO.setId(4L);
            priceDO.setName("Huawei nova 5 Pro");
            priceDO.setPrice(2999.00);
            priceDO.setBrand("Huawei");
            priceDO.setSeller(1L);
            batchWriteRowRequest.addRowChange(priceDO.toRowPutChange(tableName));
        }
        {
            PriceDO priceDO = new PriceDO();
            priceDO.setId(5L);
            priceDO.setName("iPhone XR");
            priceDO.setPrice(5188.00);
            priceDO.setBrand("iPhone");
            priceDO.setSeller(1L);
            batchWriteRowRequest.addRowChange(priceDO.toRowPutChange(tableName));
        }
        {
            PriceDO priceDO = new PriceDO();
            priceDO.setId(6L);
            priceDO.setName("iPhone 8");
            priceDO.setPrice(4688.00);
            priceDO.setBrand("iPhone");
            priceDO.setSeller(1L);
            batchWriteRowRequest.addRowChange(priceDO.toRowPutChange(tableName));
        }
        {
            PriceDO priceDO = new PriceDO();
            priceDO.setId(7L);
            priceDO.setName("iPhone 8");
            priceDO.setPrice(4699.00);
            priceDO.setBrand("iPhone");
            priceDO.setSeller(2L);
            batchWriteRowRequest.addRowChange(priceDO.toRowPutChange(tableName));
        }
        {
            PriceDO priceDO = new PriceDO();
            priceDO.setId(8L);
            priceDO.setName("iPhone 8");
            priceDO.setPrice(4600.00);
            priceDO.setBrand("iPhone");
            priceDO.setSeller(3L);
            batchWriteRowRequest.addRowChange(priceDO.toRowPutChange(tableName));
        }
        {
            PriceDO priceDO = new PriceDO();
            priceDO.setId(9L);
            priceDO.setName("OPPO K1");
            priceDO.setPrice(1299.00);
            priceDO.setBrand("OPPO");
            priceDO.setSeller(1L);
            batchWriteRowRequest.addRowChange(priceDO.toRowPutChange(tableName));
        }
        {
            PriceDO priceDO = new PriceDO();
            priceDO.setId(10L);
            priceDO.setName("Redmi 7");
            priceDO.setPrice(699.00);
            priceDO.setBrand("Xiaomi");
            priceDO.setSeller(1L);
            batchWriteRowRequest.addRowChange(priceDO.toRowPutChange(tableName));
        }

        syncClient.batchWriteRow(batchWriteRowRequest);

        try {
            Thread.sleep(30_000);
        } catch (InterruptedException e) {}
    }

    private void printResult(SearchResponse searchResponse) {
        System.out.println("Total counts: " + searchResponse.getTotalCount());
        for (Row row : searchResponse.getRows()) {
            System.out.println(PriceDO.fromRow(row));
        }
    }

    /** 不指定Query，默认使用MatchAllQuery */

    public void testAgg() {
        //查询最便宜的一款手机
        SearchRequest searchRequest = SearchRequest.newBuilder()    //多元索引查询请求
                .tableName(tableName)                               //Tablestore表名称
                .indexName(indexName)                               //多元索引名
                .returnAllColumns(true)                             //返回所有列
                .searchQuery(                                       //查询请求
                        SearchQuery.newBuilder()
                                .query(QueryBuilders.matchAll())   //匹配所有行
                                .getTotalCount(false)                                               //不返回匹配条数
                                .addAggregation(AggregationBuilders.min("test_agg", "price"))    //Count Aggregation统计"id"字段的个数，Count Agg名称为test_agg""
                                .build())
                .build();

        //发出查询请求，获取查询响应
        SearchResponse resp = syncClient.search(searchRequest);
        System.out.println(resp.getAggregationResults().getAsMinAggregationResult("test_agg").getValue());
    }

    public void testGroupby() {
        //按品牌分组，统计每组匹配个数
        SearchRequest searchRequest = SearchRequest.newBuilder()    //多元索引查询请求
                .tableName(tableName)                               //Tablestore表名称
                .indexName(indexName)                               //多元索引名
                .returnAllColumns(false)                            //返回所有列
                .searchQuery(                                       //查询请求
                        SearchQuery.newBuilder()
                                .getTotalCount(true)                //返回匹配条数
                                .addGroupBy(GroupByBuilders.groupByField("group_by_brand", "brand")) //GroupByField按"brand"不同值分组，GroupByField名称为"group_by_brand"
                                .build())
                .build();

        //发出查询请求，获取查询响应
        SearchResponse resp = syncClient.search(searchRequest);

        GroupByFieldResult results = resp.getGroupByResults().getAsGroupByFieldResult("group_by_brand");    //获取名字为"group_by_brand"的GroupByField结果
        for (GroupByFieldResultItem item : results.getGroupByFieldResultItems()) {  //每个分组
            System.out.println("group: " + item.getKey());                          //分组键"brand"的值
            System.out.println("size: " + item.getRowCount());                      //本分组内的行数
        }
    }

    public void testGroupbyThenAgg() {
        //按品牌分组，统计每个品牌手机的最小值、最大值、平均值
        SearchRequest searchRequest = SearchRequest.newBuilder()    //多元索引查询请求
                .tableName(tableName)                               //Tablestore表名称
                .indexName(indexName)                               //多元索引名
                .returnAllColumns(false)                            //返回所有列
                .searchQuery(                                       //查询请求
                        SearchQuery.newBuilder()
                                .getTotalCount(true)                //返回匹配条数
                                .addGroupBy(GroupByBuilders.groupByField("group_by_brand", "brand")                //GroupByField按"brand"不同值分组，GroupByField名称为"group_by_brand"
                                        .addSubAggregation(AggregationBuilders.max("max_price", "price"))   //分组内统计"price"字段的最大值，Max Aggregation名称为"max_price"
                                        .addSubAggregation(AggregationBuilders.min("min_price", "price"))   //分组内统计"price"字段的最小值，Min Aggregation名称为"min_price"
                                        .addSubAggregation(AggregationBuilders.avg("avg_price", "price"))   //分组内统计"price"字段的平均值，Avg Aggregation名称为"avg_price"
                                ).build())
                .build();

        //发出查询请求，获取查询响应
        SearchResponse resp = syncClient.search(searchRequest);

        GroupByFieldResult results = resp.getGroupByResults().getAsGroupByFieldResult("group_by_brand");    //获取名字为"group_by_brand"的GroupByField结果
        for (GroupByFieldResultItem item : results.getGroupByFieldResultItems()) {  //每个分组
            System.out.println("group: " + item.getKey());                          //分组键"brand"的值
            System.out.println("size: " + item.getRowCount());                      //本分组内的行数
        }
    }

    /** 显示指定Query，会先按Query过滤文档，再做后续统计聚合 */

    public void testQueryAgg() {
        //查询品牌为"Huawei"的手机记录数
        SearchRequest searchRequest = SearchRequest.newBuilder()    //多元索引查询请求
                .tableName(tableName)                               //Tablestore表名称
                .indexName(indexName)                               //多元索引名
                .returnAllColumns(true)                             //返回所有列
                .searchQuery(                                       //查询请求
                        SearchQuery.newBuilder()
                                .query(QueryBuilders.term("brand", "Huawei"))   //TermQuery查询brand为"Huawei"的行
                                .getTotalCount(false)                                               //不返回匹配条数
                                .addAggregation(AggregationBuilders.count("test_agg", "id"))    //Count Aggregation统计"id"字段的个数，Count Agg名称为"test_agg"
                                .build())
                .build();

        //发出查询请求，获取查询响应
        SearchResponse resp = syncClient.search(searchRequest);
        System.out.println(resp.getAggregationResults().getAsCountAggregationResult("test_agg").getValue());    //解析test_agg统计结果
    }

    public void testQueryGroupBy() {
        //将价格大于3000的手机过滤出来，按照"brand"字段进行分组，统计每个分组内的记录数
        SearchRequest searchRequest = SearchRequest.newBuilder()    //多元索引查询请求
                .tableName(tableName)                               //Tablestore表名称
                .indexName(indexName)                               //多元索引名
                .returnAllColumns(false)                            //返回所有列
                .searchQuery(                                       //查询请求
                        SearchQuery.newBuilder()
                                .query(QueryBuilders.range("price").greaterThan(3000.00))   //匹配价格大于3000的记录
                                .getTotalCount(true)                //返回匹配条数
                                .addGroupBy(GroupByBuilders.groupByField("group_by_brand", "brand")) //GroupByField按"brand"不同值分组，GroupByField名称为"group_by_brand"
                                .build())
                .build();

        //发出查询请求，获取查询响应
        SearchResponse resp = syncClient.search(searchRequest);

        GroupByFieldResult results = resp.getGroupByResults().getAsGroupByFieldResult("group_by_brand");    //获取名字为"group_by_brand"的GroupByField结果
        for (GroupByFieldResultItem item : results.getGroupByFieldResultItems()) {  //每个分组
            System.out.println("group: " + item.getKey());                          //分组键"brand"的值
            System.out.println("size: " + item.getRowCount());                      //本分组内的行数
        }
    }

    public void testQueryGroupByThenAgg() {
        //将价格大于3000的手机过滤出来，按照"brand"字段进行分组，统计每个分组内的最大值、最小值、平均值
        SearchRequest searchRequest = SearchRequest.newBuilder()    //多元索引查询请求
                .tableName(tableName)                               //Tablestore表名称
                .indexName(indexName)                               //多元索引名
                .returnAllColumns(false)                            //返回所有列
                .searchQuery(                                       //查询请求
                        SearchQuery.newBuilder()
                                .query(QueryBuilders.range("price").greaterThan(3000.00))   //匹配价格大于3000的记录
                                .getTotalCount(true)                //返回匹配条数
                                .addGroupBy(GroupByBuilders.groupByField("group_by_brand", "brand") //GroupByField按"brand"不同值分组，GroupByField名称为"group_by_brand"
                                        .addSubAggregation(AggregationBuilders.max("max_price", "price"))   //分组内统计"price"字段的最大值，Max Aggregation名称为"max_price"
                                        .addSubAggregation(AggregationBuilders.min("min_price", "price"))   //分组内统计"price"字段的最小值，Min Aggregation名称为"min_price"
                                        .addSubAggregation(AggregationBuilders.avg("avg_price", "price"))   //分组内统计"price"字段的平均值，Avg Aggregation名称为"avg_price"
                                )
                                .build())
                .build();

        //发出查询请求，获取查询响应
        SearchResponse resp = syncClient.search(searchRequest);

        GroupByFieldResult results = resp.getGroupByResults().getAsGroupByFieldResult("group_by_brand");    //获取名字为"group_by_brand"的GroupByField结果
        for (GroupByFieldResultItem item : results.getGroupByFieldResultItems()) {  //每个分组
            System.out.println("group: " + item.getKey());                          //分组键"brand"的值
            AggregationResults aggregationResults = item.getSubAggregationResults();
            System.out.println("\tmax_price: " + aggregationResults.getAsMaxAggregationResult("max_price").getValue()); //本组内"price"的最大值
            System.out.println("\tmin_price: " + aggregationResults.getAsMinAggregationResult("min_price").getValue()); //本组内"price"的最小值
            System.out.println("\tavg_price: " + aggregationResults.getAsAvgAggregationResult("avg_price").getValue()); //本组内"price"的平均值
        }
    }

    public void testQueryAggAndGroupBy() {
        //将价格大于3000的手机过滤出来，
        // 1. 统计均价
        // 2. 统计每个品牌"brand"的记录行数
        SearchRequest searchRequest = SearchRequest.newBuilder()    //多元索引查询请求
                .tableName(tableName)                               //Tablestore表名称
                .indexName(indexName)                               //多元索引名
                .returnAllColumns(false)                            //返回所有列
                .searchQuery(                                       //查询请求
                        SearchQuery.newBuilder()
                                .query(QueryBuilders.range("price").greaterThan(3000.00))   //匹配价格大于3000的记录
                                .getTotalCount(true)                //返回匹配条数
                                .addAggregation(AggregationBuilders.avg("avg_price", "price"))  //统计所有价格"price" > 3000.00的手机均价
                                .addGroupBy(GroupByBuilders.groupByField("group_by_brand", "brand"))   //GroupByField按"brand"不同值分组，GroupByField名称为"group_by_brand"
                                .build())
                .build();

        //发出查询请求，获取查询响应
        SearchResponse resp = syncClient.search(searchRequest);

        GroupByFieldResult results = resp.getGroupByResults().getAsGroupByFieldResult("group_by_brand");    //获取名字为"group_by_brand"的GroupByField结果
        for (GroupByFieldResultItem item : results.getGroupByFieldResultItems()) {  //每个分组
            System.out.println("group: " + item.getKey());                          //分组键"brand"的值
            System.out.println("size: " + item.getRowCount());                      //分组内行数
        }

        AggregationResult aggregationResult = resp.getAggregationResults().getAsAvgAggregationResult("avg_price");
        System.out.println("avg_price above 3000: " + ((AvgAggregationResult) aggregationResult).getValue());
    }

    /** groupby内部嵌套groupby */

    public void testQueryNestedGroupBy() {
        //将价格大于3000的手机过滤出来，按照"brand"字段进行分组，统计每个分组内的商品数
        SearchRequest searchRequest = SearchRequest.newBuilder()    //多元索引查询请求
                .tableName(tableName)                               //Tablestore表名称
                .indexName(indexName)                               //多元索引名
                .returnAllColumns(false)                            //返回所有列
                .searchQuery(
                        SearchQuery.newBuilder()
                            .getTotalCount(false)                   //不返回匹配条数
                            .addGroupBy(GroupByBuilders.groupByFilter("over_3000")
                                    .addFilter(QueryBuilders.range("price").greaterThan(3000.00))
                                    .addSubGroupBy(GroupByBuilders.groupByField("group_by_brand", "brand"))
                            ).build())
                .build();

        //发出查询请求，获取查询响应
        SearchResponse resp = syncClient.search(searchRequest);

        GroupByFilterResult results = resp.getGroupByResults().getAsGroupByFilterResult("over_3000");    //获取名字为"over_3000"的GroupByFilter结果
        for (GroupByFilterResultItem item : results.getGroupByFilterResultItems()) {  //每个filter分组，当前只有一个分组（"price"字段值>3000.00）
            GroupByResults filterResult = item.getSubGroupByResults();                //获取filter分组结果
            GroupByFieldResult brandGroups = filterResult.getAsGroupByFieldResult("group_by_brand");    //获取名字为"group_by_brand"的GroupByField结果
            for (GroupByFieldResultItem brandGroup : brandGroups.getGroupByFieldResultItems()) {    //遍历"group_by_brand"每个分组
                System.out.println("group: " + brandGroup.getKey());        //分组键"brand"的值
                System.out.println("size: " + brandGroup.getRowCount());    //本分组内的行数
            }
        }
    }

    @Override
    protected void doMain() {
        createTable();
        createSearchIndex();
        mockData();

        //默认匹配所有索引行
        testAgg();
        testGroupby();
        testGroupbyThenAgg();

        //Query过滤匹配行，然后再做统计聚合
        testQueryAgg();
        testQueryGroupBy();
        testQueryGroupByThenAgg();

        //Query过滤匹配行，然后并列做Agg和Groupby
        testQueryAggAndGroupBy();

        //Groupby嵌套
        testQueryNestedGroupBy();

        testQueryNestedGroupBy();
    }
}
