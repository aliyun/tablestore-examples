package com.aliyun.tablestore.examples.service;


import com.aliyun.tablestore.examples.bean.ConsumerTradeValue;
import com.alicloud.openservices.tablestore.SyncClient;
import com.alicloud.openservices.tablestore.model.*;
import com.alicloud.openservices.tablestore.model.search.SearchQuery;
import com.alicloud.openservices.tablestore.model.search.SearchRequest;
import com.alicloud.openservices.tablestore.model.search.SearchResponse;
import com.alicloud.openservices.tablestore.model.search.agg.AggregationBuilders;
import com.alicloud.openservices.tablestore.model.search.groupby.GroupByBuilders;
import com.alicloud.openservices.tablestore.model.search.groupby.GroupByFieldResultItem;
import com.alicloud.openservices.tablestore.model.search.query.*;
import com.alicloud.openservices.tablestore.model.search.sort.FieldSort;
import com.alicloud.openservices.tablestore.model.search.sort.GroupBySorter;
import com.alicloud.openservices.tablestore.model.search.sort.Sort;
import com.alicloud.openservices.tablestore.model.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Service
public class SearchService {

    @Autowired
    SyncClient syncClient;


    public List<String> getUserByBrand(String brand) {

        // 组装请求参数
        SearchQuery searchQuery = new SearchQuery();
        searchQuery.setGetTotalCount(true);

        BoolQuery boolQuery = new BoolQuery();

        TermQuery applierNameQuery = new TermQuery();
        applierNameQuery.setFieldName("p_brand");
        applierNameQuery.setTerm(ColumnValue.fromString(brand));

        boolQuery.setMustQueries(Arrays.asList(
                applierNameQuery
        ));

        searchQuery.setQuery(boolQuery);

        SearchRequest searchRequest = new SearchRequest("order_contract", "order_contract_index", searchQuery);
        SearchRequest.ColumnsToGet columnsToGet = new SearchRequest.ColumnsToGet();
        columnsToGet.setReturnAll(true);
        searchRequest.setColumnsToGet(columnsToGet);

        // 进行搜索
        SearchResponse response = syncClient.search(searchRequest);

        // 解析返回数据
        List<String> userList = new ArrayList<>();
        if (response != null && !CollectionUtils.isEmpty(response.getRows())) {
            List<Row> item = response.getRows();
            for (Row r : item) {
                userList.add(r.getColumn("c_id").get(0).getValue().asString());
            }
        }

        return userList;
    }


    public List<String> searchByBrandAndKey(String brand, Double high, Double low) {

        // 组装请求参数
        SearchQuery searchQuery = new SearchQuery();
        searchQuery.setGetTotalCount(true);

        BoolQuery boolQuery = new BoolQuery();

        TermQuery applierNameQuery = new TermQuery();
        applierNameQuery.setFieldName("p_brand");
        applierNameQuery.setTerm(ColumnValue.fromString(brand));

        RangeQuery rangeQuery = new RangeQuery();
        rangeQuery.setFieldName("p_price");
        rangeQuery.setFrom(ColumnValue.fromDouble(low), true);
        rangeQuery.setTo(ColumnValue.fromDouble(high),true);

        boolQuery.setMustQueries(Arrays.asList(
                applierNameQuery,
                rangeQuery
        ));

        searchQuery.setQuery(boolQuery);

        SearchRequest searchRequest = new SearchRequest("order_contract", "order_contract_index", searchQuery);
        SearchRequest.ColumnsToGet columnsToGet = new SearchRequest.ColumnsToGet();
        columnsToGet.setReturnAll(true);
        searchRequest.setColumnsToGet(columnsToGet);

        // 进行搜索
        SearchResponse response = syncClient.search(searchRequest);

        // 解析返回数据
        List<String> userList = new ArrayList<>();
        if (response != null && !CollectionUtils.isEmpty(response.getRows())) {
            List<Row> item = response.getRows();
            for (Row r : item) {
                userList.add(r.getColumn("c_id").get(0).getValue().asString());
            }
        }

        return userList;
    }

