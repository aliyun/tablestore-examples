package com.aliyun.tablestore.examples.bean;

public class ConsumerTradeValue {

    private String cId;
    private Double tradeValue;

    public ConsumerTradeValue(String cId, Double tradeValue) {
        this.cId = cId;
        this.tradeValue = tradeValue;
    }

    public String getcId() {
        return cId;
    }

    public void setcId(String cId) {
        this.cId = cId;
    }

    public Double getTradeValue() {
        return tradeValue;
    }

    public void setTradeValue(Double tradeValue) {
        this.tradeValue = tradeValue;
    }
}
