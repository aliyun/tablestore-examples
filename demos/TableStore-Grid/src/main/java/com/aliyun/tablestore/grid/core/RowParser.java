package com.aliyun.tablestore.grid.core;

import com.alicloud.openservices.tablestore.model.Column;
import com.alicloud.openservices.tablestore.model.Row;
import com.aliyun.tablestore.grid.model.GridDataSetMeta;
import com.aliyun.tablestore.grid.model.StoreOptions;
import com.aliyun.tablestore.grid.model.grid.Grid2D;
import com.aliyun.tablestore.grid.model.grid.Plane;
import com.aliyun.tablestore.grid.model.grid.Range;
import com.aliyun.tablestore.grid.utils.BlockUtil;
import com.aliyun.tablestore.grid.utils.ValueUtil;
import ucar.ma2.DataType;

import java.nio.ByteBuffer;
import java.util.*;

import static com.aliyun.tablestore.grid.consts.Constants.*;

public class RowParser {

    public static GridDataSetMeta parseMetaFromRow(Row row) {
        String uniqueKey = row.getPrimaryKey().getPrimaryKeyColumn(GRID_DATA_SET_ID_PK_NAME).getValue().asString();
        DataType dataType = DataType.getType(row.getColumn(DATA_TYPE_COL_NAME).get(0).getValue().asString());
        List<String> variables = Arrays.asList(row.getColumn(VARIABLE_LIST_COL_NAME).get(0).getValue().asString().split(","));
        int tSize = (int) row.getColumn(T_SIZE_COL_NAME).get(0).getValue().asLong();
        int zSize = (int) row.getColumn(Z_SIZE_COL_NAME).get(0).getValue().asLong();
        int xSize = (int) row.getColumn(X_SIZE_COL_NAME).get(0).getValue().asLong();
        int ySize = (int) row.getColumn(Y_SIZE_COL_NAME).get(0).getValue().asLong();

        StoreOptions.StoreType storeType = StoreOptions.StoreType.valueOf(
                row.getColumn(STORE_TYPE_COL_NAME).get(0).getValue().asString());
        StoreOptions storeOptions = new StoreOptions(storeType);
        if (storeType.equals(StoreOptions.StoreType.SLICE)) {
            storeOptions.setxSplitCount((int) row.getColumn(X_SPLIT_COUNT_COL_NAME).get(0).getValue().asLong());
            storeOptions.setySplitCount((int) row.getColumn(Y_SPLIT_COUNT_COL_NAME).get(0).getValue().asLong());
        }
        Map<String, Object> attributes = new HashMap<String, Object>();
        for (Column column : row.getColumns()) {
            if (!column.getName().startsWith("_")) {
                attributes.put(column.getName(), ValueUtil.toObject(column.getValue()));
            }
        }
        GridDataSetMeta meta = new GridDataSetMeta(uniqueKey, dataType, variables, tSize, zSize, xSize, ySize, storeOptions);
        meta.setAttributes(attributes);
        return meta;
    }

    public static Grid2D parseGridFromRow(Row row, Plane plane, GridDataSetMeta meta, byte[] buffer, int pos) {
        if (!meta.getStoreOptions().getStoreType().equals(StoreOptions.StoreType.SLICE)) {
            throw new IllegalArgumentException("unsupported store type");
        }
        int blockXSize = (meta.getxSize() - 1) / meta.getStoreOptions().getxSplitCount() + 1;
        int blockYSize = (meta.getySize() - 1) / meta.getStoreOptions().getySplitCount() + 1;
        List<Grid2D> blocks = new ArrayList<Grid2D>();
        for (Column column : row.getColumns()) {
            if (column.getName().startsWith(DATA_BLOCK_COL_NAME_PREFIX)) {
                String[] strs = column.getName().split("_");
                int xStart = Integer.valueOf(strs[strs.length - 2]);
                int yStart = Integer.valueOf(strs[strs.length - 1]);
                Range xRange = new Range(xStart, Math.min(xStart + blockXSize, meta.getxSize()));
                Range yRange = new Range(yStart, Math.min(yStart + blockYSize, meta.getySize()));
                int[] origin = new int[] {xStart, yStart};
                int[] shape = new int[] {xRange.getSize(), yRange.getSize()};
                Grid2D grid2D = new Grid2D(ByteBuffer.wrap(column.getValue().asBinary()), meta.getDataType(), origin, shape);
                blocks.add(grid2D);
            }
        }
        Grid2D grid2D = BlockUtil.buildGrid2DFromBlocks(plane, meta.getDataType(), blocks, buffer, pos);
        return grid2D;
    }

}
