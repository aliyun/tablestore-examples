package com.aliyun.tablestore.utils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class RandomMake {
    /**
     * 生成订单表ID
     * @param type
     * @param phone
     * @return
     */
    public static String getOrderID(String type,String phone){
        return type + phone.substring(3,6) + System.currentTimeMillis();
    }

    /**
     * 随机生成开始时间
     * @return
     */
    public static long getBeginTimesmp(){
        Random t = new Random();
        long cutTime = 6000L * (long)(t.nextInt(525600)) + 31536000000L;
        long time = System.currentTimeMillis() - cutTime;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date date = new Date(time);
        return time;
    }

    /**
     * 随机生成归还时间
     * @param beginTimestamp
     * @return
     */
    public static long getEndTimesmp(long beginTimestamp){

        Random m = new Random();
        if(m.nextInt(100000)>30){
            Random t = new Random();
            int h = 1 + t.nextInt(71);
            return beginTimestamp + h*3600*1000;
        }else{
            return -1;
        }


    }

    /**
     * 随机生成违约金
     * @return
     */
    public static double getLosePay(){
        Random r = new Random();
        return r.nextInt(100);
    }

    /**
     * 判断是否归还
     * @param endTimestamp
     * @return
     */
    public static boolean isRevert(long endTimestamp){
        if(endTimestamp==-1){
            return false;
        }else{
            return true;
        }
    }

    /**
     * 随机生成用户手机号
     * @return
     */
    public static String getPhone(){
        Random r1 = new Random();
        StringBuilder stringBuilder = new StringBuilder("151");
        for(int i = 0 ; i < 8 ; i++){
            stringBuilder.append(r1.nextInt(8));
        }
        return stringBuilder.toString();
    }

    /**
     * 根据中心点随机产生一个经纬度
     * @param geo
     * @return
     */
    public static String getGeo(String geo){
        double lng = Double.parseDouble(geo.split(",")[0]);
        double lat = Double.parseDouble(geo.split(",")[1]);
        DecimalFormat df = new DecimalFormat( "0.00000" );
        double lngR = Double.valueOf(df.format( Math.random()*100000/100000));
        double latR = Double.valueOf(df.format( Math.random()*100000/100000));
        double lngN = lng - lngR;
        double latN = lat - latR;
        return  latN+ "," + lngN;
    }

    /**
     * 随机获取地址位置
     * @param cabinetGeos
     * @return
     */
    public static String getPosition(CabinetGeo cabinetGeos){
        String road = StaticData.cabinetRoad.get(getRandom(StaticData.cabinetRoad.size())) + getRandom(2000) + "号";

        String position;
        int citysize = cabinetGeos.getCitys().size();
        if(citysize==0){
            position = cabinetGeos.getProvince()+road;
        }else{
            position = cabinetGeos.getProvince()+cabinetGeos.getCitys().get(getRandom(citysize))+road;
        }
        return position;
    }

    /**
     * 产生一个arrayList范围内的下标
     * @param  arrayListSize
     * @return
     */
    public static int getRandom(int arrayListSize){
        Random r = new Random();
        return r.nextInt(arrayListSize);
    }

    public static String getOnlineStatus(){
        Random r = new Random();
        if(r.nextInt(10000)>3){
            return "online";
        }else{
            return "offline";
        }
    }

    /**
     * 随机产生 0% - 100% 机柜电量信息
     * @return
     */
    public static int getPowerPercent(){
        Random r = new Random();
        return r.nextInt(100);
    }

    /**
     * 随机产生一个检修时间戳
     * @return
     */
    public static long getOverHaul(){
        Random r = new Random();
        /**
         * 万分之三的检修时间戳在近一年以外
         */
        if(r.nextInt(100000)>30){
            Random t = new Random();
            long cutTime = 6000L * (long)(t.nextInt(525600));
            long time = System.currentTimeMillis() - cutTime;

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date date = new Date(time);
            return time;
        }else{

            Random t = new Random();
            long cutTime = 6000L * (long)(t.nextInt(525600)) + 31536000000L;
            long time = System.currentTimeMillis() - cutTime;

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date date = new Date(time);
            return time;
        }


    }
}
