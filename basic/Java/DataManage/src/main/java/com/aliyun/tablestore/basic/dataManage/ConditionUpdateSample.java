package com.aliyun.tablestore.basic.dataManage;

import com.alicloud.openservices.tablestore.SyncClient;
import com.alicloud.openservices.tablestore.TableStoreException;
import com.alicloud.openservices.tablestore.core.ErrorCode;
import com.alicloud.openservices.tablestore.model.*;
import com.alicloud.openservices.tablestore.model.condition.CompositeColumnValueCondition;
import com.alicloud.openservices.tablestore.model.condition.SingleColumnValueCondition;
import com.alicloud.openservices.tablestore.model.filter.SingleColumnValueFilter;
import com.aliyun.tablestore.basic.common.Config;
import org.apache.http.util.Asserts;

import java.util.concurrent.TimeUnit;


public class ConditionUpdateSample {

    protected Config config;
    protected SyncClient syncClient;

    private final static String TABLE_NAME = "condition_update_sample";
    private final static String PK = "部门";

    public ConditionUpdateSample() {
        config = Config.newInstance();
        syncClient = config.newClient();
    }

    public void close() {
        syncClient.shutdown();
    }

    private void tryInitTable() throws InterruptedException {
        System.out.println("Prepare table ...");
        try {
            syncClient.deleteTable(new DeleteTableRequest(TABLE_NAME));
        } catch (TableStoreException e) {
            if (!e.getErrorCode().equals(ErrorCode.OBJECT_NOT_EXIST)) {
                throw e;
            }
        }

        TableMeta tableMeta = new TableMeta(TABLE_NAME);
        tableMeta.addPrimaryKeyColumn(PK, PrimaryKeyType.STRING);

        TableOptions tableOptions = new TableOptions();
        tableOptions.setTimeToLive(-1);
        tableOptions.setMaxVersions(1);
        CreateTableRequest createTableRequest = new CreateTableRequest(tableMeta, tableOptions);

        syncClient.createTable(createTableRequest);

        Thread.sleep(TimeUnit.SECONDS.toMillis(5));

        init();
    }

    private void init() {
        System.out.println("Insert rows into table ...");
        {
            RowPutChange rowChange = new RowPutChange(TABLE_NAME);
            rowChange.setPrimaryKey(PrimaryKeyBuilder.createPrimaryKeyBuilder()
                    .addPrimaryKeyColumn(PK, PrimaryKeyValue.fromString("技术部门"))
                    .build());
            rowChange.addColumn("人员数目", ColumnValue.fromLong(10));
            rowChange.addColumn("人员HC数目", ColumnValue.fromLong(30));
            rowChange.addColumn("奖金池", ColumnValue.fromLong(32));
            rowChange.addColumn("薪资池", ColumnValue.fromLong(12034));

            PutRowRequest putRowRequest = new PutRowRequest();
            putRowRequest.setRowChange(rowChange);
            syncClient.putRow(putRowRequest);
        }
        {
            RowPutChange rowChange = new RowPutChange(TABLE_NAME);
            rowChange.setPrimaryKey(PrimaryKeyBuilder.createPrimaryKeyBuilder()
                    .addPrimaryKeyColumn(PK, PrimaryKeyValue.fromString("财务部门"))
                    .build());
            rowChange.addColumn("人员数目", ColumnValue.fromLong(2));
            rowChange.addColumn("人员HC数目", ColumnValue.fromLong(2));
            rowChange.addColumn("奖金池", ColumnValue.fromLong(0));
            rowChange.addColumn("薪资池", ColumnValue.fromLong(0));

            PutRowRequest putRowRequest = new PutRowRequest();
            putRowRequest.setRowChange(rowChange);
            syncClient.putRow(putRowRequest);
        }
        {
            RowPutChange rowChange = new RowPutChange(TABLE_NAME);
            rowChange.setPrimaryKey(PrimaryKeyBuilder.createPrimaryKeyBuilder()
                    .addPrimaryKeyColumn(PK, PrimaryKeyValue.fromString("行政部门"))
                    .build());
            rowChange.addColumn("人员数目", ColumnValue.fromLong(3));
            rowChange.addColumn("人员HC数目", ColumnValue.fromLong(5));
            rowChange.addColumn("奖金池", ColumnValue.fromLong(3));
            rowChange.addColumn("薪资池", ColumnValue.fromLong(12));

            PutRowRequest putRowRequest = new PutRowRequest();
            putRowRequest.setRowChange(rowChange);
            syncClient.putRow(putRowRequest);
        }
        {
            RowPutChange rowChange = new RowPutChange(TABLE_NAME);
            rowChange.setPrimaryKey(PrimaryKeyBuilder.createPrimaryKeyBuilder()
                    .addPrimaryKeyColumn(PK, PrimaryKeyValue.fromString("销售部门"))
                    .build());
            rowChange.addColumn("人员数目", ColumnValue.fromLong(0));
            rowChange.addColumn("人员HC数目", ColumnValue.fromLong(30));
            rowChange.addColumn("奖金池", ColumnValue.fromLong(30));
            rowChange.addColumn("薪资池", ColumnValue.fromLong(35));

            PutRowRequest putRowRequest = new PutRowRequest();
            putRowRequest.setRowChange(rowChange);
            syncClient.putRow(putRowRequest);
        }

    }

