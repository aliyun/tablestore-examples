package com.aliyun.tablestore.example.trace_medicine.common;

import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;

public class Conf {
    private String endpoint;
    private String accessId;
    private String accessKey;
    private String instanceName;
    private String metaTableName = "medicine_meta";
    private String dataTableName = "medicine_data";

    private String metaFile = "src/main/resources/data/medicine_meta.csv";
    private String dataFile = "src/main/resources/data/medicine_data.csv";

    private Conf() {}

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

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public String getMetaTableName() {
        return metaTableName;
    }

    public void setMetaTableName(String metaTableName) {
        this.metaTableName = metaTableName;
    }

    public String getDataTableName() {
        return dataTableName;
    }

    public void setDataTableName(String dataTableName) {
        this.dataTableName = dataTableName;
    }

    public String getMetaFile() {
        return metaFile;
    }

    public void setMetaFile(String metaFile) {
        this.metaFile = metaFile;
    }

    public String getDataFile() {
        return dataFile;
    }

    public void setDataFile(String dataFile) {
        this.dataFile = dataFile;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
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
