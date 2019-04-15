package com.aliyun.tablestore.example.grid;

import com.alicloud.openservices.tablestore.model.search.sort.FieldSort;
import com.alicloud.openservices.tablestore.model.search.sort.Sort;
import com.alicloud.openservices.tablestore.model.search.sort.SortOrder;
import com.aliyun.tablestore.example.grid.common.ExampleConfig;
import com.aliyun.tablestore.example.grid.common.TableStoreGridExample;
import com.aliyun.tablestore.grid.core.QueryBuilder;
import com.aliyun.tablestore.grid.model.GridDataSetMeta;
import com.aliyun.tablestore.grid.model.QueryGridDataSetResult;
import com.aliyun.tablestore.grid.model.QueryParams;
import com.aliyun.tablestore.grid.model.StoreOptions;
import ucar.ma2.DataType;

import java.util.*;

public class MetaQueryExample extends TableStoreGridExample {

    public MetaQueryExample() {
        super(ExampleConfig.GRID_DATA_TABLE_NAME, ExampleConfig.GRID_META_TABLE_NAME);
    }

    private GridDataSetMeta initMeta(String uniqueKey, DataType dataType, List<String> variables, int[] shape, Map<String, Object> attributes) throws Exception {
        GridDataSetMeta meta = new GridDataSetMeta(
                uniqueKey,
                dataType,
                variables,
                shape[0],
                shape[1],
                shape[2],
                shape[3],
                new StoreOptions(StoreOptions.StoreType.SLICE));
        meta.setAttributes(attributes);
        tableStoreGrid.putDataSetMeta(meta);
        return meta;
    }

    /**
     * write some metas to table store for test.
     * @throws Exception
     */
    private void prepareMetas() throws Exception {
        for (String tagA : Arrays.asList("A", "B", "C")) {
            for (String tagB : Arrays.asList("X", "Y", "Z")) {
                for (int i = 0; i < 10; i++) {
                    String dataSetId = UUID.randomUUID().toString();
                    Map<String, Object> objectMap = new HashMap<String, Object>();
                    objectMap.put("create_time", System.currentTimeMillis());
                    objectMap.put("tag1", tagA);
                    objectMap.put("tag2", tagB);
                    objectMap.put("status", ((i % 2) == 0)?"INIT":"DONE");
                    initMeta(dataSetId, DataType.FLOAT, Arrays.asList("var"), new int[]{1, 1, 1, 1}, objectMap);
                }
            }
        }
    }

    /**
     * query by arbitrary combination conditions.
     * @throws Exception
     */
    private void query() throws Exception {
        /**
         * query condition : (status == DONE) and (create_time > System.currentTimeMillis - 86400000) and (tag1 == A or tag2 == X)
         * sort by create_time, desc.
         */
        QueryGridDataSetResult result = tableStoreGrid.queryDataSets(
                ExampleConfig.GRID_META_INDEX_NAME,
                QueryBuilder.and()
                        .equal("status", "DONE")
                        .greaterThan("create_time", System.currentTimeMillis() - 86400000)
                        .query(QueryBuilder.or()
                                .equal("tag1", "A")
                                .equal("tag2", "X")
                                .build())
                        .build(),
                new QueryParams(0, 10, new Sort(Arrays.<Sort.Sorter>asList(new FieldSort("create_time", SortOrder.DESC)))));

        System.out.println("GetMetaCount: " + result.getGridDataSetMetas().size());
        for (GridDataSetMeta meta : result.getGridDataSetMetas()) {
            System.out.println("Meta: " + meta.getGridDataSetId());
            System.out.println(meta.getAttributes());
        }
    }

    public static void main(String[] args) throws Exception {
        MetaQueryExample example = new MetaQueryExample();
        try {
            example.prepareMetas();
            System.out.println("Sleep 15s to wait new data sync to index...");
            Thread.sleep(15000); // wait data sync to index
            System.out.println("Query...");
            example.query();
        } finally {
            example.close();
        }
    }
}
