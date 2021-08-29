package com.aliyun.tablestore.utils;


import com.aliyun.tablestore.common.Config;

import java.util.ArrayList;

/**
 * 数据生成类
 */
public class CreateData {

    /**
     * 随机产生一条机柜元数据
     * @return
     */
    public static CabinetData makeCabinetData(){
        CabinetGeo cabinetGeos = StaticData.cabinetGeos.get(RandomMake.getRandom(StaticData.cabinetGeos.size()));
        CabinetInfo cabinetInfo = StaticData.cabinetInfos.get(RandomMake.getRandom(StaticData.cabinetInfos.size()));
        String geo = RandomMake.getGeo(cabinetGeos.getGeo());//机柜经纬度
        String location = RandomMake.getPosition(cabinetGeos);//机柜地理位置
        String province = cabinetGeos.getProvince();//机柜所在省份
        long availableSize = RandomMake.getRandom((int)cabinetInfo.getSize());//随机产生可用充电宝数量
        long damageSize = RandomMake.getRandom((int)(cabinetInfo.getSize()-availableSize));//随机产生损坏充电宝数量
        long powerBankSize = cabinetInfo.getSize();//机柜槽位数量
        String isonline = RandomMake.getOnlineStatus();//随机产生机柜上线状态
        int powerPercent = RandomMake.getPowerPercent();//随机产生机柜电量百分比
        String type = cabinetInfo.getID();//获取机柜型号
        String manufacturers = cabinetInfo.getBrand();//获取机柜生产厂商
        long overHaul = RandomMake.getOverHaul();//随机产生机柜检修时间戳
        double price = cabinetInfo.getPricePerHour();//获取机柜充电宝租赁时价
        String Md5ID = Md5Utils.stringToMD5(location + type + System.currentTimeMillis());//生成Md5ID
        String ID = cabinetInfo.getID()+System.currentTimeMillis();//生成机柜ID
        return new CabinetData(Md5ID,ID,geo,location,province,availableSize,damageSize,powerBankSize,isonline,powerPercent,type,manufacturers,overHaul,price);
    }

    /**
     * 随机产生 Config.CABINET_TIME_POINTS 个时序点
     * @param cabinetData
     * @return
     */
    public static ArrayList<CabinetTimeData> makeCabinetTimeData(CabinetData cabinetData){
        CabinetInfo cabinetInfo = StaticData.cabinetInfos.get(RandomMake.getRandom(StaticData.cabinetInfos.size()));
        ArrayList<CabinetTimeData> arrayList = new ArrayList<>();
        for(long t = 1609430400000L, i = 1; i <= Config.CABINET_TIME_POINTS ; i++,t+=600000){
            CabinetTimeData cabinetTimeData = new CabinetTimeData();
            CabinetData cabinetData1 = new CabinetData();
            cabinetData1.setCabinetMd5ID(cabinetData.getCabinetMd5ID());
            cabinetData1.setCabinetID(cabinetData.getCabinetID());
            cabinetTimeData.setCabinetStateTimestamp(t);
            cabinetData1.setCabinetGeo(cabinetData.getCabinetGeo());
            cabinetData1.setCabinetLocation(cabinetData.getCabinetLocation());
            cabinetData1.setCabinetProvince(cabinetData.getCabinetProvince());
            cabinetData1.setCabinetAvailableSize(RandomMake.getRandom((int)cabinetInfo.getSize()));
            cabinetData1.setCabinetDamageSize(cabinetData.getCabinetDamageSize());
            cabinetData1.setCabinetPowerbankSize(cabinetData.getCabinetPowerbankSize());
            cabinetData1.setCabinetIsOnline(cabinetData.getCabinetIsOnline());
            cabinetData1.setCabinetPowerPercent(RandomMake.getPowerPercent());
            cabinetData1.setCabinetType(cabinetData.getCabinetType());
            cabinetData1.setCabinetManufacturers(cabinetData.getCabinetManufacturers());
            cabinetData1.setCabinetOverhaulTime(cabinetData.getCabinetOverhaulTime());
            cabinetData1.setCabinetPricePerHour(cabinetData.getCabinetPricePerHour());
            cabinetTimeData.setCabinetData(cabinetData1);

            arrayList.add(cabinetTimeData);
        }
        return arrayList;
    }

    /**
     * 随机产生 Config.ORDER_PER_CABINET 条订单
     * @param cabinetData
     * @return
     */
    public static ArrayList<OrderData> makeOrderData(CabinetData cabinetData){
        ArrayList<OrderData> arrayList = new ArrayList<>();
        for(int i = 1 ; i <= Config.ORDER_PER_CABINET ; i++){
            String cabinetID = cabinetData.getCabinetID();
            double price = cabinetData.getCabinetPricePerHour();
            String type = cabinetData.getCabinetType();
            String geo = cabinetData.getCabinetGeo();
            String province = cabinetData.getCabinetProvince();
            long beginTimeStamp = RandomMake.getBeginTimesmp();
            long endTimeStamp = RandomMake.getEndTimesmp(beginTimeStamp);
            boolean isRevert = RandomMake.isRevert(endTimeStamp);
            double losePay = RandomMake.getLosePay();
            String phone = RandomMake.getPhone();
            String orderID = RandomMake.getOrderID(type,phone);
            String Md5ID = Md5Utils.stringToMD5(orderID);//生成Md5ID
            OrderData orderData = new OrderData(Md5ID,orderID,beginTimeStamp,endTimeStamp,isRevert,losePay,cabinetID,phone,price,type,geo,province);
            arrayList.add(orderData);
        }
        return arrayList;
    }
}
