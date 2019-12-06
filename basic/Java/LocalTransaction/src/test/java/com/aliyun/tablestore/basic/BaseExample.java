package com.aliyun.tablestore.basic;

import com.alicloud.openservices.tablestore.SyncClient;
import com.aliyun.tablestore.basic.common.Config;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import java.io.Closeable;
import java.io.IOException;

public abstract class BaseExample {

    protected static Config config;

    protected static SyncClient syncClient;

    @BeforeClass
    public static void beforeClass() {
        config = Config.newInstance();
        syncClient = config.newClient();
    }

    @AfterClass
    public static void afterClass() {
        IOUtils.closeQuietly(syncClient::shutdown);
    }

    @Before
    public abstract void init();

    @After
    public abstract void tearDown();
}