    private void case1ByPutRow() throws Exception {
        tryInitTable();

        System.out.println("Running case 1 by PutRow ...");
        {
            RowPutChange rowChange = new RowPutChange(TABLE_NAME);
            rowChange.setPrimaryKey(PrimaryKeyBuilder.createPrimaryKeyBuilder()
                    .addPrimaryKeyColumn(PK, PrimaryKeyValue.fromString("运营部门"))
                    .build());
            rowChange.addColumn("人员数目", ColumnValue.fromLong(0));
            rowChange.addColumn("人员HC数目", ColumnValue.fromLong(30));
            rowChange.addColumn("奖金池", ColumnValue.fromLong(30));
            rowChange.addColumn("薪资池", ColumnValue.fromLong(35));

            rowChange.setCondition(new Condition(RowExistenceExpectation.EXPECT_NOT_EXIST));

            PutRowRequest putRowRequest = new PutRowRequest();
            putRowRequest.setRowChange(rowChange);
            syncClient.putRow(putRowRequest);
        }
        {
            RowPutChange rowChange = new RowPutChange(TABLE_NAME);
            rowChange.setPrimaryKey(PrimaryKeyBuilder.createPrimaryKeyBuilder()
                    .addPrimaryKeyColumn(PK, PrimaryKeyValue.fromString("运营部门"))
                    .build());
            rowChange.addColumn("人员数目", ColumnValue.fromLong(0));
            rowChange.addColumn("人员HC数目", ColumnValue.fromLong(30));
            rowChange.addColumn("奖金池", ColumnValue.fromLong(30));
            rowChange.addColumn("薪资池", ColumnValue.fromLong(35));

            rowChange.setCondition(new Condition(RowExistenceExpectation.EXPECT_NOT_EXIST));

            PutRowRequest putRowRequest = new PutRowRequest();
            putRowRequest.setRowChange(rowChange);

            try {
                syncClient.putRow(putRowRequest);
            } catch (TableStoreException e) {
                Asserts.check(
                        e.getErrorCode().equals(ErrorCode.CONDITION_CHECK_FAIL),
                        "期望获取到条件检查失败错误的的异常");
            }
        }
    }

    private void case1ByBatchWriteRow() throws Exception {
        tryInitTable();

        System.out.println("Running case 1 by BatchWriteRow ...");
        {
            RowPutChange rowChange = new RowPutChange(TABLE_NAME);
            rowChange.setPrimaryKey(PrimaryKeyBuilder.createPrimaryKeyBuilder()
                    .addPrimaryKeyColumn(PK, PrimaryKeyValue.fromString("运营部门"))
                    .build());
            rowChange.addColumn("人员数目", ColumnValue.fromLong(0));
            rowChange.addColumn("人员HC数目", ColumnValue.fromLong(30));
            rowChange.addColumn("奖金池", ColumnValue.fromLong(30));
            rowChange.addColumn("薪资池", ColumnValue.fromLong(35));

            rowChange.setCondition(new Condition(RowExistenceExpectation.EXPECT_NOT_EXIST));

            BatchWriteRowRequest request = new BatchWriteRowRequest();
            request.addRowChange(rowChange);
            BatchWriteRowResponse response = syncClient.batchWriteRow(request);

            Asserts.check(response.isAllSucceed(), "期望所有的行执行成功");
        }
        {
            RowPutChange rowChange = new RowPutChange(TABLE_NAME);
            rowChange.setPrimaryKey(PrimaryKeyBuilder.createPrimaryKeyBuilder()
                    .addPrimaryKeyColumn(PK, PrimaryKeyValue.fromString("运营部门"))
                    .build());
            rowChange.addColumn("人员数目", ColumnValue.fromLong(0));
            rowChange.addColumn("人员HC数目", ColumnValue.fromLong(30));
            rowChange.addColumn("奖金池", ColumnValue.fromLong(30));
            rowChange.addColumn("薪资池", ColumnValue.fromLong(35));

            rowChange.setCondition(new Condition(RowExistenceExpectation.EXPECT_NOT_EXIST));

            BatchWriteRowRequest request = new BatchWriteRowRequest();
            request.addRowChange(rowChange);
            BatchWriteRowResponse response = syncClient.batchWriteRow(request);

            Asserts.check(!response.isAllSucceed(), "期望此行操作失败");

            Asserts.check(
                    response.getFailedRows().get(0).getError().getCode().equals(ErrorCode.CONDITION_CHECK_FAIL),
                    "期望获取到条件检查失败错误的的异常");
        }
    }

