package com.aliyun.tablestore.basic.dataManage;

import com.alicloud.openservices.tablestore.AsyncClient;
import com.alicloud.openservices.tablestore.model.*;
import com.alicloud.openservices.tablestore.timestream.model.GetRangeIterator;
import com.aliyun.tablestore.basic.dataManage.base.BaseManage;

import java.util.List;

import static com.aliyun.tablestore.basic.common.Consts.PK1;
import static com.aliyun.tablestore.basic.common.Consts.PK2;
import static com.aliyun.tablestore.basic.common.Consts.TABLE_NAME;

/**
 * @Author wtt
 * @create 2019/11/28 12:19 PM
 */
public class MultiRowManage extends BaseManage {

    public static void main(String[] args) {
        MultiRowManage manage = new MultiRowManage();

        manage.multiWriteRow();
        manage.multiRandomReadRow();

        manage.rangeReadRow();

        manage.paginateRangeReadRow();
        manage.getRangeIterator();


        boolean wantDeleteTable = false;
        manage.close(wantDeleteTable);          // delete table when close
    }

    public void multiWriteRow() {
        BatchWriteRowRequest batchWriteRowRequest = new BatchWriteRowRequest();

        /**
         * Multi Put Row
         */
        for (long i = 1L; i <= 10L; i++) {
            for (long j = 0L; j < 10L; j++) {

                PrimaryKey primaryKey = PrimaryKeyBuilder.createPrimaryKeyBuilder()
                        .addPrimaryKeyColumn(PK1, PrimaryKeyValue.fromLong(i))
                        .addPrimaryKeyColumn(PK2, PrimaryKeyValue.fromString(j + ""))
                        .build();

                RowPutChange rowChange = new RowPutChange(TABLE_NAME, primaryKey);
                rowChange.addColumn("long", ColumnValue.fromLong(i));

                batchWriteRowRequest.addRowChange(rowChange);
            }
        }

        /**
         * Multi Update Row
         */
        for (long i = 0L; i < 10L; i++) {
            PrimaryKey primaryKey = PrimaryKeyBuilder.createPrimaryKeyBuilder()
                    .addPrimaryKeyColumn(PK1, PrimaryKeyValue.fromLong(i))
                    .addPrimaryKeyColumn(PK2, PrimaryKeyValue.fromString("0"))
                    .build();

            RowUpdateChange rowChange = new RowUpdateChange(TABLE_NAME, primaryKey);
            rowChange.put("long", ColumnValue.fromLong(i + 100));

            batchWriteRowRequest.addRowChange(rowChange);
        }

        /**
         * Multi Delete Row
         */
        for (long i = 0L; i < 10L; i++) {
            PrimaryKey primaryKey = PrimaryKeyBuilder.createPrimaryKeyBuilder()
                    .addPrimaryKeyColumn(PK1, PrimaryKeyValue.fromLong(9))
                    .addPrimaryKeyColumn(PK2, PrimaryKeyValue.fromString(i + ""))
                    .build();

            RowDeleteChange rowChange = new RowDeleteChange(TABLE_NAME, primaryKey);

            batchWriteRowRequest.addRowChange(rowChange);
        }

        BatchWriteRowResponse batchWriteRowResponse = syncClient.batchWriteRow(batchWriteRowRequest);
        int totalRowCount = batchWriteRowResponse.getRowStatus(TABLE_NAME).size();
        int failedRowCount = batchWriteRowResponse.getFailedRows().size();


        if (batchWriteRowResponse.getFailedRows().size() > 0 ) {
            BatchWriteRowRequest retryRequest = batchWriteRowRequest.createRequestForRetry(
                    batchWriteRowResponse.getFailedRows());

            syncClient.batchWriteRow(retryRequest);
        }

        System.out.println(String.format("Batch Write Row Success: \n\tBatchWriteRowCount: [%d]\n\tFailedCount: [%d]",
                totalRowCount,
                failedRowCount));
    }


    public void multiRandomReadRow() {
        BatchGetRowRequest batchGetRowRequest = new BatchGetRowRequest();
        MultiRowQueryCriteria criteria = new MultiRowQueryCriteria(TABLE_NAME);

        /**
         * Get Multi Row At A Time
         */
        for (long i = 7L; i < 10L; i += 1) {
            PrimaryKey primaryKey = PrimaryKeyBuilder.createPrimaryKeyBuilder()
                    .addPrimaryKeyColumn(PK1, PrimaryKeyValue.fromLong(i))
                    .addPrimaryKeyColumn(PK2, PrimaryKeyValue.fromString(i + ""))
                    .build();

            criteria.addRow(primaryKey);
        }

        criteria.setMaxVersions(1);
        batchGetRowRequest.addMultiRowQueryCriteria(criteria);

        BatchGetRowResponse batchGetRowResponse = syncClient.batchGetRow(batchGetRowRequest);
        int totalRowCount = batchGetRowResponse.getBatchGetRowResult(TABLE_NAME).size();
        int failedRowCount = batchGetRowResponse.getFailedRows().size();
        System.out.println(String.format("Batch Get Row Success: \n\tBatchWriteRowCount: [%d]\n\tFailedCount: [%d]",
                totalRowCount,
                failedRowCount));

        for (BatchGetRowResponse.RowResult rowResult : batchGetRowResponse.getBatchGetRowResult(TABLE_NAME)) {
            if (rowResult.getRow() == null) {
                System.out.println(String.format(
                        "\tGet Row PrimaryKey: [%s] Row Status: [%s]. But Not Exist!",
                        criteria.get(rowResult.getIndex()).toString(),
                        rowResult.isSucceed()));
            } else {
                System.out.println(String.format(
                        "\tGet Row PrimaryKey: [%s] Row Status: [%s]",
                        rowResult.getRow().getPrimaryKey().toString(),
                        rowResult.isSucceed()));
            }
        }

    }