    public List<String> searchByKeyInProductName(String key) {
        // 组装请求参数
        SearchQuery searchQuery = new SearchQuery();
        searchQuery.setGetTotalCount(true);

        BoolQuery boolQuery = new BoolQuery();

        WildcardQuery wildcardQuery = new WildcardQuery();
        wildcardQuery.setFieldName("p_name");
        wildcardQuery.setValue("*" + key + "*");

        boolQuery.setMustQueries(Arrays.asList(
                wildcardQuery
        ));

        searchQuery.setQuery(boolQuery);

        SearchRequest searchRequest = new SearchRequest("order_contract", "order_contract_index", searchQuery);
        SearchRequest.ColumnsToGet columnsToGet = new SearchRequest.ColumnsToGet();
        columnsToGet.setReturnAll(true);
        searchRequest.setColumnsToGet(columnsToGet);

        // 进行搜索
        SearchResponse response = syncClient.search(searchRequest);

        // 解析返回数据
        List<String> userList = new ArrayList<>();
        if (response != null && !CollectionUtils.isEmpty(response.getRows())) {
            List<Row> item = response.getRows();
            for (Row r : item) {
                userList.add(r.getColumn("c_id").get(0).getValue().asString());
            }
        }

        return userList;

    }


    public List<String> searchByCId(String id) {

        RangeRowQueryCriteria rangeRowQueryCriteria = new RangeRowQueryCriteria("idx_cId"); //设置索引表名称。

        //设置起始主键。
        PrimaryKeyBuilder startPrimaryKeyBuilder = PrimaryKeyBuilder.createPrimaryKeyBuilder();
        startPrimaryKeyBuilder.addPrimaryKeyColumn("c_id", PrimaryKeyValue.fromString(id)); //设置需要读取的索引列最小值。
        startPrimaryKeyBuilder.addPrimaryKeyColumn("oId", PrimaryKeyValue.INF_MIN); //设置需要读取的索引列最小值。
        rangeRowQueryCriteria.setInclusiveStartPrimaryKey(startPrimaryKeyBuilder.build());

        //设置结束主键。
        PrimaryKeyBuilder endPrimaryKeyBuilder = PrimaryKeyBuilder.createPrimaryKeyBuilder();
        endPrimaryKeyBuilder.addPrimaryKeyColumn("c_id", PrimaryKeyValue.fromString(id)); //设置需要读取的索引列最大值。
        endPrimaryKeyBuilder.addPrimaryKeyColumn("oId", PrimaryKeyValue.INF_MAX); //设置需要读取的索引列最大值。
        rangeRowQueryCriteria.setExclusiveEndPrimaryKey(endPrimaryKeyBuilder.build());
        rangeRowQueryCriteria.setMaxVersions(1);

        List<String> orderIdList = new ArrayList<>();
        while (true) {
            GetRangeResponse getRangeResponse = syncClient.getRange(new GetRangeRequest(rangeRowQueryCriteria));
            for (Row row : getRangeResponse.getRows()) {
                PrimaryKeyColumn col = row.getPrimaryKey().getPrimaryKeyColumn("oId");
                orderIdList.add(col.getValue().toString());
            }

            //如果nextStartPrimaryKey不为null, 则继续读取。
            if (getRangeResponse.getNextStartPrimaryKey() != null) {
                rangeRowQueryCriteria.setInclusiveStartPrimaryKey(getRangeResponse.getNextStartPrimaryKey());
            } else {
                break;
            }
        }

        return orderIdList;
    }


