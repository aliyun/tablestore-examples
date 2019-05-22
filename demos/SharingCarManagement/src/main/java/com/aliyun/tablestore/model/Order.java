package com.aliyun.tablestore.model;

import static org.apache.commons.codec.digest.DigestUtils.md5Hex;

public class Order {
    private String hash;
    private String orderId;
    private String carNo;
    private String mobile;
    private long from;
    private long to;
    private String userName;
    private String status;
    private double expense;
    private double total;

    public Order(String orderId) {
        this.orderId = orderId;
        this.hash = md5Hex(orderId).substring(0, 4);
    }

    public String getCarNo() {
        return carNo;
    }

    public Order setCarNo(String carNo) {
        this.carNo = carNo;
        return this;
    }

    public String getMobile() {
        return mobile;
    }

    public Order setMobile(String mobile) {
        this.mobile = mobile;
        return this;
    }

    public long getFrom() {
        return from;
    }

    public Order setFrom(long from) {
        this.from = from;
        return this;
    }

    public long getTo() {
        return to;
    }

    public Order setTo(long to) {
        this.to = to;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public Order setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public Order setStatus(String status) {
        this.status = status;
        return this;
    }

    public double getExpense() {
        return expense;
    }

    public Order setExpense(double expense) {
        this.expense = expense;
        return this;
    }

    public String getHash() {
        return hash;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}