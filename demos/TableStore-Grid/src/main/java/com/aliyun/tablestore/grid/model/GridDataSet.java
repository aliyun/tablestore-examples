package com.aliyun.tablestore.grid.model;

import com.aliyun.tablestore.grid.model.grid.Grid4D;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GridDataSet {

    private GridDataSetMeta meta;

    private Map<String, Grid4D> variables;

    public GridDataSet(GridDataSetMeta meta) {
        this.meta = meta;
        this.variables = new ConcurrentHashMap<String, Grid4D>();
    }

    public GridDataSet(GridDataSetMeta meta, Map<String, Grid4D> variables) {
        this.meta = meta;
        this.variables = variables;
    }

    public void addVariable(String variable, Grid4D grid4D) {
        this.variables.put(variable, grid4D);
    }

    public Grid4D getVariable(String variable) {
        if (variables == null) {
            return null;
        }
        return variables.get(variable);
    }

    public Map<String, Grid4D> getVariables() {
        return variables;
    }

    public GridDataSetMeta getMeta() {
        return meta;
    }
}
