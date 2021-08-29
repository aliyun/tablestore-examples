package com.aliyun.tablestore.utils;

import java.util.ArrayList;

/**
 * 地理位置信息。省份，地理位置，城市列表。用于模拟机柜地理位置信息。
 */
public class CabinetGeo {

    private String province;
    private String geo;
    private ArrayList<String> citys;

    public CabinetGeo(String province,String geo,ArrayList<String> citys){
        this.province = province;
        this.geo = geo;
        this.citys = citys;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getGeo() {
        return geo;
    }

    public void setGeo(String geo) {
        this.geo = geo;
    }

    public ArrayList<String> getCitys() {
        return citys;
    }

    public void setCitys(ArrayList<String> citys) {
        this.citys = citys;
    }
}
