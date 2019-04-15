package com.aliyun.tablestore.common;

import java.util.Random;

public class Util {
    private static Random Rand = new Random(37);
    public static String getWifiRandomLocation() {
        return String.format("%f,%f", 30. + Rand.nextDouble() * 10, 120. + Rand.nextDouble() * 10);
    }
}
