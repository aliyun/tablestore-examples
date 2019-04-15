package com.aliyun.tablestore.grid.model;


import com.alicloud.openservices.tablestore.model.search.sort.Sort;

public class QueryParams {

    private Integer offset;
    private Integer limit;
    private Sort sort;
    private byte[] token;
    private boolean getTotalCount = false;

    public QueryParams() {
    }

    public QueryParams(int limit) {
        this.limit = limit;
    }

    public QueryParams(int offset, int limit) {
        this.offset = offset;
        this.limit = limit;
    }

    public QueryParams(int offset, int limit, Sort sort) {
        this.offset = offset;
        this.limit = limit;
        this.sort = sort;
    }

    public QueryParams(byte[] token, int limit) {
        this.token = token;
        this.limit = limit;
    }


    public Integer getOffset() {
        return offset;
    }

    public QueryParams setOffset(Integer offset) {
        this.offset = offset;
        return this;
    }

    public Integer getLimit() {
        return limit;
    }

    public QueryParams setLimit(Integer limit) {
        this.limit = limit;
        return this;
    }

    public Sort getSort() {
        return sort;
    }

    public QueryParams setSort(Sort sort) {
        this.sort = sort;
        return this;
    }

    public byte[] getToken() {
        return token;
    }

    public QueryParams setToken(byte[] token) {
        this.token = token;
        return this;
    }

    public boolean isGetTotalCount() {
        return getTotalCount;
    }

    public QueryParams setGetTotalCount(boolean getTotalCount) {
        this.getTotalCount = getTotalCount;
        return this;
    }
}
