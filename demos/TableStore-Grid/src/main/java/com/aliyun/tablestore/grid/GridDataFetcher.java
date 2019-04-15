package com.aliyun.tablestore.grid;

import com.aliyun.tablestore.grid.model.GridDataSet;
import com.aliyun.tablestore.grid.model.grid.Range;

import java.util.Collection;

public interface GridDataFetcher {

    GridDataFetcher setVariablesToGet(Collection<String> variables);

    GridDataFetcher setT(int t);

    GridDataFetcher setTRange(Range tRange);

    GridDataFetcher setZ(int z);

    GridDataFetcher setZRange(Range zRange);

    GridDataFetcher setX(int x);

    GridDataFetcher setXRange(Range xRange);

    GridDataFetcher setY(int y);

    GridDataFetcher setYRange(Range yRange);

    GridDataFetcher setOriginShape(int[] origin, int[] shape);

    GridDataSet fetch() throws Exception;

}
