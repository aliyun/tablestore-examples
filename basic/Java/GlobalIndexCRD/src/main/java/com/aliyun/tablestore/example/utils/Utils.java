package com.aliyun.tablestore.example.utils;

import com.alicloud.openservices.tablestore.SyncClient;
import com.alicloud.openservices.tablestore.core.utils.IOUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class Utils {

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static ClientAndConfig getClientAndConfig(String[] args) {
        final String pathSeparator;
        String os = System.getProperty("os.name");
        if (os.toLowerCase().startsWith("win")) {
            pathSeparator = "\\";
        } else {
            pathSeparator = "/";
        }

        final InputStream inputStream;
        try {
            inputStream = new FileInputStream(System.getProperty("user.home") + pathSeparator + "tablestoreConf.json");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        // parse config in config file
        JsonNode config;
        try {
            config = OBJECT_MAPPER.readTree(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.safeClose(inputStream);
        }

        SyncClient syncClient = new SyncClient(
                config.get("endpoint").asText(),
                config.get("accessId").asText(),
                config.get("accessKey").asText(),
                config.get("instanceName").asText()
        );
        return new ClientAndConfig(
                config.path("table").asText("call"),
                config.path("index").asText("call_index"),
                syncClient);

    }
}
