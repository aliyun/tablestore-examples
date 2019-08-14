package com.aliyun.tablestore.examples;

import com.alicloud.openservices.tablestore.SyncClient;
import com.alicloud.openservices.tablestore.TableStoreException;
import com.alicloud.openservices.tablestore.model.*;
import com.aliyun.tablestore.examples.utils.Conf;
import org.joda.time.DateTime;

import java.util.concurrent.TimeUnit;

public class ReadData {

    private static void printRow(Row row) {
        long ts = row.getPrimaryKey().getPrimaryKeyColumn("ts").getValue().asLong();
        DateTime dateTime = new DateTime(ts * 1000);
        String timeStr = dateTime.toString("yyyy/MM/dd HH:mm:ss");
        System.out.println(timeStr +
                ", Price:" + row.getColumn("price").get(0).getValue().asDouble() +
                ", Count:" + row.getColumn("ordercount").get(0).getValue().asLong());
    }

    public static void main(String[] args) throws Exception {
        long interval = 2; // 间隔秒
        Conf conf = Conf.newInstance();

        SyncClient client = new SyncClient(
                conf.getEndpoint(),
                conf.getAccessId(),
                conf.getAccessKey(),
                conf.getInstanceName()
        );

        try {
            long index = System.currentTimeMillis() / 1000 / interval * interval - 10;
            System.out.println(index);
            while (true) {

                SingleRowQueryCriteria rowQueryCriteria = new SingleRowQueryCriteria(conf.getSinkTableName());
                rowQueryCriteria.setMaxVersions(1);
                rowQueryCriteria.setPrimaryKey(PrimaryKeyBuilder.createPrimaryKeyBuilder()
                        .addPrimaryKeyColumn("metering", PrimaryKeyValue.fromString("web"))
                        .addPrimaryKeyColumn("ts", PrimaryKeyValue.fromLong(index)).build());
                GetRowRequest request = new GetRowRequest();
                request.setRowQueryCriteria(rowQueryCriteria);
                try {
                    GetRowResponse response = client.getRow(request);
                    Row row = response.getRow();
                    if (row != null) {
                        ReadData.printRow(row);
                        index += interval;
                    } else {
                        if (index < (System.currentTimeMillis() / 1000 - 10)) {
                            index += 1;
                        }
                    }
                    Thread.sleep(TimeUnit.MILLISECONDS.toMillis(50));
                } catch (TableStoreException e) {
                    System.out.println("Got error, " + e.getRequestId() + ", " + e.getErrorCode() + ", " + e.getMessage());
                }
            }
        } finally {
            client.shutdown();
        }
    }

}
