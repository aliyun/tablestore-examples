package com.aliyun.tablestore.chart.common;

import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.InputStream;

public class Config {

    private String endpoint;
    private String accessId;
    private String accessKey;
    private String instanceName;

    public static Config newInstance(String path) {
        try {
            InputStream f = new FileInputStream(path);
            Gson gson = new Gson();
            return gson.fromJson(IOUtils.toString(f), Config.class);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

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
}
