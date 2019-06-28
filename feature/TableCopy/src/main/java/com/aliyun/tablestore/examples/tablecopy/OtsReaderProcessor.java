package com.aliyun.tablestore.examples.tablecopy;

import java.util.ArrayList;
import java.util.List;

import com.alicloud.openservices.tablestore.SyncClient;
import com.alicloud.openservices.tablestore.model.BatchWriteRowRequest;
import com.alicloud.openservices.tablestore.model.Column;
import com.alicloud.openservices.tablestore.model.RecordColumn;
import com.alicloud.openservices.tablestore.model.RowDeleteChange;
import com.alicloud.openservices.tablestore.model.RowPutChange;
import com.alicloud.openservices.tablestore.model.RowUpdateChange;
import com.alicloud.openservices.tablestore.model.StreamRecord;
import com.alicloud.openservices.tablestore.tunnel.worker.IChannelProcessor;
import com.alicloud.openservices.tablestore.tunnel.worker.ProcessRecordsInput;
import com.aliyun.tablestore.examples.tablecopy.util.OtsReaderConfig;
import com.aliyun.tablestore.examples.tablecopy.util.OtsWriterConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OtsReaderProcessor implements IChannelProcessor {
    private static Logger LOG = LoggerFactory.getLogger(OtsReaderProcessor.class);
    private OtsReaderConfig readConf;
    private OtsWriterConfig writeConf;
    private SyncClient writeClient;

    public OtsReaderProcessor(OtsReaderConfig readConf, OtsWriterConfig writeConf, SyncClient writeClient) {
        this.readConf = readConf;
        this.writeConf = writeConf;
        this.writeClient = writeClient;
    }

    @Override
    public void process(ProcessRecordsInput input) {
        System.out.println(String.format("Begin process %d records.", input.getRecords().size()));
        BatchWriteRowRequest batchWriteRowRequest = new BatchWriteRowRequest();
        int count = 0;
        for (StreamRecord record : input.getRecords()) {
            if (record.getSequenceInfo().getTimestamp() / 1000 > readConf.getEndTime()) {
                System.out.println(String.format("skip record timestamp %d larger than endTime %d",
                    record.getSequenceInfo().getTimestamp() / 1000, readConf.getEndTime()));
                continue;
            }
            count++;
            switch (record.getRecordType()) {
                case PUT:
                    RowPutChange putChange = new RowPutChange(writeConf.getTableName(), record.getPrimaryKey());
                    putChange.addColumns(getColumns(record));
                    batchWriteRowRequest.addRowChange(putChange);
                    break;
                case UPDATE:
                    RowUpdateChange updateChange = new RowUpdateChange(writeConf.getTableName(),
                        record.getPrimaryKey());
                    for (RecordColumn column : record.getColumns()) {
                        switch (column.getColumnType()) {
                            case PUT:
                                updateChange.put(column.getColumn());
                                break;
                            case DELETE_ONE_VERSION:
                                updateChange.deleteColumn(column.getColumn().getName(),
                                    column.getColumn().getTimestamp());
                                break;
                            case DELETE_ALL_VERSION:
                                updateChange.deleteColumns(column.getColumn().getName());
                                break;
                            default:
                                break;
                        }
                    }
                    batchWriteRowRequest.addRowChange(updateChange);
                    break;
                case DELETE:
                    RowDeleteChange deleteChange = new RowDeleteChange(writeConf.getTableName(),
                        record.getPrimaryKey());
                    batchWriteRowRequest.addRowChange(deleteChange);
                    break;
                default:
                    break;
            }

            if (count == writeConf.getBatchWriteCount()) {
                System.out.println("BatchWriteRow: " + count);
                writeClient.batchWriteRow(batchWriteRowRequest);
                batchWriteRowRequest = new BatchWriteRowRequest();
                count = 0;
            }
        }

        // 写最后一次的数据。
        if (!batchWriteRowRequest.isEmpty()) {
            System.out.println("BatchWriteRow: " + count);
            writeClient.batchWriteRow(batchWriteRowRequest);
        }
    }

    @Override
    public void shutdown() {
        System.out.println("mock shutdown");
    }

    public List<Column> getColumns(StreamRecord record) {
        List<Column> retColumns = new ArrayList<Column>();
        for (RecordColumn column : record.getColumns()) {
            retColumns.add(column.getColumn());
        }
        return retColumns;
    }

}
