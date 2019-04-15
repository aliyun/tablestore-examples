package com.aliyun.tablestore.example.grid;

import com.alicloud.openservices.tablestore.model.search.FieldSchema;
import com.alicloud.openservices.tablestore.model.search.FieldType;
import com.alicloud.openservices.tablestore.model.search.IndexSchema;
import com.aliyun.tablestore.example.grid.common.ExampleConfig;
import com.aliyun.tablestore.example.grid.common.TableStoreGridExample;

import java.util.Arrays;

/**
 * this example will create table and index.
 */
public class CreateStoreExample extends TableStoreGridExample {

    public CreateStoreExample() {
        super(ExampleConfig.GRID_DATA_TABLE_NAME, ExampleConfig.GRID_META_TABLE_NAME);
    }

    /**
     * we must create store before we can use it.
     * @throws Exception
     */
    private void createStore() throws Exception {
        this.tableStoreGrid.createStore();
    }

    /**
     * this example create an index which contains these columns: status, tag1, tag2, create_time.
     * you can create an index which contains any other columns.
     *
     * @throws Exception
     */
    private void createIndex() throws Exception {
        IndexSchema indexSchema = new IndexSchema();
        indexSchema.setFieldSchemas(Arrays.asList(
                new FieldSchema("status", FieldType.KEYWORD).setIndex(true).setEnableSortAndAgg(true),
                new FieldSchema("tag1", FieldType.KEYWORD).setIndex(true).setEnableSortAndAgg(true),
                new FieldSchema("tag2", FieldType.KEYWORD).setIndex(true).setEnableSortAndAgg(true),
                new FieldSchema("create_time", FieldType.LONG).setIndex(true).setEnableSortAndAgg(true)
        ));
        this.tableStoreGrid.createMetaIndex(ExampleConfig.GRID_META_INDEX_NAME, indexSchema);
    }

    public static void main(String[] args) throws Exception {
        CreateStoreExample example = new CreateStoreExample();
        try {
            example.createStore();
            example.createIndex();
        } finally {
            example.close();
        }
    }
}
