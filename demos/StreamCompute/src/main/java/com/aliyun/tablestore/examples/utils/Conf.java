package com.aliyun.tablestore.examples.utils;

import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;

public class Conf {
    private String endpoint;
    private String accessId;
    private String accessKey;
    private String instanceName;
    private String sourceTableName = "source_order";
    private String sinkTableName = "sink_order";

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getAccessId() {
        return accessId;
    }

    public void setAccessId(String accessId) {
        this.accessId = accessId;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public String getSourceTableName() {
        return sourceTableName;
    }

    public void setSourceTableName(String sourceTableName) {
        this.sourceTableName = sourceTableName;
    }

    public String getSinkTableName() {
        return sinkTableName;
    }

    public void setSinkTableName(String sinkTableName) {
        this.sinkTableName = sinkTableName;
    }

    public static Conf newInstance() throws FileNotFoundException {
        String pathSeperator = "/";
        String os = System.getProperty("os.name");
        if (os.toLowerCase().startsWith("win")) {
            pathSeperator = "\\";
        }

        Reader r = new FileReader(System.getProperty("user.home") + pathSeperator + "tablestoreConf.json");
        Gson gson = new Gson();
        Conf c = gson.fromJson(r, Conf.class);
        return c;
    }
}
