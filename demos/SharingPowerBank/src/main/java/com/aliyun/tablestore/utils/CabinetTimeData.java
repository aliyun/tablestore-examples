package com.aliyun.tablestore.utils;

/**
 * 临时存储模拟数据，元数据时序数据
 */
public class CabinetTimeData {

    public CabinetData cabinetData;
    public long cabinetStateTimestamp;

    public CabinetTimeData(CabinetData cabinetData, long cabinetStateTimestamp) {
        this.cabinetData = cabinetData;
        this.cabinetStateTimestamp = cabinetStateTimestamp;
    }


    public CabinetTimeData(){

    }

    public CabinetData getCabinetData() {
        return cabinetData;
    }

    public void setCabinetData(CabinetData cabinetData) {
        this.cabinetData = cabinetData;
    }

    public long getCabinetStateTimestamp() {
        return cabinetStateTimestamp;
    }

    public void setCabinetStateTimestamp(long cabinetStateTimestamp) {
        this.cabinetStateTimestamp = cabinetStateTimestamp;
    }
}
