package com.aliyun.tablestore.basic.common;

import com.alicloud.openservices.tablestore.SyncClient;
import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.InputStream;

/**
 * @Author wtt
 * @create 2019/11/28 4:26 PM
 */
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

    public static Config newInstance() {
        String os = System.getProperty("os.name");
        String pathSeparator = "/";
        if (os.toLowerCase().startsWith("win")) {
            pathSeparator = "\\";
        }
        return newInstance(System.getProperty("user.home") + pathSeparator + "tablestoreConf.json");
    }

    public SyncClient newClient() {
        return  new SyncClient(
                getEndpoint(),
                getAccessId(),
                getAccessKey(),
                getInstanceName()
        );
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
