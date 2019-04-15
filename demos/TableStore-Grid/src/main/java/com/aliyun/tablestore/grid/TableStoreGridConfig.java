package com.aliyun.tablestore.grid;

public class TableStoreGridConfig {

    private String tableStoreEndpoint;
    private String accessId;
    private String accessKey;
    private String tableStoreInstance;

    private String metaTableName;
    private String dataTableName;

    private long dataSizeLimitForFetch = 20 * 1024 * 1024;

    public String getTableStoreEndpoint() {
        return tableStoreEndpoint;
    }

    public void setTableStoreEndpoint(String tableStoreEndpoint) {
        this.tableStoreEndpoint = tableStoreEndpoint;
    }

    public String getAccessId() {
        return accessId;
    }

    public void setAccessId(String accessId) {
        this.accessId = accessId;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getTableStoreInstance() {
        return tableStoreInstance;
    }

    public void setTableStoreInstance(String tableStoreInstance) {
        this.tableStoreInstance = tableStoreInstance;
    }

    public String getMetaTableName() {
        return metaTableName;
    }

    public void setMetaTableName(String metaTableName) {
        this.metaTableName = metaTableName;
    }

    public String getDataTableName() {
        return dataTableName;
    }

    public void setDataTableName(String dataTableName) {
        this.dataTableName = dataTableName;
    }

    public long getDataSizeLimitForFetch() {
        return dataSizeLimitForFetch;
    }

    public void setDataSizeLimitForFetch(long dataSizeLimitForFetch) {
        this.dataSizeLimitForFetch = dataSizeLimitForFetch;
    }
}
