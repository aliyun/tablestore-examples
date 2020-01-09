package com.aliyun.tablestore.basic.dataManage;

import com.alicloud.openservices.tablestore.model.*;
import com.alicloud.openservices.tablestore.model.condition.ColumnCondition;
import com.alicloud.openservices.tablestore.model.condition.CompositeColumnValueCondition;
import com.alicloud.openservices.tablestore.model.condition.SingleColumnValueCondition;
import com.aliyun.tablestore.basic.dataManage.base.BaseManage;

import static com.aliyun.tablestore.basic.common.Consts.*;

public class UpdateRowExample extends BaseManage {

    public static void main(String[] args) {
        UpdateRowExample manage = new UpdateRowExample();

        manage.updateRowNormally();
        manage.updateRowMultiVersion();
        manage.updateRowWithCondition();
        manage.updateRowIncrement();

        boolean wantDeleteTable = false;
        manage.close(wantDeleteTable);          // delete table when close
    }

    public void updateRowNormally() {
        PrimaryKey primaryKey = PrimaryKeyBuilder.createPrimaryKeyBuilder()
                .addPrimaryKeyColumn(PK1, PrimaryKeyValue.fromLong(1L))
                .addPrimaryKeyColumn(PK2, PrimaryKeyValue.fromString("string"))
                .build();
        /**
         * 构造RowUpdateChange，设置表名和主键
         */
        RowUpdateChange rowChange = new RowUpdateChange(TABLE_NAME, primaryKey);

        /**
         * 写入两列
         */
        rowChange.put("col_str", ColumnValue.fromString("value1"));
        rowChange.put("col_long", ColumnValue.fromLong(1));

        /**
         * 删除某列
         */
        rowChange.deleteColumns("col_to_delete");

        /**
         * 构造UpdateRowRequest
         */
        UpdateRowRequest updateRowRequest = new UpdateRowRequest(rowChange);

        /**
         * 调用updateRow接口。若之前该行不存在，系统会新增该行。
         */
        UpdateRowResponse updateRowResponse = syncClient.updateRow(updateRowRequest);

        /**
         * 打印requestID
         */
        System.out.printf("UpdateRowSuccess, request id: %s\n", updateRowResponse.getRequestId());
    }

    public void updateRowMultiVersion() {
        PrimaryKey primaryKey = PrimaryKeyBuilder.createPrimaryKeyBuilder()
                .addPrimaryKeyColumn(PK1, PrimaryKeyValue.fromLong(1L))
                .addPrimaryKeyColumn(PK2, PrimaryKeyValue.fromString("string"))
                .build();
        /**
         * 构造RowUpdateChange，设置表名和主键
         */
        RowUpdateChange rowChange = new RowUpdateChange(TABLE_NAME, primaryKey);

        long version = System.currentTimeMillis();

        /**
         * 写入两列，指定版本号。
         * 若指定的版本之前不存在，则会新增一个版本；若该版本已存在，会修改该版本的值。
         */
        rowChange.put("col_str", ColumnValue.fromString("value1"), version);
        rowChange.put("col_long", ColumnValue.fromLong(1), version);

        /**
         * 删除某列的某一个版本，指定版本号。
         */
        rowChange.deleteColumn("col_to_delete", version);

        /**
         * 构造UpdateRowRequest
         */
        UpdateRowRequest updateRowRequest = new UpdateRowRequest(rowChange);

        /**
         * 调用updateRow接口。若之前该行不存在，系统会新增该行。
         */
        UpdateRowResponse updateRowResponse = syncClient.updateRow(updateRowRequest);

        /**
         * 打印requestID
         */
        System.out.printf("UpdateRowSuccess, request id: %s\n", updateRowResponse.getRequestId());
    }

