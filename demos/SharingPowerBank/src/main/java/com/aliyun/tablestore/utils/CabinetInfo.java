package com.aliyun.tablestore.utils;

/**
 * 品牌、型号、槽位、时价信息。用于模拟机柜元数据信息
 */
public class CabinetInfo {
    public String brand;
    public String ID;
    public long size;
    public double pricePerHour;

    public CabinetInfo(String brand, String ID, long size, double pricePerHour) {
        this.brand = brand;
        this.ID = ID;
        this.size = size;
        this.pricePerHour = pricePerHour;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public double getPricePerHour() {
        return pricePerHour;
    }

    public void setPricePerHour(double pricePerHour) {
        this.pricePerHour = pricePerHour;
    }
}