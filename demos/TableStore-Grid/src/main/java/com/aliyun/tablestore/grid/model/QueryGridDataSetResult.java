package com.aliyun.tablestore.grid.model;

import java.util.List;

public class QueryGridDataSetResult {

    private List<GridDataSetMeta> gridDataSetMetas;
    private long totalCount;
    private boolean isAllSuccess;
    private byte[] nextToken;

    public List<GridDataSetMeta> getGridDataSetMetas() {
        return gridDataSetMetas;
    }

    public void setGridDataSetMetas(List<GridDataSetMeta> gridDataSetMetas) {
        this.gridDataSetMetas = gridDataSetMetas;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public boolean isAllSuccess() {
        return isAllSuccess;
    }

    public void setAllSuccess(boolean allSuccess) {
        isAllSuccess = allSuccess;
    }

    public byte[] getNextToken() {
        return nextToken;
    }

    public void setNextToken(byte[] nextToken) {
        this.nextToken = nextToken;
    }
}
