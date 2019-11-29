package com.aliyun.tablestore.basic.common;

import com.alicloud.openservices.tablestore.model.PrimaryKeyType;

/**
 * @Author wtt
 * @create 2019/11/28 4:30 PM
 */
public class Consts {
    public static String TABLE_NAME = "DataManageTable";
    public static String TABLE_NAME_INC = "DataManageTableInc";
    public static int TTL = -1;
    public static int MAX_VERSION = 10;

    public static String PK1 = "pk1";
    public static PrimaryKeyType PK1_TYPE = PrimaryKeyType.INTEGER;
    public static String PK2 = "pk2";
    public static PrimaryKeyType PK2_TYPE = PrimaryKeyType.STRING;
    public static String PK3 = "pk3";
    public static PrimaryKeyType PK3_TYPE = PrimaryKeyType.INTEGER;
}
