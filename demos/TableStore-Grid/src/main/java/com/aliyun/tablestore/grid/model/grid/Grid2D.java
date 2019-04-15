package com.aliyun.tablestore.grid.model.grid;

import ucar.ma2.DataType;

import java.nio.ByteBuffer;

public class Grid2D extends Grid {

    public Grid2D(ByteBuffer data, DataType dataType, int[] origin, int[] shape) {
        super(data, dataType, origin, shape);
        if (origin.length != 2 || shape.length != 2) {
            throw new IllegalArgumentException("the length of origin and shape must be 2");
        }
    }

    public Plane getPlane() {
        return new Plane(getOrigin(), getShape());
    }
}
