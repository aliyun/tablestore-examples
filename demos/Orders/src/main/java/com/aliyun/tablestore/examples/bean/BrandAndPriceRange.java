package com.aliyun.tablestore.examples.bean;

public class BrandAndPriceRange {

    private String brand;
    private Double high;
    private Double low;

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public Double getHigh() {
        return high;
    }

    public void setHigh(Double high) {
        this.high = high;
    }

    public Double getLow() {
        return low;
    }

    public void setLow(Double low) {
        this.low = low;
    }
}
