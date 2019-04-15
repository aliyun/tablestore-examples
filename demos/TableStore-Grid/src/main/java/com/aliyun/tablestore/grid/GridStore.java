package com.aliyun.tablestore.grid;

import com.alicloud.openservices.tablestore.model.search.IndexSchema;
import com.alicloud.openservices.tablestore.model.search.query.Query;
import com.aliyun.tablestore.grid.model.GridDataSetMeta;
import com.aliyun.tablestore.grid.model.QueryGridDataSetResult;
import com.aliyun.tablestore.grid.model.QueryParams;

public interface GridStore {

    void createStore() throws Exception;

    void putDataSetMeta(GridDataSetMeta meta) throws Exception;

    void updateDataSetMeta(GridDataSetMeta meta) throws Exception;

    GridDataSetMeta getDataSetMeta(String uniqueKey) throws Exception;

    void createMetaIndex(String indexName, IndexSchema indexSchema) throws Exception;

    QueryGridDataSetResult queryDataSets(String indexName, Query query, QueryParams queryParams) throws Exception;

    GridDataWriter getDataWriter(GridDataSetMeta meta);

    GridDataFetcher getDataFetcher(GridDataSetMeta meta);

    void close();

}
