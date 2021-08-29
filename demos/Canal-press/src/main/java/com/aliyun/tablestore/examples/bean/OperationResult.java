package com.aliyun.tablestore.examples.bean;

public class OperationResult {

    private Integer code;
    private Object data;

    public static OperationResult toResult(Object data) {
        OperationResult res = new OperationResult();
        res.setCode(1);
        res.setData(data);
        return res;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
