package com.aliyun.tablestore.model;

public class Car {
    private String carNo;
    private String company;
    private String province;
    private String type;
    private String status;
    private long seats;
    private double remain;
    private double expected;
    private String location;


    public String getCarNo() {
        return carNo;
    }

    public Car setCarNo(String carNo) {
        this.carNo = carNo;
        return this;
    }

    public String getCompany() {
        return company;
    }

    public Car setCompany(String company) {
        this.company = company;
        return this;
    }

    public String getProvince() {
        return province;
    }

    public Car setProvince(String province) {
        this.province = province;
        return this;
    }

    public String getType() {
        return type;
    }

    public Car setType(String type) {
        this.type = type;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public Car setStatus(String status) {
        this.status = status;
        return this;
    }

    public long getSeats() {
        return seats;
    }

    public Car setSeats(long seats) {
        this.seats = seats;
        return this;
    }

    public double getRemain() {
        return remain;
    }

    public Car setRemain(double remain) {
        this.remain = remain;
        return this;
    }

    public double getExpected() {
        return expected;
    }

    public Car setExpected(double expected) {
        this.expected = expected;
        return this;
    }

    public String getLocation() {
        return location;
    }

    public Car setLocation(String location) {
        this.location = location;
        return this;
    }

}
