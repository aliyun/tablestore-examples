package com.aliyun.tablestore.example;

import com.aliyun.tablestore.example.common.SportTrackExample;

public class CreateStoreExample extends SportTrackExample {
    /**
     * we must create store before we can use it.
     *
     * @throws Exception
     */
    public void createStore() {
        sportTrack.createTable();
    }

    public static void main(String[] args) {
        CreateStoreExample example = new CreateStoreExample();
        example.createStore();
    }
}
