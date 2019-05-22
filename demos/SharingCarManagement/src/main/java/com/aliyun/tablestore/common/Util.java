package com.aliyun.tablestore.common;

import java.util.Random;

public class Util {
    public static String getMailRandomLocation() {
        return String.format("%f,%f", 30. + new Random().nextDouble() * 10, 120. + new Random().nextDouble() * 10);
    }
}
