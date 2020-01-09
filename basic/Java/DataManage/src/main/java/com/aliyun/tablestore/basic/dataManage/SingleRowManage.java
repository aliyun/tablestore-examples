package com.aliyun.tablestore.basic.dataManage;

import com.alicloud.openservices.tablestore.model.*;
import com.alicloud.openservices.tablestore.model.condition.ColumnCondition;
import com.alicloud.openservices.tablestore.model.condition.CompositeColumnValueCondition;
import com.alicloud.openservices.tablestore.model.condition.SingleColumnValueCondition;
import com.aliyun.tablestore.basic.dataManage.base.BaseManage;

import java.util.Arrays;
import java.util.Collections;

import static com.aliyun.tablestore.basic.common.Consts.*;

/**
 * @Author wtt
 * @create 2019/11/28 12:19 PM
 */
public class SingleRowManage extends BaseManage {
    private long timestamp = System.currentTimeMillis();

    public static void main(String[] args) {
        SingleRowManage manage = new SingleRowManage();

        manage.putRow();
        manage.getRow();

        manage.updateRow();
        manage.getRow();

        manage.deleteRow();
        manage.getRow();


        boolean wantDeleteTable = false;
        manage.close(wantDeleteTable);          // delete table when close
    }

    public void putRow() {
        PrimaryKey primaryKey = PrimaryKeyBuilder.createPrimaryKeyBuilder()
                .addPrimaryKeyColumn(PK1, PrimaryKeyValue.fromLong(1L))
                .addPrimaryKeyColumn(PK2, PrimaryKeyValue.fromString("string"))
                .build();

        RowPutChange rowChange = new RowPutChange(TABLE_NAME, primaryKey);
        rowChange.addColumn("string", ColumnValue.fromString("string value"))
                .addColumn("long", ColumnValue.fromLong(1L))
                .addColumn("boolean", ColumnValue.fromBoolean(true))
                .addColumn("double", ColumnValue.fromDouble(1.1), timestamp)
                .addColumn("binary", ColumnValue.fromBinary(new byte[]{1, 2, 3}));

//        rowChange.setCondition(new Condition(RowExistenceExpectation.EXPECT_NOT_EXIST));  // 条件插入（期望：行存在或不存在）

        PutRowRequest putRowRequest = new PutRowRequest(rowChange);

        syncClient.putRow(putRowRequest);

        System.out.println(String.format("PutRowSuccess: \n\tPrimaryKey: %s\n\tColumns: %s",
                rowChange.getPrimaryKey().toString(),
                rowChange.getColumnsToPut().toString()));
    }


    public void getRow() {
        PrimaryKey primaryKey = PrimaryKeyBuilder.createPrimaryKeyBuilder()
                .addPrimaryKeyColumn(PK1, PrimaryKeyValue.fromLong(1L))
                .addPrimaryKeyColumn(PK2, PrimaryKeyValue.fromString("string"))
                .build();

        SingleRowQueryCriteria criteria = new SingleRowQueryCriteria(TABLE_NAME, primaryKey);
        criteria.setMaxVersions(1);

        GetRowRequest getRowRequest = new GetRowRequest(criteria);

        GetRowResponse getRowResponse = syncClient.getRow(getRowRequest);

        if (getRowResponse.getRow() == null) {
            System.out.println(String.format("Get Row Not Exist: \n\tPrimaryKey: %s",
                    primaryKey.toString()));
        } else {
            System.out.println(String.format("Get Row Success: \n\tPrimaryKey: %s\n\tColumns: %s",
                    getRowResponse.getRow().getPrimaryKey().toString(),
                    Arrays.asList(getRowResponse.getRow().getColumns()).toString()));
        }
    }

    public void updateRow() {
        PrimaryKey primaryKey = PrimaryKeyBuilder.createPrimaryKeyBuilder()
                .addPrimaryKeyColumn(PK1, PrimaryKeyValue.fromLong(1L))
                .addPrimaryKeyColumn(PK2, PrimaryKeyValue.fromString("string"))
                .build();

        RowUpdateChange rowChange = new RowUpdateChange(TABLE_NAME, primaryKey);
        rowChange
                .deleteColumns("binary")										        // 删除指定列所有版本
                .deleteColumn("double", timestamp)								// 删除指定列指定版本
                .put("string", ColumnValue.fromString("new string value"))		// 更新已存在列
                .put("newCol", ColumnValue.fromString("new col")) 				// 增加新列
                .increment(new Column("long", ColumnValue.fromLong(100L)));		// 原子加

        rowChange.setReturnType(ReturnType.RT_AFTER_MODIFY);                            // 获取属性列原子加修改后的值
        rowChange.addReturnColumn("long");

        /**
         * 单列条件过滤：支持等于、大于、大于等于、小于、小于等于
         */
//        Condition condition = new Condition();
//        condition.setColumnCondition(
//                new SingleColumnValueCondition(
//                        "boolean",
//                        SingleColumnValueCondition.CompareOperator.EQUAL,
//                        ColumnValue.fromBoolean(true)));
//
//        rowChange.setCondition(condition);

        /**
         * 复合条件过滤：支持与、或、非
         */
//        CompositeColumnValueCondition colCondition = new CompositeColumnValueCondition(CompositeColumnValueCondition.LogicOperator.AND);
//
//        ColumnCondition subColCondition1 = new SingleColumnValueCondition(
//                "boolean",
//                SingleColumnValueCondition.CompareOperator.EQUAL,
//                ColumnValue.fromBoolean(true));
//
//        ColumnCondition subColCondition2 = new SingleColumnValueCondition(
//                "double",
//                SingleColumnValueCondition.CompareOperator.GREATER_THAN,
//                ColumnValue.fromLong(0L));
//
//        colCondition.addCondition(subColCondition1).addCondition(subColCondition2);
//        Condition condition = new Condition();
//        condition.setColumnCondition(colCondition);
//
//        rowChange.setCondition(condition);



        UpdateRowRequest updateRowRequest = new UpdateRowRequest(rowChange);


        UpdateRowResponse updateRowResponse = syncClient.updateRow(updateRowRequest);
        System.out.println(String.format("UpdateRowSuccess: \n\tPrimaryKey: %s",
                updateRowRequest.getRowChange().getPrimaryKey().toString()));
    }

    public void deleteRow() {
        PrimaryKey primaryKey = PrimaryKeyBuilder.createPrimaryKeyBuilder()
                .addPrimaryKeyColumn(PK1, PrimaryKeyValue.fromLong(1L))
                .addPrimaryKeyColumn(PK2, PrimaryKeyValue.fromString("string"))
                .build();

        RowDeleteChange rowChange = new RowDeleteChange(TABLE_NAME, primaryKey);
        DeleteRowRequest getRowRequest = new DeleteRowRequest(rowChange);

        syncClient.deleteRow(getRowRequest);
        System.out.println(String.format("DeleteRowSuccess: \n\tPrimaryKey: %s",
                primaryKey.toString()));
    }
}
