package com.aliyun.tablestore.examples.bean.Automobile;


public class AutomobileBean {
    private String carId;
    private String carMd5;
    private long carTimestamp;
    private double mileage;
    private double oil;
    private double temperatureIn;
    private double temperatureOut;
    private double velocity;
    private double tyrePressure;
    private double longitude;
    private double latitude;
    private int timeMinute;

    private String location;
    private long time;


    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }

    public String getCarMd5() {
        return carMd5;
    }

    public void setCarMd5(String carMd5) {
        this.carMd5 = carMd5;
    }

    public long getCarTimestamp() {
        return carTimestamp;
    }

    public void setCarTimestamp(long carTimestamp) {
        this.carTimestamp = carTimestamp;
    }

    public double getMileage() {
        return mileage;
    }

    public void setMileage(double mileage) {
        this.mileage = mileage;
    }

    public double getOil() {
        return oil;
    }

    public void setOil(double oil) {
        this.oil = oil;
    }

    public double getTemperatureIn() {
        return temperatureIn;
    }

    public void setTemperatureIn(double temperatureIn) {
        this.temperatureIn = temperatureIn;
    }

    public double getTemperatureOut() {
        return temperatureOut;
    }

    public void setTemperatureOut(double temperatureOut) {
        this.temperatureOut = temperatureOut;
    }

    public double getVelocity() {
        return velocity;
    }

    public void setVelocity(double velocity) {
        this.velocity = velocity;
    }

    public double getTyrePressure() {
        return tyrePressure;
    }

    public void setTyrePressure(double tyrePressure) {
        this.tyrePressure = tyrePressure;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public int getTimeMinute() {
        return timeMinute;
    }

    public void setTimeMinute(int timeMinute) {
        this.timeMinute = timeMinute;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }


    public void buildDBInfo() {
        location = latitude + "," + longitude;
        time = (int)Math.ceil(timeMinute / 60.0);
    }
}
