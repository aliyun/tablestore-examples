package com.aliyun.tablestore.examples.tablecopy.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OtsReaderConfig extends OtsBaseConfig {
    private String tunnelName;
    private String endTime;

    public String getTunnelName() {
        return tunnelName;
    }

    public long getEndTime() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = df.parse(endTime);
            return date.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
