package com.aliyun.tablestore.grid.model;

import java.util.List;

public class GetDataParam {

    private String dataTableName;
    private String dataSetId;
    private String variable;
    private int t;
    private int z;
    private List<String> columnsToGet;

    public GetDataParam(String dataTableName, String dataSetId, String variable, int t, int z, List<String> columnsToGet) {
        this.dataTableName = dataTableName;
        this.dataSetId = dataSetId;
        this.variable = variable;
        this.t = t;
        this.z = z;
        this.columnsToGet = columnsToGet;
    }

    public String getDataTableName() {
        return dataTableName;
    }

    public void setDataTableName(String dataTableName) {
        this.dataTableName = dataTableName;
    }

    public String getDataSetId() {
        return dataSetId;
    }

    public void setDataSetId(String dataSetId) {
        this.dataSetId = dataSetId;
    }

    public String getVariable() {
        return variable;
    }

    public void setVariable(String variable) {
        this.variable = variable;
    }

    public int getT() {
        return t;
    }

    public void setT(int t) {
        this.t = t;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public List<String> getColumnsToGet() {
        return columnsToGet;
    }

    public void setColumnsToGet(List<String> columnsToGet) {
        this.columnsToGet = columnsToGet;
    }
}
