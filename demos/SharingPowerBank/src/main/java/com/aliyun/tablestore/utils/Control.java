package com.aliyun.tablestore.utils;

import com.aliyun.tablestore.common.Config;

public class Control {
    public static boolean checkParam(){
        if(Config.CABINET_NUM<0||Config.CABINET_TIME_NUM<0||Config.ORDER_PER_CABINET<0||Config.CABINET_TIME_POINTS<0){
            System.err.println("[err msg]:cabinet_num,cabinet_time_num,order_per_cabinet,cabinet_time_points must greater than 0!");
            return true;
        }
        if(Config.CABINET_NUM < Config.CABINET_TIME_NUM * 2){
            System.err.println("[err msg]:cabinet_num must greater or equal double cabinet_time_num");
            return true;
        }
        return false;
    }

    public static boolean needWriteCabinetTime(int cabinet_size) {
        if(cabinet_size%((Config.CABINET_NUM - (Config.CABINET_NUM%Config.CABINET_TIME_NUM))/Config.CABINET_TIME_NUM) == 0){
            return true;
        }
        return false;
    }
}
