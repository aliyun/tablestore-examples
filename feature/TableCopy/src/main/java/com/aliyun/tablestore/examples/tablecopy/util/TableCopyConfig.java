package com.aliyun.tablestore.examples.tablecopy.util;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TableCopyConfig {
    private static final Logger LOG = LoggerFactory.getLogger(TableCopyConfig.class);

    @SerializedName("ots-reader")
    private OtsReaderConfig readConf;

    @SerializedName("ots-writer")
    private OtsWriterConfig writeConf;

    public static TableCopyConfig loadConfig(String path) {
        try {
            Reader reader = new InputStreamReader((new FileInputStream(path)));
            TableCopyConfig config = new Gson().fromJson(reader, TableCopyConfig.class);
            System.out.println(config);
            LOG.debug("Config: ", config);
            return config;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("invalid config file");
        }
    }

    public OtsReaderConfig getReadConf() {
        return readConf;
    }

    public OtsWriterConfig getWriteConf() {
        return writeConf;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