    public void rangeReadRow() {
        GetRangeRequest getRangeRequest = new GetRangeRequest();

        PrimaryKey start = PrimaryKeyBuilder.createPrimaryKeyBuilder()
                .addPrimaryKeyColumn(PK1, PrimaryKeyValue.fromLong(2))
                .addPrimaryKeyColumn(PK2, PrimaryKeyValue.fromString("5"))
                .build();

        PrimaryKey end = PrimaryKeyBuilder.createPrimaryKeyBuilder()
                .addPrimaryKeyColumn(PK1, PrimaryKeyValue.fromLong(4))
                .addPrimaryKeyColumn(PK2, PrimaryKeyValue.fromString("4"))
                .build();

        RangeRowQueryCriteria criteria = new RangeRowQueryCriteria(TABLE_NAME);
        criteria.setInclusiveStartPrimaryKey(start);
        criteria.setExclusiveEndPrimaryKey(end);
        criteria.setDirection(Direction.FORWARD);
        criteria.setMaxVersions(1);
        criteria.setLimit(20);

        getRangeRequest.setRangeRowQueryCriteria(criteria);

        GetRangeResponse getRangeResponse = syncClient.getRange(getRangeRequest);

        System.out.println(String.format("Get Range Success: \n\tReturn Row Count: [%d]\n\tNext Start PrimaryKey: [%s]",
                getRangeResponse.getRows().size(),
                getRangeResponse.getNextStartPrimaryKey().toString()));

        for (Row row : getRangeResponse.getRows()) {
            System.out.println(String.format("PrimaryKey: [%s]",
                    row.getPrimaryKey().toString()));
        }

    }

    public void paginateRangeReadRow() {
        GetRangeRequest getRangeRequest = new GetRangeRequest();

        PrimaryKey start = PrimaryKeyBuilder.createPrimaryKeyBuilder()
                .addPrimaryKeyColumn(PK1, PrimaryKeyValue.INF_MIN)
                .addPrimaryKeyColumn(PK2, PrimaryKeyValue.INF_MIN)
                .build();

        PrimaryKey end = PrimaryKeyBuilder.createPrimaryKeyBuilder()
                .addPrimaryKeyColumn(PK1, PrimaryKeyValue.INF_MAX)
                .addPrimaryKeyColumn(PK2, PrimaryKeyValue.INF_MAX)
                .build();

        RangeRowQueryCriteria criteria = new RangeRowQueryCriteria(TABLE_NAME);
        criteria.setInclusiveStartPrimaryKey(start);
        criteria.setExclusiveEndPrimaryKey(end);
        criteria.setMaxVersions(1);
        criteria.setLimit(20);

        getRangeRequest.setRangeRowQueryCriteria(criteria);

        int counter = 0;
        PrimaryKey nextStartPrimaryKey = null;
        do {
            GetRangeResponse getRangeResponse = syncClient.getRange(getRangeRequest);
            List<Row> pageRowList = getRangeResponse.getRows();
            counter += pageRowList.size();

            nextStartPrimaryKey = getRangeResponse.getNextStartPrimaryKey();
            if (nextStartPrimaryKey != null) {
                criteria.setInclusiveStartPrimaryKey(nextStartPrimaryKey);
                getRangeRequest.setRangeRowQueryCriteria(criteria);
            }
        } while (nextStartPrimaryKey != null);

        System.out.println(String.format("Get Range By Paginate Success: \n\tReturn Row Count: [%d]", counter));
    }


    public void getRangeIterator() {

        GetRangeRequest getRangeRequest = new GetRangeRequest();

        PrimaryKey start = PrimaryKeyBuilder.createPrimaryKeyBuilder()
                .addPrimaryKeyColumn(PK1, PrimaryKeyValue.INF_MIN)
                .addPrimaryKeyColumn(PK2, PrimaryKeyValue.INF_MIN)
                .build();

        PrimaryKey end = PrimaryKeyBuilder.createPrimaryKeyBuilder()
                .addPrimaryKeyColumn(PK1, PrimaryKeyValue.INF_MAX)
                .addPrimaryKeyColumn(PK2, PrimaryKeyValue.INF_MAX)
                .build();

        RangeRowQueryCriteria criteria = new RangeRowQueryCriteria(TABLE_NAME);
        criteria.setInclusiveStartPrimaryKey(start);
        criteria.setExclusiveEndPrimaryKey(end);
        criteria.setMaxVersions(1);
        criteria.setLimit(20);

        getRangeRequest.setRangeRowQueryCriteria(criteria);

        AsyncClient asyncClient = (AsyncClient) syncClient.asAsyncClient();
        GetRangeIterator getRangeIterator = new GetRangeIterator(asyncClient, getRangeRequest);

        int counter = 0;
        while (getRangeIterator.hasNext()) {
            Row row = getRangeIterator.next();
            counter++;
        }

        System.out.println(String.format("Get Range By Iterator Success: \n\tReturn Row Count: [%d]", counter));
    }
}
