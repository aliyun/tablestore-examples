package com.aliyun.tablestore.utils;


/**
 * 临时存储模拟数据，订单数据
 */
public class OrderData {

    public String orderMd5ID;
    public String orderID;
    public long orderStartTime;
    public long orderEndTime;
    public boolean orderIsRevert;
    public double orderLosePay;
    public String cabinetID;
    public String orderPhone;
    public double cabinetPricePerHour;
    public String cabinetType;
    public String cabinetGeo;
    public String cabinetProvince;

    public OrderData(String orderMd5ID, String orderID, long orderStartTime, long orderEndTime, boolean orderIsRevert, double orderLosePay, String cabinetID, String orderPhone, double cabinetPricePerHour, String cabinetType, String cabinetGeo, String cabinetProvince) {
        this.orderMd5ID = orderMd5ID;
        this.orderID = orderID;
        this.orderStartTime = orderStartTime;
        this.orderEndTime = orderEndTime;
        this.orderIsRevert = orderIsRevert;
        this.orderLosePay = orderLosePay;
        this.cabinetID = cabinetID;
        this.orderPhone = orderPhone;
        this.cabinetPricePerHour = cabinetPricePerHour;
        this.cabinetType = cabinetType;
        this.cabinetGeo = cabinetGeo;
        this.cabinetProvince = cabinetProvince;
    }

    public String getOrderMd5ID() {
        return orderMd5ID;
    }

    public void setOrderMd5ID(String orderMd5ID) {
        this.orderMd5ID = orderMd5ID;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public long getOrderStartTime() {
        return orderStartTime;
    }

    public void setOrderStartTime(long orderStartTime) {
        this.orderStartTime = orderStartTime;
    }

    public long getOrderEndTime() {
        return orderEndTime;
    }

    public void setOrderEndTime(long orderEndTime) {
        this.orderEndTime = orderEndTime;
    }

    public boolean isOrderIsRevert() {
        return orderIsRevert;
    }

    public void setOrderIsRevert(boolean orderIsRevert) {
        this.orderIsRevert = orderIsRevert;
    }

    public double getOrderLosePay() {
        return orderLosePay;
    }

    public void setOrderLosePay(double orderLosePay) {
        this.orderLosePay = orderLosePay;
    }

    public String getCabinetID() {
        return cabinetID;
    }

    public void setCabinetID(String cabinetID) {
        this.cabinetID = cabinetID;
    }

    public String getOrderPhone() {
        return orderPhone;
    }

    public void setOrderPhone(String orderPhone) {
        this.orderPhone = orderPhone;
    }

    public double getCabinetPricePerHour() {
        return cabinetPricePerHour;
    }

    public void setCabinetPricePerHour(double cabinetPricePerHour) {
        this.cabinetPricePerHour = cabinetPricePerHour;
    }

    public String getCabinetType() {
        return cabinetType;
    }

    public void setCabinetType(String cabinetType) {
        this.cabinetType = cabinetType;
    }

    public String getCabinetGeo() {
        return cabinetGeo;
    }

    public void setCabinetGeo(String cabinetGeo) {
        this.cabinetGeo = cabinetGeo;
    }

    public String getCabinetProvince() {
        return cabinetProvince;
    }

    public void setCabinetProvince(String cabinetProvince) {
        this.cabinetProvince = cabinetProvince;
    }
}
