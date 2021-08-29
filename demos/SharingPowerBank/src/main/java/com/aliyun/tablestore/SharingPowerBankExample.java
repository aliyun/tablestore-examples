package com.aliyun.tablestore;

import com.aliyun.tablestore.common.Config;
import com.aliyun.tablestore.utils.*;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

public class SharingPowerBankExample {
    public static void main(String[] args) {
        //Config参数校验
        if(Control.checkParam()){
            return;
        }

        StaticData.init();

        ApiService apiService = new ApiService();
        apiService.clientInit();

        //创建数据表
        apiService.prepareTables();//数据表创建不计费，读写数据有一定免费额度
        apiService.createSearchIndex();//多元索引创建后即会开始计费

        apiService.writerInit();

        int cabinetProcess = 0 , cabinetTimeProcess = 0 , orderProcess = 0;

        for(int i = 1; i <= Config.CABINET_NUM; i++){
            CabinetData cabinetData = CreateData.makeCabinetData();
            apiService.cabinetAdd(cabinetData);
            cabinetProcess += 1;
            if(Control.needWriteCabinetTime(i)) {
                ArrayList<CabinetTimeData> cabinetTimeDatas = CreateData.makeCabinetTimeData(cabinetData);
                for(CabinetTimeData cabinetTimeData : cabinetTimeDatas){
                    apiService.cabinetTimeAdd(cabinetTimeData);
                }
                cabinetTimeProcess += cabinetTimeDatas.size();
            }
            ArrayList<OrderData> orderDatas = CreateData.makeOrderData(cabinetData);
            for (OrderData orderData : orderDatas){
                apiService.orderAdd(orderData);
            }
            orderProcess += orderDatas.size();

            //打印部分进度log
            if(i == Config.CABINET_NUM/100 || i == Config.CABINET_NUM){
                System.out.println(Thread.currentThread()+"[schedule] cabinet write rows: " + cabinetProcess);
                System.out.println(Thread.currentThread()+"[schedule] cabinet_time table write rows: " + cabinetTimeProcess);
                System.out.println(Thread.currentThread()+"[schedule] order table write rows: " + orderProcess);
            }
        }
        apiService.shutdown();
    }
}
