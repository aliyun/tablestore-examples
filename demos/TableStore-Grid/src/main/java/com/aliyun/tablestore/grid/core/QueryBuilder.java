package com.aliyun.tablestore.grid.core;

import com.alicloud.openservices.tablestore.model.ColumnValue;
import com.alicloud.openservices.tablestore.model.search.query.*;
import com.aliyun.tablestore.grid.utils.ValueUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QueryBuilder {

    enum Operator {
        AND,
        OR
    }

    private Operator operator;
    private List<Query> filterQueries;
    private List<Query> shouldQueries;

    QueryBuilder(Operator operator) {
        this.operator = operator;
        switch (operator) {
            case AND: {
                this.filterQueries = new ArrayList<Query>();
                break;
            }
            case OR: {
                this.shouldQueries = new ArrayList<Query>();
                break;
            }
            default:
                throw new IllegalArgumentException();
        }
    }

    public static QueryBuilder or() {
        return new QueryBuilder(Operator.OR);
    }

    public static QueryBuilder and() {
        return new QueryBuilder(Operator.AND);
    }

    public QueryBuilder equal(String columnName, Object... values) {
        TermsQuery termsQuery = new TermsQuery();
        termsQuery.setFieldName(columnName);
        List<ColumnValue> columnValues = new ArrayList<ColumnValue>();
        for (Object value : values) {
            columnValues.add(ValueUtil.toColumnValue(value));
        }
        termsQuery.setTerms(columnValues);
        return query(termsQuery);
    }

    public QueryBuilder notEqual(String columnName, Object value) {
        TermQuery termQuery = new TermQuery();
        termQuery.setFieldName(columnName);
        termQuery.setTerm(ValueUtil.toColumnValue(value));
        BoolQuery boolQuery = new BoolQuery();
        boolQuery.setMustNotQueries(Arrays.<Query>asList(termQuery));
        return query(boolQuery);
    }

    public QueryBuilder greaterThan(String columnName, Object value) {
        RangeQuery rangeQuery = new RangeQuery();
        rangeQuery.setFieldName(columnName);
        rangeQuery.greaterThan(ValueUtil.toColumnValue(value));
        return query(rangeQuery);
    }

    public QueryBuilder greaterThanEqual(String columnName, Object value) {
        RangeQuery rangeQuery = new RangeQuery();
        rangeQuery.setFieldName(columnName);
        rangeQuery.greaterThanOrEqual(ValueUtil.toColumnValue(value));
        return query(rangeQuery);
    }

    public QueryBuilder lessThan(String columnName, Object value) {
        RangeQuery rangeQuery = new RangeQuery();
        rangeQuery.setFieldName(columnName);
        rangeQuery.lessThan(ValueUtil.toColumnValue(value));
        return query(rangeQuery);
    }

    public QueryBuilder lessThanEqual(String columnName, Object value) {
        RangeQuery rangeQuery = new RangeQuery();
        rangeQuery.setFieldName(columnName);
        rangeQuery.lessThanOrEqual(ValueUtil.toColumnValue(value));
        return query(rangeQuery);
    }

    public QueryBuilder prefix(String columnName, String prefix) {
        PrefixQuery prefixQuery = new PrefixQuery();
        prefixQuery.setFieldName(columnName);
        prefixQuery.setPrefix(prefix);
        return query(prefixQuery);
    }

    public QueryBuilder query(Query query) {
        switch (operator) {
            case AND: {
                this.filterQueries.add(query);
                break;
            }
            case OR: {
                this.shouldQueries.add(query);
                break;
            }
            default:
                throw new IllegalArgumentException();
        }
        return this;
    }

    public Query build() {
        switch (operator) {
            case AND: {
                BoolQuery boolQuery = new BoolQuery();
                boolQuery.setFilterQueries(filterQueries);
                return boolQuery;
            }
            case OR: {
                BoolQuery boolQuery = new BoolQuery();
                boolQuery.setShouldQueries(shouldQueries);
                return boolQuery;
            }
            default:
                throw new IllegalArgumentException();
        }
    }
}
