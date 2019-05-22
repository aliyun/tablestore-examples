package com.aliyun.tablestore.example;

import com.alicloud.openservices.tablestore.model.DeleteTableRequest;
import com.alicloud.openservices.tablestore.model.search.DeleteSearchIndexRequest;
import com.alicloud.openservices.tablestore.model.search.ListSearchIndexRequest;
import com.alicloud.openservices.tablestore.model.search.ListSearchIndexResponse;
import com.alicloud.openservices.tablestore.model.search.SearchIndexInfo;
import com.aliyun.tablestore.example.utils.ClientAndConfig;
import com.aliyun.tablestore.example.utils.Utils;

public class CleanupEnv extends BaseExample {

    public CleanupEnv(ClientAndConfig clientAndConfig) {
        super(clientAndConfig);
    }

    public static void main(String[] args) {
        new CleanupEnv(Utils.getClientAndConfig(args)).main();
    }

    private void deleteIndex() {
        ListSearchIndexRequest listRequest = new ListSearchIndexRequest();
        listRequest.setTableName(tableName);
        ListSearchIndexResponse listResponse = syncClient.listSearchIndex(listRequest);
        for (SearchIndexInfo searchIndexInfo : listResponse.getIndexInfos()) {
            System.out.println("Deleting SearchIndex " + searchIndexInfo.getIndexName());
            DeleteSearchIndexRequest deleteRequest = new DeleteSearchIndexRequest();
            deleteRequest.setTableName(tableName);
            deleteRequest.setIndexName(searchIndexInfo.getIndexName());
            syncClient.deleteSearchIndex(deleteRequest);
        }
    }

    private void deleteTable() {
        System.out.println("Deleting Table");
        DeleteTableRequest deleteRequest = new DeleteTableRequest(tableName);
        syncClient.deleteTable(deleteRequest);
    }

    @Override
    protected void doMain() {
        deleteIndex();
        deleteTable();
        System.out.println("Delete done");
    }
}
