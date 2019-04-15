package com.aliyun.tablestore.grid;

import com.alicloud.openservices.tablestore.*;
import com.alicloud.openservices.tablestore.core.ErrorCode;
import com.alicloud.openservices.tablestore.model.GetRowResponse;
import com.alicloud.openservices.tablestore.model.Row;
import com.alicloud.openservices.tablestore.model.search.CreateSearchIndexRequest;
import com.alicloud.openservices.tablestore.model.search.IndexSchema;
import com.alicloud.openservices.tablestore.model.search.SearchResponse;
import com.alicloud.openservices.tablestore.model.search.query.Query;
import com.alicloud.openservices.tablestore.writer.WriterConfig;
import com.aliyun.tablestore.grid.core.RequestBuilder;
import com.aliyun.tablestore.grid.core.RowParser;
import com.aliyun.tablestore.grid.core.TableStoreDataFetcher;
import com.aliyun.tablestore.grid.core.TableStoreDataWriter;
import com.aliyun.tablestore.grid.model.GridDataSetMeta;
import com.aliyun.tablestore.grid.model.QueryGridDataSetResult;
import com.aliyun.tablestore.grid.model.QueryParams;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TableStoreGrid implements GridStore {

    private TableStoreGridConfig config;
    private AsyncClientInterface asyncClient;
    private ExecutorService writerExecutor;
    private TableStoreWriter writer;

    public TableStoreGrid(TableStoreGridConfig config) {
        this.config = config;
        this.asyncClient = new AsyncClient(config.getTableStoreEndpoint(),
                config.getAccessId(), config.getAccessKey(),
                config.getTableStoreInstance());
    }

    @Override
    public void createStore() throws Exception {
        // create meta table
        try {
            this.asyncClient.createTable(RequestBuilder.buildCreateMetaTableRequest(config.getMetaTableName()), null).get();
        } catch (TableStoreException ex) {
            if (!ex.getErrorCode().equals(ErrorCode.OBJECT_ALREADY_EXIST)) {
                throw ex;
            }
        }

        // create buffer table
        try {
            this.asyncClient.createTable(RequestBuilder.buildCreateDataTableRequest(config.getDataTableName()), null).get();
        } catch (TableStoreException ex) {
            if (!ex.getErrorCode().equals(ErrorCode.OBJECT_ALREADY_EXIST)) {
                throw ex;
            }
        }
    }

    @Override
    public void putDataSetMeta(GridDataSetMeta meta) throws Exception {
        this.asyncClient.putRow(
                RequestBuilder.buildPutMetaRequest(config.getMetaTableName(), meta), null).get();
    }

    @Override
    public void updateDataSetMeta(GridDataSetMeta meta) throws Exception {
        this.asyncClient.updateRow(
                RequestBuilder.buildUpdateMetaRequest(config.getMetaTableName(), meta), null).get();
    }

    @Override
    public GridDataSetMeta getDataSetMeta(String uniqueKey) throws Exception {
        GetRowResponse getRowResponse = this.asyncClient.getRow(
                RequestBuilder.buildGetMetaRequest(config.getMetaTableName(), uniqueKey), null).get();
        if (getRowResponse.getRow() == null) {
            return null;
        }
        return RowParser.parseMetaFromRow(getRowResponse.getRow());
    }

    @Override
    public void createMetaIndex(String indexName, IndexSchema indexSchema) throws Exception {
        CreateSearchIndexRequest request = new CreateSearchIndexRequest();
        request.setTableName(config.getMetaTableName());
        request.setIndexName(indexName);
        request.setIndexSchema(indexSchema);
        this.asyncClient.createSearchIndex(request, null).get();
    }

    @Override
    public QueryGridDataSetResult queryDataSets(String indexName, Query query, QueryParams queryParams) throws Exception {
        SearchResponse searchResponse = this.asyncClient.search(
                RequestBuilder.buildSearchRequest(config.getMetaTableName(), indexName, query, queryParams),
                null).get();
        List<GridDataSetMeta> metaList = new ArrayList<GridDataSetMeta>();
        for (Row row : searchResponse.getRows()) {
            metaList.add(RowParser.parseMetaFromRow(row));
        }
        QueryGridDataSetResult result = new QueryGridDataSetResult();
        result.setGridDataSetMetas(metaList);
        result.setAllSuccess(searchResponse.isAllSuccess());
        result.setNextToken(searchResponse.getNextToken());
        result.setTotalCount(searchResponse.getTotalCount());
        return result;
    }

    @Override
    public GridDataWriter getDataWriter(GridDataSetMeta meta) {
        if (writer == null) {
           synchronized (this) {
               if (writer == null) {
                   this.writerExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
                   this.writer = new DefaultTableStoreWriter(this.asyncClient, config.getDataTableName(), new WriterConfig(), null, this.writerExecutor);
               }
           }
        }
        return new TableStoreDataWriter(writer, config.getDataTableName(), meta);
    }

    public GridDataFetcher getDataFetcher(GridDataSetMeta meta) {
        return new TableStoreDataFetcher(asyncClient, config.getDataTableName(), meta, config.getDataSizeLimitForFetch());
    }

    public synchronized void close() {
        if (writer != null) {
            this.writer.close();
            this.writerExecutor.shutdown();
        }
        this.asyncClient.shutdown();
    }
}
