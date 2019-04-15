package com.aliyun.tablestore.example.grid;

import com.aliyun.tablestore.example.grid.common.ExampleConfig;
import com.aliyun.tablestore.example.grid.common.TableStoreGridExample;
import com.aliyun.tablestore.grid.GridDataFetcher;
import com.aliyun.tablestore.grid.model.grid.Grid4D;
import ucar.ma2.Array;

import java.util.Arrays;

public class DataFetchExample extends TableStoreGridExample {

    public DataFetchExample() {
        super(ExampleConfig.GRID_DATA_TABLE_NAME, ExampleConfig.GRID_META_TABLE_NAME);
    }

    public Array queryByTableStore(String dataSetId, String variable, int[] origin, int[] shape) throws Exception {
        GridDataFetcher fetcher = this.tableStoreGrid.getDataFetcher(this.tableStoreGrid.getDataSetMeta(dataSetId));
        fetcher.setVariablesToGet(Arrays.asList(variable));
        fetcher.setOriginShape(origin, shape);
        Grid4D grid4D = fetcher.fetch().getVariable(variable);
        return grid4D.toArray();
    }

    /**
     * get a plane.
     * @throws Exception
     */
    public void fetch1() throws Exception {
        int[] origin = new int[] {0, 0, 0, 0};
        int[] shape = new int[] {1, 1, ExampleConfig.EXAMPLE_GRID_DATA_SHAPE[2], ExampleConfig.EXAMPLE_GRID_DATA_SHAPE[3]};
        Array array = queryByTableStore(ExampleConfig.EXAMPLE_GRID_DATA_SET_ID,
                ExampleConfig.EXAMPLE_GRID_DATA_VARIABLE,
                origin, shape);
        System.out.println("Shape: " + array.shapeToString());
        System.out.println("Data: " + array.toString());
    }

    /**
     * get an sequence of point with different levels.
     * @throws Exception
     */
    public void fetch2() throws Exception {
        int[] origin = new int[] {0, 0, 0, 0};
        int[] shape = new int[] {1, ExampleConfig.EXAMPLE_GRID_DATA_SHAPE[1], 1, 1};
        Array array = queryByTableStore(ExampleConfig.EXAMPLE_GRID_DATA_SET_ID,
                ExampleConfig.EXAMPLE_GRID_DATA_VARIABLE,
                origin, shape);
        System.out.println("Shape:" + array.shapeToString());
        System.out.println("Data:" + array.toString());
    }

    /**
     * get arbitrary 4-dimension data.
     * @throws Exception
     */
    public void fetch3() throws Exception {
        int[] origin = new int[] {2, 5, 10, 10};
        int[] shape = new int[] {3, 10, 30, 30};
        Array array = queryByTableStore(ExampleConfig.EXAMPLE_GRID_DATA_SET_ID,
                ExampleConfig.EXAMPLE_GRID_DATA_VARIABLE,
                origin, shape);
        System.out.println("Shape:" + array.shapeToString());
        System.out.println("Data:" + array.toString());
    }

    public void run() throws Exception {
        fetch1();
        fetch2();
        fetch3();
    }

    public static void main(String[] args) throws Exception {
        DataFetchExample example = new DataFetchExample();
        try {
            example.run();
        } finally {
            example.close();
        }
    }
}
