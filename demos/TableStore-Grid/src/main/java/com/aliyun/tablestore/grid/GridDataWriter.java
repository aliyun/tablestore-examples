package com.aliyun.tablestore.grid;

import com.aliyun.tablestore.grid.model.grid.Grid2D;

public interface GridDataWriter {

    void writeGrid2D(String variable, int t, int z, Grid2D grid2D) throws Exception;

}
