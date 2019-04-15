package com.aliyun.tablestore.grid.model;

public class StoreOptions {

    public enum StoreType {
        SLICE
    }

    private StoreType storeType;
    private int xSplitCount = 10;
    private int ySplitCount = 10;

    public StoreOptions(StoreType storeType) {
        this.storeType = storeType;
    }

    public StoreType getStoreType() {
        return storeType;
    }

    public void setStoreType(StoreType storeType) {
        this.storeType = storeType;
    }

    public int getxSplitCount() {
        return xSplitCount;
    }

    public void setxSplitCount(int xSplitCount) {
        this.xSplitCount = xSplitCount;
    }

    public int getySplitCount() {
        return ySplitCount;
    }

    public void setySplitCount(int ySplitCount) {
        this.ySplitCount = ySplitCount;
    }

}
