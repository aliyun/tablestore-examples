package com.aliyun.tablestore.common;

import com.alicloud.openservices.tablestore.AsyncClient;
import com.alicloud.openservices.tablestore.SyncClient;

public class Config {
    /**
     * 数据表名
     */
    public static String CABINET_TABLENAME = "cabinet";//元数据表名
    public static String ORDER_TALENAME = "order";//订单数据表名
    public static String CABINET_TIME_TABLENAME = "cabinet_time";//元数据时序表名

    /**
     * 多远索引名
     */
    public static String CABINET_TABLENAME_INDEX = "cabinet_index";//元数据索引名


    /**
     * 数据规模配置  元数据表行数 = CABINET_NUM ， 元数据时序表行数 = CABINET_TIME_NUM * CABINET_TIME_POINTS ， 订单数据表行数 = CABINET_NUM * ORDER_PER_CABINET
     */
    public static long CABINET_NUM = 10000;//元数据表行数
    public static long CABINET_TIME_POINTS = 1000;//元数据时序表时间点个数
    public static long CABINET_TIME_NUM = 100;//跟踪的机柜数量
    public static long ORDER_PER_CABINET = 5;//每台机柜产生订单的个数


}
