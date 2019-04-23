package com.aliyun.tablestore.example;

import com.alicloud.openservices.tablestore.model.ColumnValue;
import com.alicloud.openservices.tablestore.model.Row;
import com.alicloud.openservices.tablestore.model.search.Collapse;
import com.alicloud.openservices.tablestore.model.search.SearchQuery;
import com.alicloud.openservices.tablestore.model.search.SearchRequest;
import com.alicloud.openservices.tablestore.model.search.SearchResponse;
import com.alicloud.openservices.tablestore.model.search.query.BoolQuery;
import com.alicloud.openservices.tablestore.model.search.query.NestedQuery;
import com.alicloud.openservices.tablestore.model.search.query.RangeQuery;
import com.alicloud.openservices.tablestore.model.search.query.ScoreMode;
import com.alicloud.openservices.tablestore.model.search.query.TermQuery;
import com.alicloud.openservices.tablestore.model.search.sort.FieldSort;
import com.alicloud.openservices.tablestore.model.search.sort.Sort;
import com.alicloud.openservices.tablestore.model.search.sort.SortOrder;
import com.aliyun.tablestore.example.model.PolicyDO;
import com.aliyun.tablestore.example.utils.ClientAndConfig;
import com.aliyun.tablestore.example.utils.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import static com.aliyun.tablestore.example.consts.ColumnConsts.APPLIER_NAME;
import static com.aliyun.tablestore.example.consts.ColumnConsts.BENEFICIARY_INFO;
import static com.aliyun.tablestore.example.consts.ColumnConsts.BENEFIT_PERCENTAGE;
import static com.aliyun.tablestore.example.consts.ColumnConsts.EXPIRATION_TIME;
import static com.aliyun.tablestore.example.consts.ColumnConsts.NAME;
import static com.aliyun.tablestore.example.consts.ColumnConsts.PROFIT;

/**
 * @author hydrogen
 */
public class SearchPolicyExample extends BaseExample {
    public SearchPolicyExample(ClientAndConfig clientAndConfig) {
        super(clientAndConfig);
    }

    public static void main(String[] args) {
        new SearchPolicyExample(Utils.getClientAndConfig(args)).main();
    }

    private void printResult(SearchResponse searchResponse) {
        System.out.println("Total counts: " + searchResponse.getTotalCount());
        for (Row row : searchResponse.getRows()) {
            System.out.println(PolicyDO.fromRow(row));
        }
    }

    /**
     * search applier Vernon Richardson's policies
     */
    private void searchExample1() {
        SearchQuery searchQuery = new SearchQuery();
        // get total count
        searchQuery.setGetTotalCount(true);

        // search applier Vernon Richardson's policy
        TermQuery termQuery = new TermQuery();
        termQuery.setFieldName(APPLIER_NAME);
        termQuery.setTerm(ColumnValue.fromString("Vernon Richardson"));
        searchQuery.setQuery(termQuery);
        SearchRequest searchRequest = new SearchRequest(tableName, indexName, searchQuery);
        // specify columns to get
        SearchRequest.ColumnsToGet columnsToGet = new SearchRequest.ColumnsToGet();
        // get all columns
        columnsToGet.setReturnAll(true);
        searchRequest.setColumnsToGet(columnsToGet);
        SearchResponse searchResponse = syncClient.search(searchRequest);
        printResult(searchResponse);
    }

