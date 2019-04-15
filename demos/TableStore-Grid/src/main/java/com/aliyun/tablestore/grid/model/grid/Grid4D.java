package com.aliyun.tablestore.grid.model.grid;

import ucar.ma2.DataType;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class Grid4D extends Grid {

    public Grid4D(ByteBuffer data, DataType dataType, int[] origin, int[] shape) {
        super(data, dataType, origin, shape);
        if (origin.length != 4 || shape.length != 4) {
            throw new IllegalArgumentException("the length of origin and shape must be 2");
        }
    }

    public Grid3D getGrid3D(int idx) {
        if (idx < 0 || idx >= shape[0]) {
            throw new IllegalArgumentException("index out of range");
        }
        int itemSize = shape[1] * shape[2] * shape[3] * dataType.getSize();
        int pos = idx * itemSize;
        ByteBuffer newBuffer = buffer.slice();
        newBuffer.position(pos);
        newBuffer.limit(pos + itemSize);
        newBuffer = newBuffer.slice();
        return new Grid3D(newBuffer, dataType, Arrays.copyOfRange(origin, 1, origin.length),
                Arrays.copyOfRange(shape, 1, shape.length));
    }
}