    public List<String> searchPIdByCId(String id) {
        RangeRowQueryCriteria rangeRowQueryCriteria = new RangeRowQueryCriteria("idx_cId"); //设置索引表名称。

        //设置起始主键。
        PrimaryKeyBuilder startPrimaryKeyBuilder = PrimaryKeyBuilder.createPrimaryKeyBuilder();
        startPrimaryKeyBuilder.addPrimaryKeyColumn("c_id", PrimaryKeyValue.fromString(id));
        startPrimaryKeyBuilder.addPrimaryKeyColumn("oId", PrimaryKeyValue.INF_MIN); //设置需要读取的索引列最小值。
        rangeRowQueryCriteria.setInclusiveStartPrimaryKey(startPrimaryKeyBuilder.build());

        //设置结束主键。
        PrimaryKeyBuilder endPrimaryKeyBuilder = PrimaryKeyBuilder.createPrimaryKeyBuilder();
        endPrimaryKeyBuilder.addPrimaryKeyColumn("c_id", PrimaryKeyValue.fromString(id));
        endPrimaryKeyBuilder.addPrimaryKeyColumn("oId", PrimaryKeyValue.INF_MAX); //设置需要读取的索引列最大值。
        rangeRowQueryCriteria.setExclusiveEndPrimaryKey(endPrimaryKeyBuilder.build());
        rangeRowQueryCriteria.setMaxVersions(1);

        List<String> orderIdList = new ArrayList<>();
        while (true) {
            GetRangeResponse getRangeResponse = syncClient.getRange(new GetRangeRequest(rangeRowQueryCriteria));
            for (Row row : getRangeResponse.getRows()) {
                PrimaryKeyColumn col = row.getPrimaryKey().getPrimaryKeyColumn("oId");

                PrimaryKeyBuilder mainTablePKBuilder = PrimaryKeyBuilder.createPrimaryKeyBuilder();
                mainTablePKBuilder.addPrimaryKeyColumn("oId", col.getValue());
                PrimaryKey mainTablePK = mainTablePKBuilder.build(); //根据索引表主键构造数据表主键。

                //反查数据表。
                SingleRowQueryCriteria criteria = new SingleRowQueryCriteria("order_contract", mainTablePK);
                criteria.addColumnsToGet("p_id"); //设置读取数据表的商品id列
                //设置读取最新版本。
                criteria.setMaxVersions(1);
                GetRowResponse getRowResponse = syncClient.getRow(new GetRowRequest(criteria));
                Row mainTableRow = getRowResponse.getRow();
                orderIdList.add(mainTableRow.getColumn("p_id").get(0).getValue().toString());
            }

            //如果nextStartPrimaryKey不为null, 则继续读取。
            if (getRangeResponse.getNextStartPrimaryKey() != null) {
                rangeRowQueryCriteria.setInclusiveStartPrimaryKey(getRangeResponse.getNextStartPrimaryKey());
            } else {
                break;
            }
        }

        return orderIdList;
    }



public List<ConsumerTradeValue>  getMaxTradeCustomer100() {
    // 组装请求参数
    SearchRequest searchRequest = SearchRequest.newBuilder()
            .tableName("order_contract")
            .indexName("order_contract_index")
            .addColumnsToGet("c_id","total_price")
            .searchQuery(
                    SearchQuery.newBuilder()
                            .query(QueryBuilders.range("pay_time").greaterThan(1624982400000000L))
                            .addGroupBy(GroupByBuilders.groupByField("c_id","c_id")
                                    .addGroupBySorter(GroupBySorter.subAggSortInDesc("sumPrice"))
                                    .addSubAggregation(AggregationBuilders.sum("sumPrice", "total_price"))
                            .size(100))
                            .build())
            .build();


    // 进行搜索
    long t1 = System.currentTimeMillis();
    SearchResponse response = syncClient.search(searchRequest);
    long t2 = System.currentTimeMillis();
    System.out.println("using:" + (t2 - t1));

    List<ConsumerTradeValue> resList = new ArrayList<ConsumerTradeValue>();
    List<GroupByFieldResultItem> items = response.getGroupByResults().getAsGroupByFieldResult("c_id").getGroupByFieldResultItems();
    for (GroupByFieldResultItem item : items) {
        double tradeValue = item.getSubAggregationResults().getAsSumAggregationResult("sumPrice").getValue();
        String userID = item.getKey();
        resList.add(new ConsumerTradeValue(userID, tradeValue));
    }

    return resList;
}


    public void getCombinationQuery() {
        SearchRequest searchRequest = SearchRequest.newBuilder()
                .tableName("order_contract")
                .indexName("order_contract_index")
                .searchQuery(
                        SearchQuery.newBuilder()
                                .query(QueryBuilders.bool().must(QueryBuilders.range("total_price").greaterThan(2000))
                                .must(QueryBuilders.wildcard("p_brand","*牌22*"))
                                .must(QueryBuilders.range("pay_time").greaterThan(1624982400000000L)))
                                .sort(new Sort(Arrays.asList(new FieldSort("p_price", SortOrder.DESC))))
                                .limit(1000)
                                .build())
                .build();
        long t1 = System.currentTimeMillis();
        SearchResponse response = syncClient.search(searchRequest);
        long t2 = System.currentTimeMillis();
        System.out.println("using:" + (t2 - t1));
    }
}
