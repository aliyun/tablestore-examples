package com.aliyun.tablestore.grid.core;

import com.alicloud.openservices.tablestore.core.utils.StringUtils;
import com.alicloud.openservices.tablestore.model.*;
import com.alicloud.openservices.tablestore.model.search.SearchQuery;
import com.alicloud.openservices.tablestore.model.search.SearchRequest;
import com.alicloud.openservices.tablestore.model.search.query.Query;
import com.aliyun.tablestore.grid.model.GetDataParam;
import com.aliyun.tablestore.grid.model.GridDataSetMeta;
import com.aliyun.tablestore.grid.model.QueryParams;
import com.aliyun.tablestore.grid.model.StoreOptions;
import com.aliyun.tablestore.grid.utils.ValueUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.aliyun.tablestore.grid.consts.Constants.*;

public class RequestBuilder {

    public static CreateTableRequest buildCreateMetaTableRequest(String tableName) {
        TableMeta meta = new TableMeta(tableName);
        meta.addPrimaryKeyColumn(new PrimaryKeySchema(GRID_DATA_SET_ID_PK_NAME, PrimaryKeyType.STRING));
        return new CreateTableRequest(meta, new TableOptions(-1, 1));
    }

    public static CreateTableRequest buildCreateDataTableRequest(String tableName) {
        TableMeta meta = new TableMeta(tableName);
        meta.addPrimaryKeyColumn(new PrimaryKeySchema(GRID_DATA_SET_ID_PK_NAME, PrimaryKeyType.STRING));
        meta.addPrimaryKeyColumn(new PrimaryKeySchema(VARIABLE_PK_NAME, PrimaryKeyType.STRING));
        meta.addPrimaryKeyColumn(new PrimaryKeySchema(T_PK_NAME, PrimaryKeyType.INTEGER));
        meta.addPrimaryKeyColumn(new PrimaryKeySchema(Z_PK_NAME, PrimaryKeyType.INTEGER));
        return new CreateTableRequest(meta, new TableOptions(-1, 1));
    }

    private static List<Column> buildMetaColumns(GridDataSetMeta meta) {
        List<Column> columns = new ArrayList<Column>();
        columns.add(new Column(DATA_TYPE_COL_NAME, ColumnValue.fromString(meta.getDataType().toString())));
        columns.add(new Column(VARIABLE_LIST_COL_NAME, ColumnValue.fromString(StringUtils.join(",", meta.getVariables()))));
        columns.add(new Column(T_SIZE_COL_NAME, ColumnValue.fromLong(meta.gettSize())));
        columns.add(new Column(Z_SIZE_COL_NAME, ColumnValue.fromLong(meta.getzSize())));
        columns.add(new Column(X_SIZE_COL_NAME, ColumnValue.fromLong(meta.getxSize())));
        columns.add(new Column(Y_SIZE_COL_NAME, ColumnValue.fromLong(meta.getySize())));
        columns.add(new Column(STORE_TYPE_COL_NAME, ColumnValue.fromString(meta.getStoreOptions().getStoreType().name())));
        if (meta.getStoreOptions().getStoreType().equals(StoreOptions.StoreType.SLICE)) {
            columns.add(new Column(X_SPLIT_COUNT_COL_NAME, ColumnValue.fromLong(meta.getStoreOptions().getxSplitCount())));
            columns.add(new Column(Y_SPLIT_COUNT_COL_NAME, ColumnValue.fromLong(meta.getStoreOptions().getySplitCount())));
        }
        if (meta.getAttributes() != null) {
            for (Map.Entry<String, Object> entry : meta.getAttributes().entrySet()) {
                columns.add(new Column(entry.getKey(), ValueUtil.toColumnValue(entry.getValue())));
            }
        }
        return columns;
    }

