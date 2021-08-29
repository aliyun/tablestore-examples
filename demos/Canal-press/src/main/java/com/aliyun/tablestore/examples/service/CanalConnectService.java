package com.aliyun.tablestore.examples.service;
import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.CanalEntry.*;
import com.alibaba.otter.canal.protocol.Message;
import com.google.protobuf.InvalidProtocolBufferException;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author idea
 * @date 2019/5/6
 * @Version V1.0
 */
@Service
public class CanalConnectService {




    public void init() {

        CanalConnector connector = CanalConnectors.newSingleConnector(new InetSocketAddress("127.0.0.1",
                11111), "test_ots", "", "");
        int batchSize = 1000;
        try {
            connector.connect();
            connector.subscribe("test_ots.test");
            connector.rollback();
            try {
                while (true) {
                    //尝试从master那边拉去数据batchSize条记录，有多少取多少
                    Message message = connector.getWithoutAck(batchSize);
                    long batchId = message.getId();
                    int size = message.getEntries().size();
                    if (batchId == -1 || size == 0) {
                        Thread.sleep(1000);
                    } else {
                        dataHandle(message.getEntries());
                    }
                    connector.ack(batchId);


                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } finally {
            connector.disconnect();
        }
    }

    private static void dataHandle(List<Entry> entrys) throws InvalidProtocolBufferException {
        for (Entry entry : entrys) {
            if (EntryType.ROWDATA == entry.getEntryType()) {
                RowChange rowChange = RowChange.parseFrom(entry.getStoreValue());
                EventType eventType = rowChange.getEventType();

                if (eventType == EventType.DELETE) {
//                    saveDeleteSql(entry);
                    System.out.println(rowChange);
                } else if (eventType == EventType.UPDATE) {
//                    saveUpdateSql(entry);
                    System.out.println(rowChange);
                } else if (eventType == EventType.INSERT) {
//                    saveInsertSql(entry);
                    System.out.println(rowChange);
                }
            }
        }
    }

}
