package com.aliyun.tablestore.grid.core;

import com.alicloud.openservices.tablestore.AsyncClientInterface;
import com.alicloud.openservices.tablestore.TableStoreCallback;
import com.alicloud.openservices.tablestore.model.GetRowRequest;
import com.alicloud.openservices.tablestore.model.GetRowResponse;
import com.aliyun.tablestore.grid.GridDataFetcher;
import com.aliyun.tablestore.grid.model.GetDataParam;
import com.aliyun.tablestore.grid.model.GridDataSet;
import com.aliyun.tablestore.grid.model.GridDataSetMeta;
import com.aliyun.tablestore.grid.model.StoreOptions;
import com.aliyun.tablestore.grid.model.grid.*;
import com.aliyun.tablestore.grid.utils.BlockUtil;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static com.aliyun.tablestore.grid.consts.Constants.DATA_BLOCK_COL_NAME_FORMAT;

/**
 * not thread-safe
 */
public class TableStoreDataFetcher implements GridDataFetcher {

    private AsyncClientInterface asyncClient;
    private GridDataSetMeta meta;
    private String tableName;
    private long dataSizeLimitForFetch;
    private Collection<String> variables;
    private Range tRange;
    private Range zRange;
    private Range xRange;
    private Range yRange;

    public TableStoreDataFetcher(AsyncClientInterface asyncClient, String tableName, GridDataSetMeta meta, long dataSizeLimitForFetch) {
        this.asyncClient = asyncClient;
        this.tableName = tableName;
        this.meta = meta;
        this.dataSizeLimitForFetch = dataSizeLimitForFetch;
        this.variables = this.meta.getVariables();
        this.tRange = new Range(0, this.meta.gettSize());
        this.zRange = new Range(0, this.meta.getzSize());
        this.xRange = new Range(0, this.meta.getxSize());
        this.yRange = new Range(0, this.meta.getySize());
    }

    public GridDataFetcher setVariablesToGet(Collection<String> variables) {
        this.variables = variables;
        return this;
    }

    @Override
    public GridDataFetcher setT(int t) {
        return setTRange(new Range(t, t+1));
    }

    @Override
    public GridDataFetcher setTRange(Range range) {
        if (range.getStart() < 0 || range.getEnd() > meta.gettSize()) {
            throw new IllegalArgumentException("range invalid");
        }
        this.tRange = range;
        return this;
    }

    @Override
    public GridDataFetcher setZ(int z) {
        return setZRange(new Range(z, z+1));
    }

    @Override
    public GridDataFetcher setZRange(Range range) {
        if (range.getStart() < 0 || range.getEnd() > meta.getzSize()) {
            throw new IllegalArgumentException("range invalid");
        }
        this.zRange = range;
        return this;
    }

    @Override
    public GridDataFetcher setX(int x) {
        return setXRange(new Range(x, x+1));
    }

    @Override
    public GridDataFetcher setXRange(Range range) {
        if (range.getStart() < 0 || range.getEnd() > meta.getxSize()) {
            throw new IllegalArgumentException("range invalid");
        }
        this.xRange = range;
        return this;
    }

    @Override
    public GridDataFetcher setY(int y) {
        return setYRange(new Range(y, y+1));
    }

    @Override
    public GridDataFetcher setYRange(Range range) {
        if (range.getStart() < 0 || range.getEnd() > meta.getySize()) {
            throw new IllegalArgumentException("range invalid");
        }
        this.yRange = range;
        return this;
    }

    @Override
    public GridDataFetcher setOriginShape(int[] origin, int[] shape) {
        if (origin.length != 4 || shape.length != 4) {
            throw new IllegalArgumentException("the length of origin and shape must be 4");
        }
        setTRange(new Range(origin[0], origin[0] + shape[0]));
        setZRange(new Range(origin[1], origin[1] + shape[1]));
        setXRange(new Range(origin[2], origin[2] + shape[2]));
        setYRange(new Range(origin[3], origin[3] + shape[3]));
        return this;
    }

    public int[] getOrigin() {
        return new int[] {tRange.getStart(), zRange.getStart(), xRange.getStart(), yRange.getStart()};
    }

