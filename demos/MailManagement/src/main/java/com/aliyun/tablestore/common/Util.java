package com.aliyun.tablestore.common;

import java.util.Random;

public class Util {
    public static String getMailRandomLocation() {
        return String.format("%f,%f", 30. + new Random().nextDouble() * 10, 120. + new Random().nextDouble() * 10);
    }

    public static String formatMId(long index) {
        String oId = "" + index;
        StringBuilder oIdBuider = new StringBuilder("m");
        for (int i = 0; i < 10 - oId.length(); i ++) {
            oIdBuider.append("0");
        }
        oIdBuider.append(oId);
        return oIdBuider.toString();
    }
}