    private void case2ByUpdateRow() throws Exception {
        tryInitTable();

        System.out.println("Running case 2 by UpdateRow ...");
        {
            RowUpdateChange rowChange = new RowUpdateChange(TABLE_NAME);
            rowChange.setPrimaryKey(PrimaryKeyBuilder.createPrimaryKeyBuilder()
                    .addPrimaryKeyColumn(PK, PrimaryKeyValue.fromString("行政部门"))
                    .build());
            rowChange.put("人员数目", ColumnValue.fromLong(5));

            // 行存在性检查，期望存在
            Condition condition = new Condition(RowExistenceExpectation.EXPECT_EXIST);

            // 列条件检查，期望 ‘人员数目’ 为 3
            SingleColumnValueCondition singleColumnValueCondition = new SingleColumnValueCondition(
                    "人员数目",
                    SingleColumnValueCondition.CompareOperator.EQUAL,
                    ColumnValue.fromLong(3)
            );
            condition.setColumnCondition(singleColumnValueCondition);

            rowChange.setCondition(condition);

            UpdateRowRequest updateRowRequest = new UpdateRowRequest();
            updateRowRequest.setRowChange(rowChange);
            syncClient.updateRow(updateRowRequest);
        }
        {
            RowUpdateChange rowChange = new RowUpdateChange(TABLE_NAME);
            rowChange.setPrimaryKey(PrimaryKeyBuilder.createPrimaryKeyBuilder()
                    .addPrimaryKeyColumn(PK, PrimaryKeyValue.fromString("行政部门"))
                    .build());
            rowChange.put("人员数目", ColumnValue.fromLong(5));

            // 行存在性检查，期望存在
            Condition condition = new Condition(RowExistenceExpectation.EXPECT_EXIST);

            // 列条件检查，期望 ‘人员数目’ 为 3
            SingleColumnValueCondition singleColumnValueCondition = new SingleColumnValueCondition(
                    "人员数目",
                    SingleColumnValueCondition.CompareOperator.EQUAL,
                    ColumnValue.fromLong(3)
            );
            condition.setColumnCondition(singleColumnValueCondition);

            rowChange.setCondition(condition);

            UpdateRowRequest updateRowRequest = new UpdateRowRequest();
            updateRowRequest.setRowChange(rowChange);

            try {
                syncClient.updateRow(updateRowRequest);
            } catch (TableStoreException e) {
                Asserts.check(
                        e.getErrorCode().equals(ErrorCode.CONDITION_CHECK_FAIL),
                        "期望获取到条件检查失败错误的的异常");
            }
        }
    }

    private void case2ByBatchWriteRow() throws Exception {
        tryInitTable();

        System.out.println("Running case 2 by BatchWriteRow ...");
        {
            RowUpdateChange rowChange = new RowUpdateChange(TABLE_NAME);
            rowChange.setPrimaryKey(PrimaryKeyBuilder.createPrimaryKeyBuilder()
                    .addPrimaryKeyColumn(PK, PrimaryKeyValue.fromString("行政部门"))
                    .build());
            rowChange.put("人员数目", ColumnValue.fromLong(5));

            // 行存在性检查，期望存在
            Condition condition = new Condition(RowExistenceExpectation.EXPECT_EXIST);

            // 列条件检查，期望 ‘人员数目’ 为 3
            SingleColumnValueCondition singleColumnValueCondition = new SingleColumnValueCondition(
                    "人员数目",
                    SingleColumnValueCondition.CompareOperator.EQUAL,
                    ColumnValue.fromLong(3)
            );
            condition.setColumnCondition(singleColumnValueCondition);

            rowChange.setCondition(condition);

            BatchWriteRowRequest request = new BatchWriteRowRequest();
            request.addRowChange(rowChange);
            BatchWriteRowResponse response = syncClient.batchWriteRow(request);

            Asserts.check(response.isAllSucceed(), "期望所有的行执行成功");
        }
        {
            RowUpdateChange rowChange = new RowUpdateChange(TABLE_NAME);
            rowChange.setPrimaryKey(PrimaryKeyBuilder.createPrimaryKeyBuilder()
                    .addPrimaryKeyColumn(PK, PrimaryKeyValue.fromString("行政部门"))
                    .build());
            rowChange.put("人员数目", ColumnValue.fromLong(5));

            // 行存在性检查，期望存在
            Condition condition = new Condition(RowExistenceExpectation.EXPECT_EXIST);

            // 列条件检查，期望 ‘人员数目’ 为 3
            SingleColumnValueCondition singleColumnValueCondition = new SingleColumnValueCondition(
                    "人员数目",
                    SingleColumnValueCondition.CompareOperator.EQUAL,
                    ColumnValue.fromLong(3)
            );
            condition.setColumnCondition(singleColumnValueCondition);

            rowChange.setCondition(condition);

            BatchWriteRowRequest request = new BatchWriteRowRequest();
            request.addRowChange(rowChange);
            BatchWriteRowResponse response = syncClient.batchWriteRow(request);

            Asserts.check(!response.isAllSucceed(), "期望此行操作失败");

            Asserts.check(
                    response.getFailedRows().get(0).getError().getCode().equals(ErrorCode.CONDITION_CHECK_FAIL),
                    "期望获取到条件检查失败错误的的异常");
        }
    }