    public void updateRowWithCondition() {
        PrimaryKey primaryKey = PrimaryKeyBuilder.createPrimaryKeyBuilder()
                .addPrimaryKeyColumn(PK1, PrimaryKeyValue.fromLong(1L))
                .addPrimaryKeyColumn(PK2, PrimaryKeyValue.fromString("string"))
                .build();
        /**
         * 构造RowUpdateChange，设置表名和主键
         */
        RowUpdateChange rowChange = new RowUpdateChange(TABLE_NAME, primaryKey);

        /**
         * 设置行存在条件为期望行存在
         */
        Condition condition = new Condition(RowExistenceExpectation.EXPECT_EXIST);

        /**
         * 设置列条件，若只需要检查行存在性，可以不设置列条件。
         *
         * 这里设置列条件为两列的组合条件： "(col_boolean == true) && (col_long > 0)"
         */
        CompositeColumnValueCondition colCondition = new CompositeColumnValueCondition(CompositeColumnValueCondition.LogicOperator.AND);
        SingleColumnValueCondition subColCondition1 = new SingleColumnValueCondition(
                "col_boolean",
                SingleColumnValueCondition.CompareOperator.EQUAL,
                ColumnValue.fromBoolean(true));
        subColCondition1.setPassIfMissing(true); // setPassIfMissing(true)，表示若该列不存在，也视为满足条件。
        SingleColumnValueCondition subColCondition2 = new SingleColumnValueCondition(
                "col_long",
                SingleColumnValueCondition.CompareOperator.GREATER_THAN,
                ColumnValue.fromLong(0L));
        colCondition.addCondition(subColCondition1).addCondition(subColCondition2);
        subColCondition2.setPassIfMissing(false); // setPassIfMissing(false)，表示若该列不存在，视为不满足条件。

        condition.setColumnCondition(colCondition);
        rowChange.setCondition(condition);

        /**
         * 满足条件时，写入两列
         */
        rowChange.put("col_str", ColumnValue.fromString("value1"));
        rowChange.put("col_long", ColumnValue.fromLong(1));

        /**
         * 构造UpdateRowRequest
         */
        UpdateRowRequest updateRowRequest = new UpdateRowRequest(rowChange);

        /**
         * 调用updateRow接口。
         * 若不满足设置的条件，比如该行不存在，或者不满足列条件，会抛OTSException，ErrorCode为"OTSConditionCheckFail".
         */
        UpdateRowResponse updateRowResponse = syncClient.updateRow(updateRowRequest);

        /**
         * 打印requestID
         */
        System.out.printf("UpdateRowSuccess, request id: %s\n", updateRowResponse.getRequestId());
    }

    public void updateRowIncrement() {
        PrimaryKey primaryKey = PrimaryKeyBuilder.createPrimaryKeyBuilder()
                .addPrimaryKeyColumn(PK1, PrimaryKeyValue.fromLong(1L))
                .addPrimaryKeyColumn(PK2, PrimaryKeyValue.fromString("string"))
                .build();
        /**
         * 构造RowUpdateChange，设置表名和主键
         */
        RowUpdateChange rowChange = new RowUpdateChange(TABLE_NAME, primaryKey);

        String columnName = "col_long";
        /**
         * 对col_long这一列进行原子加100操作。
         * 若该列之前不存在，会从0开始累加。
         */
        rowChange.increment(new Column(columnName, ColumnValue.fromLong(100)));

        /**
         * 设置返回修改后的该列值。
         */
        rowChange.setReturnType(ReturnType.RT_AFTER_MODIFY);
        rowChange.addReturnColumn(columnName);

        /**
         * 构造UpdateRowRequest
         */
        UpdateRowRequest updateRowRequest = new UpdateRowRequest(rowChange);

        /**
         * 调用updateRow接口。若之前该行不存在，系统会新增该行。
         */
        UpdateRowResponse updateRowResponse = syncClient.updateRow(updateRowRequest);

        /**
         * 打印修改后的该列的值和RequestId
         */
        System.out.printf("UpdateRowSuccess, column [%s] was updated to %d, request id: %s\n",
                columnName,
                updateRowResponse.getRow().getLatestColumn(columnName).getValue().asLong(),
                updateRowResponse.getRequestId());
    }
}
