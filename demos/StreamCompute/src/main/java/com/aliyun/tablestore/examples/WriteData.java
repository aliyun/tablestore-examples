package com.aliyun.tablestore.examples;

import com.alicloud.openservices.tablestore.SyncClient;
import com.alicloud.openservices.tablestore.model.*;
import com.aliyun.tablestore.examples.utils.Conf;
import org.joda.time.DateTime;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class WriteData {

    private static void printData(RowPutChange change) {
        long ts = change.getColumnsToPut("ts").get(0).getValue().asLong();
        DateTime dateTime = new DateTime(ts * 1000);
        String timeStr = dateTime.toString("yyyy/MM/dd HH:mm:ss");
        System.out.println(timeStr +
                " metering:" + change.getPrimaryKey().getPrimaryKeyColumn("metering").getValue().asString() +
                " orderid:" + change.getPrimaryKey().getPrimaryKeyColumn("orderid").getValue().asString() +
                ", ts:" + ts +
                ", price:" + change.getColumnsToPut("price").get(0).getValue().asDouble() +
                ", buyerid:" + change.getColumnsToPut("buyerid").get(0).getValue().asLong()+
                ", sellerid:" + change.getColumnsToPut("sellerid").get(0).getValue().asLong()+
                ", productid:" + change.getColumnsToPut("productid").get(0).getValue().asLong());
    }

    public static void main(String[] args) throws Exception {
        Conf conf = Conf.newInstance();

        SyncClient client = new SyncClient(
                conf.getEndpoint(),
                conf.getAccessId(),
                conf.getAccessKey(),
                conf.getInstanceName()
        );

        try {
            while (true) {
                RowPutChange rowChange = new RowPutChange(conf.getSourceTableName());

                PrimaryKey pk = PrimaryKeyBuilder.createPrimaryKeyBuilder()
                        .addPrimaryKeyColumn("metering", PrimaryKeyValue.fromString("web"))
                        .addPrimaryKeyColumn("orderid", PrimaryKeyValue.fromString("orderid" + System.currentTimeMillis()))
                        .build();

                rowChange.addColumn("ts", ColumnValue.fromLong(System.currentTimeMillis() / 1000));
                rowChange.addColumn("price", ColumnValue.fromDouble(new Random().nextDouble() * 100));
                rowChange.addColumn("buyerid", ColumnValue.fromLong(new Random().nextInt(10000)));
                rowChange.addColumn("sellerid", ColumnValue.fromLong(new Random().nextInt(10000)));
                rowChange.addColumn("productid", ColumnValue.fromLong(new Random().nextInt(10000)));

                rowChange.setPrimaryKey(pk);
                PutRowRequest putRowRequest = new PutRowRequest(rowChange);
                client.putRow(putRowRequest);
                printData(rowChange);
                Thread.sleep(TimeUnit.MILLISECONDS.toMillis(100));
            }
        } finally {
            client.shutdown();
        }
    }
}