    public int[] getShape() {
        return new int[] {tRange.getSize(), zRange.getSize(), xRange.getSize(), yRange.getSize()};
    }

    private long calcDataSize(int variableCount) {
        long dataSize = variableCount;
        return dataSize * meta.getDataType().getSize() * tRange.getSize() * zRange.getSize() * xRange.getSize() * yRange.getSize();
    }

    private List<String> getColumnsToGet() {
        if (!meta.getStoreOptions().getStoreType().equals(StoreOptions.StoreType.SLICE)) {
            throw new IllegalArgumentException("unsupported store type");
        }
        Plane plane = new Plane(new Range(meta.getxSize()), new Range(meta.getySize()));
        Plane subPlane = new Plane(xRange, yRange);
        if (plane.equals(subPlane)) {
            return null;
        }
        List<String> columnsToGet = new ArrayList<String>();
        List<Point> points = BlockUtil.calcBlockPointsCanCoverSubPlane(plane, subPlane,
                meta.getStoreOptions().getxSplitCount(), meta.getStoreOptions().getySplitCount());
        for (Point point : points) {
            columnsToGet.add(String.format(DATA_BLOCK_COL_NAME_FORMAT, point.getX(), point.getY()));
        }
        return columnsToGet;
    }

    private void addTask(final AtomicInteger counter, final byte[] buffer, final int pos, String variable, int t, int z, final CountDownLatch latch, final Queue<Exception> exceptions) {
        GetDataParam param = new GetDataParam(tableName, meta.getGridDataSetId(), variable, t, z, getColumnsToGet());
        asyncClient.getRow(RequestBuilder.buildGetDataRequest(param), new TableStoreCallback<GetRowRequest, GetRowResponse>() {
            @Override
            public void onCompleted(GetRowRequest req, GetRowResponse res) {
                try {
                    if (res.getRow() == null) {
                        exceptions.add(new RuntimeException("the row in not exist, pk: " + req.getRowQueryCriteria().getPrimaryKey()));
                    }
                    RowParser.parseGridFromRow(res.getRow(), new Plane(xRange, yRange), meta, buffer, pos);
                    counter.incrementAndGet();
                } catch (Exception ex) {
                    exceptions.add(ex);
                } finally {
                    latch.countDown();
                }
            }

            @Override
            public void onFailed(GetRowRequest req, Exception ex) {
                try {
                    exceptions.add(ex);
                } finally {
                    latch.countDown();
                }
            }
        });
    }

    public GridDataSet fetch() throws Exception {
        long totalFetchDataSize = calcDataSize(variables.size());
        if (totalFetchDataSize == 0) {
            throw new RuntimeException("no data to fetch");
        }
        if (totalFetchDataSize > dataSizeLimitForFetch) {
            throw new RuntimeException("exceed the max data limit for fetch");
        }
        GridDataSet dataSet = new GridDataSet(meta);
        CountDownLatch latch = new CountDownLatch(variables.size() * tRange.getSize() * zRange.getSize());
        Queue<Exception> exceptions = new ConcurrentLinkedQueue<Exception>();
        AtomicInteger counter = new AtomicInteger();
        int taskCount = 0;
        for (String variable : variables) {
            int dataSize = (int) calcDataSize(1);
            byte[] data = new byte[dataSize];
            ByteBuffer buffer = ByteBuffer.wrap(data).asReadOnlyBuffer();
            dataSet.addVariable(variable, new Grid4D(buffer, meta.getDataType(), getOrigin(), getShape()));
            int curPos = 0;
            for (int t = tRange.getStart(); t < tRange.getEnd(); t++) {
                for (int z = zRange.getStart(); z < zRange.getEnd(); z++) {
                    addTask(counter, data, curPos, variable, t, z, latch, exceptions);
                    curPos += xRange.getSize() * yRange.getSize() * meta.getDataType().getSize();
                    taskCount++;
                }
            }
        }
        latch.await();
        if (!exceptions.isEmpty()) {
            throw exceptions.peek();
        }
        if (counter.get() != taskCount) {
            throw new RuntimeException("not all task success");
        }
        return dataSet;
    }
}