    public static PutRowRequest buildPutMetaRequest(String metaTableName, GridDataSetMeta meta) {
        PrimaryKeyBuilder builder = PrimaryKeyBuilder.createPrimaryKeyBuilder();
        builder.addPrimaryKeyColumn(GRID_DATA_SET_ID_PK_NAME, PrimaryKeyValue.fromString(meta.getGridDataSetId()));
        PrimaryKey pk = builder.build();
        RowPutChange rowPutChange = new RowPutChange(metaTableName, pk);
        rowPutChange.addColumns(buildMetaColumns(meta));
        return new PutRowRequest(rowPutChange);
    }

    public static UpdateRowRequest buildUpdateMetaRequest(String metaTableName, GridDataSetMeta meta) {
        PrimaryKeyBuilder builder = PrimaryKeyBuilder.createPrimaryKeyBuilder();
        builder.addPrimaryKeyColumn(GRID_DATA_SET_ID_PK_NAME, PrimaryKeyValue.fromString(meta.getGridDataSetId()));
        PrimaryKey pk = builder.build();
        RowUpdateChange rowUpdateChange = new RowUpdateChange(metaTableName, pk);
        rowUpdateChange.put(buildMetaColumns(meta));
        return new UpdateRowRequest(rowUpdateChange);
    }

    public static GetRowRequest buildGetMetaRequest(String metaTableName, String uniqueKey) {
        PrimaryKeyBuilder builder = PrimaryKeyBuilder.createPrimaryKeyBuilder();
        builder.addPrimaryKeyColumn(GRID_DATA_SET_ID_PK_NAME, PrimaryKeyValue.fromString(uniqueKey));
        PrimaryKey pk = builder.build();
        SingleRowQueryCriteria criteria = new SingleRowQueryCriteria(metaTableName);
        criteria.setMaxVersions(1);
        criteria.setPrimaryKey(pk);
        GetRowRequest getRowRequest = new GetRowRequest();
        getRowRequest.setRowQueryCriteria(criteria);
        return getRowRequest;
    }

    public static SearchRequest buildSearchRequest(String metaTableName, String indexName, Query query, QueryParams params) {
        SearchQuery searchQuery = new SearchQuery();
        searchQuery.setQuery(query);
        if (params.getOffset() != null) {
            searchQuery.setOffset(params.getOffset());
        }
        if (params.getLimit() != null) {
            searchQuery.setLimit(params.getLimit());
        }
        if (params.getSort() != null) {
            searchQuery.setSort(params.getSort());
        }
        if (params.getToken() != null) {
            searchQuery.setToken(params.getToken());
        }
        searchQuery.setGetTotalCount(params.isGetTotalCount());
        SearchRequest searchRequest = new SearchRequest(metaTableName, indexName, searchQuery);
        SearchRequest.ColumnsToGet columnsToGet = new SearchRequest.ColumnsToGet();
        columnsToGet.setReturnAll(true);
        searchRequest.setColumnsToGet(columnsToGet);
        return searchRequest;
    }

    public static GetRowRequest buildGetDataRequest(GetDataParam getDataParam) {
        PrimaryKeyBuilder builder = PrimaryKeyBuilder.createPrimaryKeyBuilder();
        builder.addPrimaryKeyColumn(GRID_DATA_SET_ID_PK_NAME, PrimaryKeyValue.fromString(getDataParam.getDataSetId()));
        builder.addPrimaryKeyColumn(VARIABLE_PK_NAME, PrimaryKeyValue.fromString(getDataParam.getVariable()));
        builder.addPrimaryKeyColumn(T_PK_NAME, PrimaryKeyValue.fromLong(getDataParam.getT()));
        builder.addPrimaryKeyColumn(Z_PK_NAME, PrimaryKeyValue.fromLong(getDataParam.getZ()));
        PrimaryKey pk = builder.build();

        SingleRowQueryCriteria criteria = new SingleRowQueryCriteria(getDataParam.getDataTableName());
        criteria.setMaxVersions(1);
        criteria.setPrimaryKey(pk);
        if (getDataParam.getColumnsToGet() != null) {
            criteria.addColumnsToGet(getDataParam.getColumnsToGet());
        }
        return new GetRowRequest(criteria);
    }

}
