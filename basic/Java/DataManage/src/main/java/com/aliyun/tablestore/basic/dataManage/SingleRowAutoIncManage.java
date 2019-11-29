package com.aliyun.tablestore.basic.dataManage;

import com.alicloud.openservices.tablestore.model.*;
import com.aliyun.tablestore.basic.dataManage.base.BaseAutoIncManage;
import com.aliyun.tablestore.basic.dataManage.base.BaseManage;

import java.util.Arrays;

import static com.aliyun.tablestore.basic.common.Consts.*;

/**
 * @Author wtt
 * @create 2019/11/28 12:19 PM
 */
public class SingleRowAutoIncManage extends BaseAutoIncManage {

    public static void main(String[] args) {
        SingleRowAutoIncManage manage = new SingleRowAutoIncManage();

        manage.putRow();

        boolean wantDeleteTable = false;
        manage.close(wantDeleteTable);          // delete table when close
    }

    public void putRow() {
        PrimaryKey primaryKey = PrimaryKeyBuilder.createPrimaryKeyBuilder()
                .addPrimaryKeyColumn(PK1, PrimaryKeyValue.fromLong(1L))
                .addPrimaryKeyColumn(PK2, PrimaryKeyValue.fromString("string"))
                .addPrimaryKeyColumn(PK3, PrimaryKeyValue.AUTO_INCREMENT)       // 自增列的特殊列值
                .build();

        RowPutChange rowChange = new RowPutChange(TABLE_NAME_INC, primaryKey);
        rowChange.addColumn("string", ColumnValue.fromString("string value"));

        rowChange.setReturnType(ReturnType.RT_PK);                              // 获取主键自增列实际插入的值

        PutRowRequest putRowRequest = new PutRowRequest(rowChange);

        PutRowResponse putRowResponse = syncClient.putRow(putRowRequest);

        // 自增列的实际存储值只有在返回的Response中才能拿到
        PrimaryKey returnPrimaryKey = putRowResponse.getRow().getPrimaryKey();

        System.out.println(String.format("PutRowSuccess: \n\tPrimaryKey: %s",
                returnPrimaryKey.toString()));
    }
}
