package com.aliyun.tablestore.example.grid.common;

import ucar.ma2.DataType;

public class ExampleConfig {

    /**
     * table name and index name
     */
    public static String GRID_DATA_TABLE_NAME = "GRID_DATA_TABLE_EXAMPLE";
    public static String GRID_META_TABLE_NAME = "GRID_META_TABLE_EXAMPLE";
    public static String GRID_META_INDEX_NAME = "GRID_META_INDEX";


    /**
     * data set config
     */
    public static String EXAMPLE_GRID_DATA_SET_ID = "test_echam_spectral_example";
    public static String EXAMPLE_GRID_DATA_SET_NC_FILE_PATH = "test_echam_spectral.nc";
    public static String EXAMPLE_GRID_DATA_VARIABLE = "tpot";
    public static int[] EXAMPLE_GRID_DATA_SHAPE = new int[]{8, 47, 96, 192};
    public static DataType EXAMPLE_GRID_DATA_TYPE = DataType.FLOAT;
}
