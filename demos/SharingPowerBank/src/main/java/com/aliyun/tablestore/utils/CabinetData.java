package com.aliyun.tablestore.utils;


/**
 * 临时存储模拟数据，元数据
 */
public class CabinetData {
    public String cabinetMd5ID;
    public String cabinetID;
    public String cabinetGeo;
    public String cabinetLocation;
    public String cabinetProvince;
    public long cabinetAvailableSize;
    public long cabinetDamageSize;
    public long cabinetPowerbankSize;
    public String cabinetIsOnline;
    public double cabinetPowerPercent;
    public String cabinetType;
    public String cabinetManufacturers;
    public long cabinetOverhaulTime;
    public double cabinetPricePerHour;

    public CabinetData(){

    }

    public CabinetData(String cabinetMd5ID, String cabinetID, String cabinetGeo, String cabinetLocation, String cabinetProvince, long cabinetAvailableSize, long cabinetDamageSize, long cabinetPowerbankSize, String cabinetIsOnline, double cabinetPowerPercent, String cabinetType, String cabinetManufacturers, long cabinetOverhaulTime, double cabinetPricePerHour) {
        this.cabinetMd5ID = cabinetMd5ID;
        this.cabinetID = cabinetID;
        this.cabinetGeo = cabinetGeo;
        this.cabinetLocation = cabinetLocation;
        this.cabinetProvince = cabinetProvince;
        this.cabinetAvailableSize = cabinetAvailableSize;
        this.cabinetDamageSize = cabinetDamageSize;
        this.cabinetPowerbankSize = cabinetPowerbankSize;
        this.cabinetIsOnline = cabinetIsOnline;
        this.cabinetPowerPercent = cabinetPowerPercent;
        this.cabinetType = cabinetType;
        this.cabinetManufacturers = cabinetManufacturers;
        this.cabinetOverhaulTime = cabinetOverhaulTime;
        this.cabinetPricePerHour = cabinetPricePerHour;
    }

    public String getCabinetMd5ID() {
        return cabinetMd5ID;
    }

    public void setCabinetMd5ID(String cabinetMd5ID) {
        this.cabinetMd5ID = cabinetMd5ID;
    }

    public String getCabinetID() {
        return cabinetID;
    }

    public void setCabinetID(String cabinetID) {
        this.cabinetID = cabinetID;
    }

    public String getCabinetGeo() {
        return cabinetGeo;
    }

    public void setCabinetGeo(String cabinetGeo) {
        this.cabinetGeo = cabinetGeo;
    }

    public String getCabinetLocation() {
        return cabinetLocation;
    }

    public void setCabinetLocation(String cabinetLocation) {
        this.cabinetLocation = cabinetLocation;
    }

    public String getCabinetProvince() {
        return cabinetProvince;
    }

    public void setCabinetProvince(String cabinetProvince) {
        this.cabinetProvince = cabinetProvince;
    }

    public long getCabinetAvailableSize() {
        return cabinetAvailableSize;
    }

    public void setCabinetAvailableSize(long cabinetAvailableSize) {
        this.cabinetAvailableSize = cabinetAvailableSize;
    }

    public long getCabinetDamageSize() {
        return cabinetDamageSize;
    }

    public void setCabinetDamageSize(long cabinetDamageSize) {
        this.cabinetDamageSize = cabinetDamageSize;
    }

    public long getCabinetPowerbankSize() {
        return cabinetPowerbankSize;
    }

    public void setCabinetPowerbankSize(long cabinetPowerbankSize) {
        this.cabinetPowerbankSize = cabinetPowerbankSize;
    }

    public String getCabinetIsOnline() {
        return cabinetIsOnline;
    }

    public void setCabinetIsOnline(String cabinetIsOnline) {
        this.cabinetIsOnline = cabinetIsOnline;
    }

    public double getCabinetPowerPercent() {
        return cabinetPowerPercent;
    }

    public void setCabinetPowerPercent(double cabinetPowerPercent) {
        this.cabinetPowerPercent = cabinetPowerPercent;
    }

    public String getCabinetType() {
        return cabinetType;
    }

    public void setCabinetType(String cabinetType) {
        this.cabinetType = cabinetType;
    }

    public String getCabinetManufacturers() {
        return cabinetManufacturers;
    }

    public void setCabinetManufacturers(String cabinetManufacturers) {
        this.cabinetManufacturers = cabinetManufacturers;
    }

    public long getCabinetOverhaulTime() {
        return cabinetOverhaulTime;
    }

    public void setCabinetOverhaulTime(long cabinetOverhaulTime) {
        this.cabinetOverhaulTime = cabinetOverhaulTime;
    }

    public double getCabinetPricePerHour() {
        return cabinetPricePerHour;
    }

    public void setCabinetPricePerHour(double cabinetPricePerHour) {
        this.cabinetPricePerHour = cabinetPricePerHour;
    }
}
