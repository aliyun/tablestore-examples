package com.aliyun.tablestore.basic;

import com.alicloud.openservices.tablestore.model.*;
import com.alicloud.openservices.tablestore.model.filter.ColumnPaginationFilter;
import com.alicloud.openservices.tablestore.model.filter.SingleColumnValueFilter;
import com.aliyun.tablestore.basic.dataManage.base.BaseManage;

import static com.aliyun.tablestore.basic.common.Consts.*;

/**
 * @Author boxiao
 * @create 2019/12/06
 */
public class GetRowExample extends BaseManage {

    public static void main(String[] args) {
        GetRowExample manage = new GetRowExample();

        manage.prepareData();
        manage.getRow();
        manage.getMultiVersionRow();
//        manage.getWideRow();
//        manage.getRowWithFilter();

        boolean wantDeleteTable = false;
        manage.close(wantDeleteTable);          // delete table when close
    }

    public void prepareData(){
        putRow();
        //对同一行更新10次，产生10个版本
        for (int i = 0; i < 10; i++) {
            updateRow();
        }

    }

    private void putRow() {
        PrimaryKey primaryKey = PrimaryKeyBuilder.createPrimaryKeyBuilder()
                .addPrimaryKeyColumn(PK1, PrimaryKeyValue.fromLong(1L))
                .addPrimaryKeyColumn(PK2, PrimaryKeyValue.fromString("string"))
                .build();

        RowPutChange rowChange = new RowPutChange(TABLE_NAME, primaryKey);
        //写入10列
        for (int i = 0; i < 10; i++) {
            rowChange.addColumn("col" + i, ColumnValue.fromLong(i));
        }

        PutRowRequest putRowRequest = new PutRowRequest(rowChange);

        syncClient.putRow(putRowRequest);

        System.out.println(String.format("PutRowSuccess: \n\tPrimaryKey: %s\n\tColumns: %s",
                rowChange.getPrimaryKey().toString(),
                rowChange.getColumnsToPut().toString()));
    }

    private void updateRow() {
        // 构造主键
        PrimaryKey primaryKey = PrimaryKeyBuilder.createPrimaryKeyBuilder()
                .addPrimaryKeyColumn(PK1, PrimaryKeyValue.fromLong(1L))
                .addPrimaryKeyColumn(PK2, PrimaryKeyValue.fromString("string"))
                .build();

        RowUpdateChange rowUpdateChange = new RowUpdateChange(TABLE_NAME, primaryKey);

        // 更新一些列
        for (int i = 0; i < 10; i++) {
            rowUpdateChange.put(new Column("col" + i, ColumnValue.fromLong(i)));
        }
        syncClient.updateRow(new UpdateRowRequest(rowUpdateChange));
    }


    public void getRow() {
        //构造主键，主键列必须全部指定
        PrimaryKeyBuilder primaryKeyBuilder = PrimaryKeyBuilder.createPrimaryKeyBuilder();
        primaryKeyBuilder.addPrimaryKeyColumn(PK1, PrimaryKeyValue.fromLong(1L));
        primaryKeyBuilder.addPrimaryKeyColumn(PK2, PrimaryKeyValue.fromString("string"));
        PrimaryKey primaryKey = primaryKeyBuilder.build();

        SingleRowQueryCriteria criteria = new SingleRowQueryCriteria(TABLE_NAME, primaryKey);
        //设置读取的版本数为1，即读取最新版本
        criteria.setMaxVersions(1);

        GetRowRequest getRowRequest = new GetRowRequest(criteria);
        GetRowResponse getRowResponse = syncClient.getRow(getRowRequest);
        Row row = getRowResponse.getRow();

        System.out.println("读取行完毕, 结果为: ");
        System.out.println(row);
    }

    public void getMultiVersionRow() {
        // 构造主键
        PrimaryKeyBuilder primaryKeyBuilder = PrimaryKeyBuilder.createPrimaryKeyBuilder();
        primaryKeyBuilder.addPrimaryKeyColumn(PK1, PrimaryKeyValue.fromLong(1L));
        primaryKeyBuilder.addPrimaryKeyColumn(PK2, PrimaryKeyValue.fromString("string"));
        PrimaryKey primaryKey = primaryKeyBuilder.build();

        // 读一行
        SingleRowQueryCriteria criteria = new SingleRowQueryCriteria(TABLE_NAME, primaryKey);
        // 设置读取版本数量
        criteria.setMaxVersions(10);
        //通过版本号范围读取
//        TimeRange timeRange = new TimeRange(1418380771, 1418390771);
//        criteria.setTimeRange(timeRange);

        GetRowResponse getRowResponse = syncClient.getRow(new GetRowRequest(criteria));
        Row row = getRowResponse.getRow();

        System.out.println("读取完毕, 结果为: ");
        System.out.println(row);
    }