    private void case3ByDeleteRow() throws Exception {
        tryInitTable();

        System.out.println("Running case 3 by DeleteRow ...");
        {
            RowDeleteChange rowChange = new RowDeleteChange(TABLE_NAME);
            rowChange.setPrimaryKey(PrimaryKeyBuilder.createPrimaryKeyBuilder()
                    .addPrimaryKeyColumn(PK, PrimaryKeyValue.fromString("销售部门"))
                    .build());

            // 行存在性检查，期望存在
            Condition condition = new Condition(RowExistenceExpectation.EXPECT_EXIST);
            rowChange.setCondition(condition);

            DeleteRowRequest deleteRowRequest = new DeleteRowRequest();
            deleteRowRequest.setRowChange(rowChange);
            syncClient.deleteRow(deleteRowRequest);
        }
        {
            RowDeleteChange rowChange = new RowDeleteChange(TABLE_NAME);
            rowChange.setPrimaryKey(PrimaryKeyBuilder.createPrimaryKeyBuilder()
                    .addPrimaryKeyColumn(PK, PrimaryKeyValue.fromString("销售部门"))
                    .build());

            // 行存在性检查，期望存在
            Condition condition = new Condition(RowExistenceExpectation.EXPECT_EXIST);
            rowChange.setCondition(condition);

            DeleteRowRequest deleteRowRequest = new DeleteRowRequest();
            deleteRowRequest.setRowChange(rowChange);

            try {
                syncClient.deleteRow(deleteRowRequest);
            } catch (TableStoreException e) {
                Asserts.check(
                        e.getErrorCode().equals(ErrorCode.CONDITION_CHECK_FAIL),
                        "期望获取到条件检查失败错误的的异常");
            }
        }
    }

    private void case3ByBatchWriteRow() throws Exception {
        tryInitTable();

        System.out.println("Running case 3 by BatchWriteRow ...");
        {
            RowDeleteChange rowChange = new RowDeleteChange(TABLE_NAME);
            rowChange.setPrimaryKey(PrimaryKeyBuilder.createPrimaryKeyBuilder()
                    .addPrimaryKeyColumn(PK, PrimaryKeyValue.fromString("销售部门"))
                    .build());

            // 行存在性检查，期望存在
            Condition condition = new Condition(RowExistenceExpectation.EXPECT_EXIST);
            rowChange.setCondition(condition);

            BatchWriteRowRequest request = new BatchWriteRowRequest();
            request.addRowChange(rowChange);
            BatchWriteRowResponse response = syncClient.batchWriteRow(request);

            Asserts.check(response.isAllSucceed(), "期望所有的行执行成功");
        }
        {
            RowDeleteChange rowChange = new RowDeleteChange(TABLE_NAME);
            rowChange.setPrimaryKey(PrimaryKeyBuilder.createPrimaryKeyBuilder()
                    .addPrimaryKeyColumn(PK, PrimaryKeyValue.fromString("销售部门"))
                    .build());

            // 行存在性检查，期望存在
            Condition condition = new Condition(RowExistenceExpectation.EXPECT_EXIST);
            rowChange.setCondition(condition);

            BatchWriteRowRequest request = new BatchWriteRowRequest();
            request.addRowChange(rowChange);
            BatchWriteRowResponse response = syncClient.batchWriteRow(request);

            Asserts.check(!response.isAllSucceed(), "期望此行操作失败");

            Asserts.check(
                    response.getFailedRows().get(0).getError().getCode().equals(ErrorCode.CONDITION_CHECK_FAIL),
                    "期望获取到条件检查失败错误的的异常");
        }
    }

