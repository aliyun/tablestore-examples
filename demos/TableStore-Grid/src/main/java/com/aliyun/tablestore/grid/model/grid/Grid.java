package com.aliyun.tablestore.grid.model.grid;

import ucar.ma2.Array;
import ucar.ma2.DataType;

import java.nio.ByteBuffer;

public abstract class Grid {

    protected ByteBuffer buffer;
    protected DataType dataType;
    protected int[] origin;
    protected int[] shape;

    public Grid(ByteBuffer buffer, DataType dataType, int[] origin, int[] shape) {
        this.buffer = buffer;
        this.dataType = dataType;
        this.origin = origin;
        this.shape = shape;
        int size = dataType.getSize();
        for (int i = 0; i < shape.length; i++) {
            size *= shape[i];
        }
        if (buffer.remaining() != size) {
            throw new IllegalArgumentException("data length and shape mismatch");
        }
        if (origin.length != shape.length) {
            throw new IllegalArgumentException("the length of origin and shape mismatch");
        }
    }

    public int getDataSize() {
        return buffer.remaining();
    }

    public byte[] getDataAsByteArray() {
        byte[] data = new byte[getDataSize()];
        buffer.duplicate().get(data);
        return data;
    }

    public int[] getOrigin() {
        return origin;
    }

    public int[] getShape() {
        return shape;
    }

    public DataType getDataType() {
        return dataType;
    }

    public Array toArray() {
        Array array = Array.factory(dataType, shape, buffer.duplicate());
        return array;
    }
}
