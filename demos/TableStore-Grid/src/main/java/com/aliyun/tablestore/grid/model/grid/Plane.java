package com.aliyun.tablestore.grid.model.grid;

public class Plane {

    private Range xRange;
    private Range yRange;

    public Plane(Range xRange, Range yRange) {
        this.xRange = xRange;
        this.yRange = yRange;
    }

    public Plane(int[] origin, int[] shape) {
        if (origin.length != 2 || shape.length != 2) {
            throw new IllegalArgumentException("the length of origin and shape must be 2");
        }
        this.xRange = new Range(origin[0], origin[0] + shape[0]);
        this.yRange = new Range(origin[1], origin[1] + shape[1]);
    }

    public int[] getOrigin() {
        return new int[] {xRange.getStart(), yRange.getStart()};
    }

    public int[] getShape() {
        return new int[] {xRange.getSize(), yRange.getSize()};
    }

    public Range getxRange() {
        return xRange;
    }

    public void setxRange(Range xRange) {
        this.xRange = xRange;
    }

    public Range getyRange() {
        return yRange;
    }

    public void setyRange(Range yRange) {
        this.yRange = yRange;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof Plane) {
            if (xRange.equals(((Plane) o).getxRange()) && yRange.equals(((Plane) o).getyRange())) {
                return true;
            }
            return false;
        }
        return false;
    }
}
