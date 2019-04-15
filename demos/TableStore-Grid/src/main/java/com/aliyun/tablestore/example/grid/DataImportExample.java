package com.aliyun.tablestore.example.grid;

import com.aliyun.tablestore.example.grid.common.ExampleConfig;
import com.aliyun.tablestore.example.grid.common.TableStoreGridExample;
import com.aliyun.tablestore.grid.GridDataWriter;
import com.aliyun.tablestore.grid.model.GridDataSetMeta;
import com.aliyun.tablestore.grid.model.StoreOptions;
import com.aliyun.tablestore.grid.model.grid.Grid2D;
import ucar.ma2.Array;
import ucar.ma2.DataType;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

import java.util.Arrays;
import java.util.List;

public class DataImportExample extends TableStoreGridExample {

    public DataImportExample() {
        super(ExampleConfig.GRID_DATA_TABLE_NAME, ExampleConfig.GRID_META_TABLE_NAME);
    }

    /**
     * init meta data to table store.
     * @param dataSetID
     * @param dataType
     * @param variables
     * @param shape
     * @return
     * @throws Exception
     */
    public GridDataSetMeta initMeta(String dataSetID, DataType dataType, List<String> variables, int[] shape) throws Exception {
        GridDataSetMeta meta = new GridDataSetMeta(
                dataSetID,
                dataType,
                variables,
                shape[0],
                shape[1],
                shape[2],
                shape[3],
                new StoreOptions(StoreOptions.StoreType.SLICE));
        meta.addAttribute("status", "INIT");
        meta.addAttribute("create_time", System.currentTimeMillis());
        tableStoreGrid.putDataSetMeta(meta);
        return meta;
    }

    /**
     * update meta and set status to DONE when data import finished.
     * @param meta
     * @return
     * @throws Exception
     */
    public GridDataSetMeta updateMeta(GridDataSetMeta meta) throws Exception {
        meta.addAttribute("status", "DONE");
        tableStoreGrid.updateDataSetMeta(meta);
        return meta;
    }

    /**
     * read data from netcdf file and write data to table store.
     * @param meta
     * @param ncFileName
     * @throws Exception
     */
    public void importFromNcFile(GridDataSetMeta meta, String ncFileName) throws Exception {
        GridDataWriter writer = tableStoreGrid.getDataWriter(meta);
        NetcdfFile ncFile = NetcdfFile.open(ncFileName);
        List<Variable> variables = ncFile.getVariables();
        for (Variable variable : variables) {
            if (meta.getVariables().contains(variable.getShortName())) {
                for (int t = 0; t < meta.gettSize(); t++) {
                    for (int z = 0; z < meta.getzSize(); z++) {
                        Array array = variable.read(new int[]{t, z, 0, 0}, new int[]{1, 1, meta.getxSize(), meta.getySize()});
                        Grid2D grid2D = new Grid2D(array.getDataAsByteBuffer(), variable.getDataType(),
                                new int[] {0, 0}, new int[] {meta.getxSize(), meta.getySize()});
                        writer.writeGrid2D(variable.getShortName(), t, z, grid2D);
                    }
                }
            }
        }
    }

    public void run() throws Exception {
        String dataSetId = ExampleConfig.EXAMPLE_GRID_DATA_SET_ID;
        String filePath = ExampleConfig.EXAMPLE_GRID_DATA_SET_NC_FILE_PATH;
        String variable = ExampleConfig.EXAMPLE_GRID_DATA_VARIABLE;
        int[] shape = ExampleConfig.EXAMPLE_GRID_DATA_SHAPE;
        DataType dataType = ExampleConfig.EXAMPLE_GRID_DATA_TYPE;

        GridDataSetMeta meta = initMeta(dataSetId, dataType, Arrays.asList(variable), shape);
        importFromNcFile(meta, filePath);
        updateMeta(meta);
    }

    public static void main(String[] args) throws Exception {
        DataImportExample example = new DataImportExample();
        try {
            example.run();
        } finally {
            example.close();
        }
    }

}