    /**
     * search applier Vernon Richardson's polices which will expire after 2019-03-04
     */
    private void searchexample2() {
        SearchQuery searchQuery = new SearchQuery();
        searchQuery.setGetTotalCount(true);

        BoolQuery boolQuery = new BoolQuery();

        TermQuery applierNameQuery = new TermQuery();
        applierNameQuery.setFieldName(APPLIER_NAME);
        applierNameQuery.setTerm(ColumnValue.fromString("Vernon Richardson"));

        RangeQuery expirationTimeQuery = new RangeQuery();
        expirationTimeQuery.setFieldName(EXPIRATION_TIME);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date;
        try {
            date = simpleDateFormat.parse("2019-03-04");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        expirationTimeQuery.setTo(ColumnValue.fromLong(date.getTime()), true);

        // use BoolQuery to combine other queries
        boolQuery.setMustQueries(Arrays.asList(
                applierNameQuery,
                expirationTimeQuery
        ));

        searchQuery.setQuery(boolQuery);

        SearchRequest searchRequest = new SearchRequest(tableName, indexName, searchQuery);
        // specify columns to get
        SearchRequest.ColumnsToGet columnsToGet = new SearchRequest.ColumnsToGet();
        // get all columns
        columnsToGet.setReturnAll(true);
        searchRequest.setColumnsToGet(columnsToGet);
        SearchResponse searchResponse = syncClient.search(searchRequest);
        printResult(searchResponse);
    }

    /**
     * search applier Lance Rivera or David Hayes's polices
     */
    private void searchexample3() {
        SearchQuery searchQuery = new SearchQuery();
        searchQuery.setGetTotalCount(true);

        BoolQuery boolQuery = new BoolQuery();

        TermQuery name1TermQuery = new TermQuery();
        name1TermQuery.setFieldName(APPLIER_NAME);
        name1TermQuery.setTerm(ColumnValue.fromString("Lance Rivera"));

        TermQuery name2TermQuery = new TermQuery();
        name2TermQuery.setFieldName(APPLIER_NAME);
        name2TermQuery.setTerm(ColumnValue.fromString("David Hayes"));

        boolQuery.setShouldQueries(Arrays.asList(
                name1TermQuery,
                name2TermQuery
        ));
        // minimum should match: at lease 1 query in shouldQueries should match
        boolQuery.setMinimumShouldMatch(1);

        searchQuery.setQuery(boolQuery);

        SearchRequest searchRequest = new SearchRequest(tableName, indexName, searchQuery);
        // specify columns to get
        SearchRequest.ColumnsToGet columnsToGet = new SearchRequest.ColumnsToGet();
        // get all columns
        columnsToGet.setReturnAll(true);
        searchRequest.setColumnsToGet(columnsToGet);
        SearchResponse searchResponse = syncClient.search(searchRequest);
        printResult(searchResponse);
    }

    /**
     * sort by profit desc, but policy with same applier_name will appear only once
     */
    private void searchexample4() {
        SearchQuery searchQuery = new SearchQuery();
        searchQuery.setGetTotalCount(true);

        // collapse with applier_name field, so that record with same applier_name will appear only once
        Collapse collapse = new Collapse();
        collapse.setFieldName(APPLIER_NAME);

        // sort by profit, desc
        Sort sort = new Sort(Collections.singletonList(new FieldSort(PROFIT, SortOrder.DESC)));
        searchQuery.setSort(sort);
        searchQuery.setCollapse(collapse);

        SearchRequest searchRequest = new SearchRequest(tableName, indexName, searchQuery);
        // specify columns to get
        SearchRequest.ColumnsToGet columnsToGet = new SearchRequest.ColumnsToGet();
        // get all columns
        columnsToGet.setReturnAll(true);
        searchRequest.setColumnsToGet(columnsToGet);
        SearchResponse searchResponse = syncClient.search(searchRequest);
        printResult(searchResponse);
    }

    /**
     * search beneficiary with name "Tyrone Lee" and profit percentage greater than 50%
     */
    private void searchexample5() {
        SearchQuery searchQuery = new SearchQuery();
        searchQuery.setGetTotalCount(true);

        NestedQuery nestedQuery = new NestedQuery();
        // search for nested field beneficiary_info
        nestedQuery.setPath(BENEFICIARY_INFO);
        nestedQuery.setScoreMode(ScoreMode.Avg);

        BoolQuery boolQuery = new BoolQuery();

        TermQuery beneficiaryNameQuery = new TermQuery();
        // concat field name with `.`
        beneficiaryNameQuery.setFieldName(BENEFICIARY_INFO + "." + NAME);
        beneficiaryNameQuery.setTerm(ColumnValue.fromString("Tyrone Lee"));

        RangeQuery profitPercentageQuery = new RangeQuery();
        // concat field name with `.`
        profitPercentageQuery.setFieldName(BENEFICIARY_INFO + "." + BENEFIT_PERCENTAGE);
        profitPercentageQuery.setFrom(ColumnValue.fromLong(50), true);

        boolQuery.setMustQueries(Arrays.asList(
                beneficiaryNameQuery,
                profitPercentageQuery
        ));

        nestedQuery.setQuery(boolQuery);

        searchQuery.setQuery(nestedQuery);
        SearchRequest searchRequest = new SearchRequest(tableName, indexName, searchQuery);
        // specify columns to get
        SearchRequest.ColumnsToGet columnsToGet = new SearchRequest.ColumnsToGet();
        // get all columns
        columnsToGet.setReturnAll(true);
        searchRequest.setColumnsToGet(columnsToGet);
        SearchResponse searchResponse = syncClient.search(searchRequest);
        printResult(searchResponse);

    }

    @Override
    protected void doMain() {
        searchExample1();
    }
}
