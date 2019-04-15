package com.aliyun.tablestore.grid.core;

import com.alicloud.openservices.tablestore.TableStoreWriter;
import com.alicloud.openservices.tablestore.model.*;
import com.aliyun.tablestore.grid.GridDataWriter;
import com.aliyun.tablestore.grid.model.grid.Grid2D;
import com.aliyun.tablestore.grid.model.GridDataSetMeta;
import com.aliyun.tablestore.grid.model.StoreOptions;
import com.aliyun.tablestore.grid.model.grid.Plane;
import com.aliyun.tablestore.grid.utils.BlockUtil;
import ucar.ma2.InvalidRangeException;

import java.util.ArrayList;
import java.util.List;

import static com.aliyun.tablestore.grid.consts.Constants.*;

public class TableStoreDataWriter implements GridDataWriter {

    private String tableName;
    private GridDataSetMeta meta;
    private TableStoreWriter writer;

    public TableStoreDataWriter(TableStoreWriter writer, String tableName, GridDataSetMeta dataSetMeta) {
        this.writer = writer;
        this.tableName = tableName;
        this.meta = dataSetMeta;
    }

    private void checkDataSize(String variable, int t, int z, Grid2D grid2D) {
        if (!meta.getVariables().contains(variable)) {
            throw new IllegalArgumentException("The data set dose not include this variable: " + variable);
        }
        if (t >= meta.gettSize()) {
            throw new IllegalArgumentException("t must be in range: [0, " + meta.gettSize() + ")");
        }
        if (z >= meta.getzSize()) {
            throw new IllegalArgumentException("z must be in range: [0, " + meta.getzSize() + ")");
        }
        Plane plane = new Plane(grid2D.getOrigin(), grid2D.getShape());
        if (plane.getxRange().getStart() != 0 || plane.getyRange().getStart() != 0) {
            throw new IllegalArgumentException("xStart and yStart in grid2D must be 0");
        }
        if (plane.getxRange().getSize() != meta.getxSize()) {
            throw new IllegalArgumentException("xSize in grid2D must be equal to gridDataSetMeta's xSize");
        }
        if (plane.getyRange().getSize() != meta.getySize()) {
            throw new IllegalArgumentException("ySize in grid2D must be equal to gridDataSetMeta's ySize");
        }
    }

    private List<Column> splitDataToColumns(Grid2D grid2D) throws InvalidRangeException {
        List<Column> columns = new ArrayList<Column>();
        List<Grid2D> blocks = BlockUtil.splitGrid2DToBlocks(grid2D, meta.getStoreOptions().getxSplitCount(), meta.getStoreOptions().getySplitCount());
        for (Grid2D block : blocks) {
            columns.add(new Column(String.format(DATA_BLOCK_COL_NAME_FORMAT, block.getPlane().getxRange().getStart(),
                    block.getPlane().getyRange().getStart()), ColumnValue.fromBinary(block.getDataAsByteArray())));
        }
        return columns;
    }

    private void writeToTableStore(String variable, int t, int z, List<Column> columns) {
        if (columns.size() == 0) {
            throw new IllegalArgumentException("columns are empty");
        }
        PrimaryKeyBuilder builder = PrimaryKeyBuilder.createPrimaryKeyBuilder();
        builder.addPrimaryKeyColumn(GRID_DATA_SET_ID_PK_NAME, PrimaryKeyValue.fromString(meta.getGridDataSetId()));
        builder.addPrimaryKeyColumn(VARIABLE_PK_NAME, PrimaryKeyValue.fromString(variable));
        builder.addPrimaryKeyColumn(T_PK_NAME, PrimaryKeyValue.fromLong(t));
        builder.addPrimaryKeyColumn(Z_PK_NAME, PrimaryKeyValue.fromLong(z));
        PrimaryKey pk = builder.build();

        RowUpdateChange rowUpdateChange = new RowUpdateChange(tableName, pk);
        int currentSize = 0;
        for (int i = 0; i < columns.size(); i++) {
            if (currentSize != 0 && (currentSize + columns.get(i).getDataSize()) > MAX_REQUEST_SIZE) {
                writer.addRowChange(rowUpdateChange);
                rowUpdateChange = new RowUpdateChange(tableName, pk);
                currentSize = 0;
            }
            rowUpdateChange.put(columns.get(i));
            currentSize += columns.get(i).getDataSize();
        }
        writer.addRowChange(rowUpdateChange);
        writer.flush();
    }

    @Override
    public void writeGrid2D(String variable, int t, int z, Grid2D grid2D) throws Exception {
        checkDataSize(variable, t, z, grid2D);
        if (meta.getStoreOptions().getStoreType().equals(StoreOptions.StoreType.SLICE)) {
            List<Column> columns = splitDataToColumns(grid2D);
            writeToTableStore(variable, t, z, columns);
        } else {
            throw new IllegalArgumentException("unsupported store type");
        }
    }
}