    public void getWideRow() {
        // 构造主键
        PrimaryKeyBuilder primaryKeyBuilder = PrimaryKeyBuilder.createPrimaryKeyBuilder();
        primaryKeyBuilder.addPrimaryKeyColumn(PK1, PrimaryKeyValue.fromLong(1L));
        primaryKeyBuilder.addPrimaryKeyColumn(PK2, PrimaryKeyValue.fromString("string"));
        PrimaryKey primaryKey = primaryKeyBuilder.build();

        // 读一行
        SingleRowQueryCriteria criteria = new SingleRowQueryCriteria(TABLE_NAME, primaryKey);
        // 设置读取最新版本
        criteria.setMaxVersions(1);

        // 1.设置读取某些列
        criteria.addColumnsToGet(new String[]{"col1", "col2", "col3"});

//        //2.使用startColumn和endColumn读取一定范围的列
//        criteria.setStartColumn("col0");
//        criteria.setEndColumn("col3");

//        //3.使用ColumnPaginationFilter配合startColumn翻页读取
//        // 设置从col0开始读
//        criteria.setStartColumn("col0");
//        // 使用ColumnPaginationFilter设置一次要读取的列数, limit=5, offset=0
//        criteria.setFilter(new ColumnPaginationFilter(5, 0));

        GetRowResponse getRowResponse = syncClient.getRow(new GetRowRequest(criteria));
        Row row = getRowResponse.getRow();

        System.out.println("读取完毕, 结果为: ");
        System.out.println(row);
    }


    public void getRowWithFilter() {
        // 构造主键
        PrimaryKeyBuilder primaryKeyBuilder = PrimaryKeyBuilder.createPrimaryKeyBuilder();
        primaryKeyBuilder.addPrimaryKeyColumn(PK1, PrimaryKeyValue.fromLong(1L));
        primaryKeyBuilder.addPrimaryKeyColumn(PK2, PrimaryKeyValue.fromString("string"));
        PrimaryKey primaryKey = primaryKeyBuilder.build();

        // 读一行
        SingleRowQueryCriteria criteria = new SingleRowQueryCriteria(TABLE_NAME, primaryKey);
        // 设置读取最新版本
        criteria.setMaxVersions(1);

        //1.单条件过滤器：SingleColumnValueFilter
        // 设置过滤器, 当 col0 的值为 0 时返回该行。
        SingleColumnValueFilter singleColumnValueFilter = new SingleColumnValueFilter("col0",
                SingleColumnValueFilter.CompareOperator.EQUAL, ColumnValue.fromLong(0));
        // 如果不存在 col0 这一列, 也不返回。
        singleColumnValueFilter.setPassIfMissing(false);

//        //2.多条件过滤：CompositeColumnValueFilter
//        // composite1 条件为 (col0 == 0) AND (Col1 > 100)
//        CompositeColumnValueFilter composite1 = new CompositeColumnValueFilter(CompositeColumnValueFilter.LogicOperator.AND);
//        SingleColumnValueFilter single1 = new SingleColumnValueFilter("col0",
//                SingleColumnValueFilter.CompareOperator.EQUAL, ColumnValue.fromLong(0));
//        SingleColumnValueFilter single2 = new SingleColumnValueFilter("Col1",
//                SingleColumnValueFilter.CompareOperator.GREATER_THAN, ColumnValue.fromLong(100));
//        composite1.addFilter(single1);
//        composite1.addFilter(single2);
//
//        // composite2 条件为 ( (col0 == 0) AND (Col1 > 100) ) OR (Col2 <= 10)
//        CompositeColumnValueFilter composite2 = new CompositeColumnValueFilter(CompositeColumnValueFilter.LogicOperator.OR);
//        SingleColumnValueFilter single3 = new SingleColumnValueFilter("Col2",
//                SingleColumnValueFilter.CompareOperator.LESS_EQUAL, ColumnValue.fromLong(10));
//        composite2.addFilter(composite1);
//        composite2.addFilter(single3);

        criteria.setFilter(singleColumnValueFilter);


        GetRowResponse getRowResponse = syncClient.getRow(new GetRowRequest(criteria));
        Row row = getRowResponse.getRow();

        System.out.println("读取完毕, 结果为: ");
        System.out.println(row);
    }

}