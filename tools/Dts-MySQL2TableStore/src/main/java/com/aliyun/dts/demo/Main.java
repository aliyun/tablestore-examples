package com.aliyun.dts.demo;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Iterator;
import com.fasterxml.jackson.databind.JsonNode;
import com.alicloud.openservices.tablestore.ClientException;
import com.alicloud.openservices.tablestore.SyncClient;
import com.alicloud.openservices.tablestore.TableStoreException;
import com.alicloud.openservices.tablestore.model.*;
import com.alicloud.openservices.tablestore.model.condition.ColumnCondition;
import com.alicloud.openservices.tablestore.model.condition.SingleColumnValueCondition;
import com.alicloud.openservices.tablestore.model.filter.SingleColumnValueFilter;

import com.aliyun.drc.client.message.DataMessage;
import com.aliyun.drc.clusterclient.ClusterClient;
import com.aliyun.drc.clusterclient.message.ClusterMessage;
import com.aliyun.drc.clusterclient.DefaultClusterClient;
import com.aliyun.drc.clusterclient.RegionContext;
import com.aliyun.drc.clusterclient.ClusterListener;

import com.aliyun.dts.demo.utils.Utils;

public class Main {

    private static final String TABLE_NAME = ""; //Tablestore tablename

    public static void main(String[] args) {
        JsonNode config = Utils.getClientAndConfig();
        final String accessId = config.get("accessId").asText();
        final String accessKey = config.get("accessKey").asText();
        final String otsEndPoint = config.get("endpoint").asText();
        final String otsInstanceName = config.get("instanceName").asText();
        final String dtsGuid = config.get("dtsguid").asText();//DTS inanse ID

        final SyncClient otsclient = new SyncClient(otsEndPoint, accessId, accessKey, otsInstanceName);

        // create a context
        RegionContext context = new RegionContext();
        // connect to dts by  internet
        context.setUsePublicIp(true);
        // aliyun accessKey secret
        context.setAccessKey(accessId);
        context.setSecret(accessKey);
        // create a cosummer
        final ClusterClient client = new DefaultClusterClient(context);

        // create listener
        ClusterListener listener = new ClusterListener() {
            @Override
            public void notify(List<ClusterMessage> messages) throws Exception {
                for (ClusterMessage message : messages) {
                    // debug
                    String Opt = message.getRecord().getOpt().toString();
                    List<DataMessage.Record.Field> FieldList = message.getRecord().getFieldList();
                    System.out.println(FieldList);
                    Long[] field = new Long[10];
                    for (int i = 0; i < FieldList.size(); i++) {
                        field[i] =  Long.parseLong(FieldList.get(i).getValue().toString());
                    }
                    //System.out.println(field);
                    System.out.println(Opt);
                    if (Opt == "INSERT") {
                        System.out.println("a");
                        int num = FieldList.size();
                        try {
                            putRow(otsclient, num, field);
                        } catch (TableStoreException e) {
                            System.err.println("操作失败，详情：" + e.getMessage());
                            System.err.println("Request ID:" + e.getRequestId());
                        } catch (ClientException e) {
                            System.err.println("请求失败，详情：" + e.getMessage());
                        }

                    } else if (Opt == "DELETE") {
                        System.out.println("b");
                        try {
                            deleteRow(otsclient,field);
                        } catch (TableStoreException e) {
                            System.err.println("操作失败，详情：" + e.getMessage());
                            System.err.println("Request ID:" + e.getRequestId());
                        } catch (ClientException e) {
                            System.err.println("请求失败，详情：" + e.getMessage());
                        }
                    } else if (Opt == "UPDATE") {
                        System.out.println("c");
                        Long[] field0 = new Long[5];
                        Long[] field1 = new Long[5];
                        int num = FieldList.size() / 2;
                        for(int i=0; i<num; i++) {
                            field0[i] = field[i * 2];
                            field1[i] = field[i * 2 + 1];
                        }
                        try {
                            deleteRow(otsclient,field0);
                            putRow(otsclient,num, field1);
                        } catch (TableStoreException e) {
                            System.err.println("操作失败，详情：" + e.getMessage());
                            System.err.println("Request ID:" + e.getRequestId());
                        } catch (ClientException e) {
                            System.err.println("请求失败，详情：" + e.getMessage());
                        }
                    }
                    //you must call ackAsConsumed when you consume the data
                    message.ackAsConsumed();
                }
            }

            @Override
            public void noException(Exception e) {
                e.printStackTrace();
            }
        };
        // add listener
        client.addConcurrentListener(listener);
        // set inanse ID
        try{
            client.askForGUID(dtsGuid);
            client.start();
        }catch ( Exception e) {
            System.err.println("请求失败，详情：" + e.getMessage());
        }

        //otsclient.shutdown();

    }

    private static void putRow(SyncClient client, int num, Long field[]) {
        // 构造主键
        PrimaryKeyBuilder primaryKeyBuilder = PrimaryKeyBuilder.createPrimaryKeyBuilder();
        primaryKeyBuilder.addPrimaryKeyColumn("pk1",PrimaryKeyValue.fromLong(field[0]));
        primaryKeyBuilder.addPrimaryKeyColumn("pk2",PrimaryKeyValue.fromLong(field[1]));
        PrimaryKey primaryKey = primaryKeyBuilder.build();
        RowPutChange rowPutChange = new RowPutChange(TABLE_NAME, primaryKey);
        //加入属性列
        for(int i=2; i<num ;i++)
            rowPutChange.addColumn(new Column("v"+i, ColumnValue.fromLong(field[i])));

        client.putRow(new PutRowRequest(rowPutChange));
    }

    private static void deleteRow(SyncClient client, Long pk[]) {
        // 构造主键
        PrimaryKeyBuilder primaryKeyBuilder = PrimaryKeyBuilder.createPrimaryKeyBuilder();
        primaryKeyBuilder.addPrimaryKeyColumn("pk1",PrimaryKeyValue.fromLong(pk[0]));
        primaryKeyBuilder.addPrimaryKeyColumn("pk2",PrimaryKeyValue.fromLong(pk[1]));
        PrimaryKey primaryKey = primaryKeyBuilder.build();

        RowDeleteChange rowDeleteChange = new RowDeleteChange(TABLE_NAME, primaryKey);

        client.deleteRow(new DeleteRowRequest(rowDeleteChange));
    }

}
