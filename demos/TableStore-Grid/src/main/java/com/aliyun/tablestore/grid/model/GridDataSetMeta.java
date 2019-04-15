package com.aliyun.tablestore.grid.model;

import ucar.ma2.DataType;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GridDataSetMeta {

    private String gridDataSetId;
    private DataType dataType;
    private List<String> variables;
    private int tSize;
    private int zSize;
    private int xSize;
    private int ySize;
    private StoreOptions storeOptions;
    private Map<String, Object> attributes;

    public GridDataSetMeta(String gridDataSetId, DataType dataType, List<String> variables, int tSize, int zSize, int xSize, int ySize, StoreOptions storeOptions) {
        assert gridDataSetId != null;
        assert variables != null;
        assert storeOptions != null;

        this.gridDataSetId = gridDataSetId;
        this.dataType = dataType;
        this.variables = variables;
        this.tSize = tSize;
        this.zSize = zSize;
        this.xSize = xSize;
        this.ySize = ySize;
        this.storeOptions = storeOptions;
    }

    public String getGridDataSetId() {
        return gridDataSetId;
    }

    public void setGridDataSetId(String gridDataSetId) {
        this.gridDataSetId = gridDataSetId;
    }

    public List<String> getVariables() {
        return variables;
    }

    public void setVariables(List<String> variables) {
        this.variables = variables;
    }

    public int gettSize() {
        return tSize;
    }

    public void settSize(int tSize) {
        this.tSize = tSize;
    }

    public int getzSize() {
        return zSize;
    }

    public void setzSize(int zSize) {
        this.zSize = zSize;
    }

    public int getxSize() {
        return xSize;
    }

    public void setxSize(int xSize) {
        this.xSize = xSize;
    }

    public int getySize() {
        return ySize;
    }

    public void setySize(int ySize) {
        this.ySize = ySize;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        assert attributes != null;
        for (String key : attributes.keySet()) {
            if (key.startsWith("_")) {
                throw new IllegalArgumentException("attribute key can't start with \"_\"");
            }
        }
        this.attributes = attributes;
    }

    public void addAttribute(String key, Object value) {
        if (key.startsWith("_")) {
            throw new IllegalArgumentException("attribute key can't start with \"_\"");
        }
        if (this.attributes == null) {
            this.attributes = new ConcurrentHashMap<String, Object>();
        }
        this.attributes.put(key, value);
    }

    public StoreOptions getStoreOptions() {
        return storeOptions;
    }

    public DataType getDataType() {
        return dataType;
    }
}