    private void case4ByUpdateRow() throws Exception {
        tryInitTable();

        System.out.println("Running case 4 by UpdateRow ...");
        {
            RowUpdateChange rowChange = new RowUpdateChange(TABLE_NAME);
            rowChange.setPrimaryKey(PrimaryKeyBuilder.createPrimaryKeyBuilder()
                    .addPrimaryKeyColumn(PK, PrimaryKeyValue.fromString("财务部门"))
                    .build());
            rowChange.increment(new Column("人员HC数目", ColumnValue.fromLong(2)));

            // 行存在性检查
            Condition condition = new Condition(RowExistenceExpectation.IGNORE);

            CompositeColumnValueCondition conditions = new CompositeColumnValueCondition(CompositeColumnValueCondition.LogicOperator.OR);

            {
                // 列条件检查，期望 ‘人员数目’ 小于等于5
                SingleColumnValueCondition singleColumnValueCondition = new SingleColumnValueCondition(
                        "人员数目",
                        SingleColumnValueCondition.CompareOperator.LESS_EQUAL,
                        ColumnValue.fromLong(5)
                );
                conditions.addCondition(singleColumnValueCondition);
            }

            {
                // 列条件检查，期望 ‘人员HC数目’ 小于等于5
                SingleColumnValueCondition singleColumnValueCondition = new SingleColumnValueCondition(
                        "人员HC数目",
                        SingleColumnValueCondition.CompareOperator.LESS_EQUAL,
                        ColumnValue.fromLong(5)
                );
                conditions.addCondition(singleColumnValueCondition);
            }

            condition.setColumnCondition(conditions);

            rowChange.setCondition(condition);

            UpdateRowRequest updateRowRequest = new UpdateRowRequest();
            updateRowRequest.setRowChange(rowChange);
            syncClient.updateRow(updateRowRequest);
        }
    }

    private void case4ByBatchWriteRow() throws Exception {
        tryInitTable();

        System.out.println("Running case 4 by BatchWriteRow ...");
        {
            RowUpdateChange rowChange = new RowUpdateChange(TABLE_NAME);
            rowChange.setPrimaryKey(PrimaryKeyBuilder.createPrimaryKeyBuilder()
                    .addPrimaryKeyColumn(PK, PrimaryKeyValue.fromString("财务部门"))
                    .build());
            rowChange.increment(new Column("人员HC数目", ColumnValue.fromLong(2)));

            // 行存在性检查
            Condition condition = new Condition(RowExistenceExpectation.IGNORE);

            CompositeColumnValueCondition conditions = new CompositeColumnValueCondition(CompositeColumnValueCondition.LogicOperator.OR);

            {
                // 列条件检查，期望 ‘人员数目’ 小于等于5
                SingleColumnValueCondition singleColumnValueCondition = new SingleColumnValueCondition(
                        "人员数目",
                        SingleColumnValueCondition.CompareOperator.LESS_EQUAL,
                        ColumnValue.fromLong(5)
                );
                conditions.addCondition(singleColumnValueCondition);
            }

            {
                // 列条件检查，期望 ‘人员HC数目’ 小于等于5
                SingleColumnValueCondition singleColumnValueCondition = new SingleColumnValueCondition(
                        "人员HC数目",
                        SingleColumnValueCondition.CompareOperator.LESS_EQUAL,
                        ColumnValue.fromLong(5)
                );
                conditions.addCondition(singleColumnValueCondition);
            }

            condition.setColumnCondition(conditions);

            rowChange.setCondition(condition);

            BatchWriteRowRequest request = new BatchWriteRowRequest();
            request.addRowChange(rowChange);
            BatchWriteRowResponse response = syncClient.batchWriteRow(request);

            Asserts.check(response.isAllSucceed(), "期望所有的行执行成功");
        }
    }

    public void actions() throws Exception {
        // case 1
        case1ByPutRow();
        case1ByBatchWriteRow();
        // case 2
        case2ByUpdateRow();
        case2ByBatchWriteRow();
        // case 3
        case3ByDeleteRow();
        case3ByBatchWriteRow();
        // case 4
        case4ByUpdateRow();
        case4ByBatchWriteRow();
    }

    public static void main(String[] args) throws Exception {
        ConditionUpdateSample sample = new ConditionUpdateSample();
        try {
            sample.actions();
        } finally {
            sample.close();
        }
    }
}
