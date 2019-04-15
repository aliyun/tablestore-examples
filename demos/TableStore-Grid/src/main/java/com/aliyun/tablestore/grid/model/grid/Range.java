package com.aliyun.tablestore.grid.model.grid;

public class Range {

    private int start;
    private int end;

    public Range(int end) {
        this.start = 0;
        this.end = end;
    }

    public Range(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public int getSize() {
        return this.end - this.start;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof Range) {
            if (start == ((Range) o).start && end == ((Range) o).end) {
                return true;
            }
            return false;
        }
        return false;
    }

    @Override
    public String toString() {
        return ("[" + start + ", " + end + ")");
    }
}
