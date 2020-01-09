package com.aliyun.tablestore.basic;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.alicloud.openservices.tablestore.SyncClient;
import com.alicloud.openservices.tablestore.model.CapacityUnit;
import com.alicloud.openservices.tablestore.model.ColumnValue;
import com.alicloud.openservices.tablestore.model.CreateTableRequest;
import com.alicloud.openservices.tablestore.model.DefinedColumnSchema;
import com.alicloud.openservices.tablestore.model.DefinedColumnType;
import com.alicloud.openservices.tablestore.model.DeleteTableRequest;
import com.alicloud.openservices.tablestore.model.DescribeTableRequest;
import com.alicloud.openservices.tablestore.model.DescribeTableResponse;
import com.alicloud.openservices.tablestore.model.IndexMeta;
import com.alicloud.openservices.tablestore.model.ListTableResponse;
import com.alicloud.openservices.tablestore.model.PrimaryKey;
import com.alicloud.openservices.tablestore.model.PrimaryKeyBuilder;
import com.alicloud.openservices.tablestore.model.PrimaryKeyOption;
import com.alicloud.openservices.tablestore.model.PrimaryKeySchema;
import com.alicloud.openservices.tablestore.model.PrimaryKeyType;
import com.alicloud.openservices.tablestore.model.PrimaryKeyValue;
import com.alicloud.openservices.tablestore.model.PutRowRequest;
import com.alicloud.openservices.tablestore.model.ReservedThroughput;
import com.alicloud.openservices.tablestore.model.ReservedThroughputDetails;
import com.alicloud.openservices.tablestore.model.RowPutChange;
import com.alicloud.openservices.tablestore.model.StreamDetails;
import com.alicloud.openservices.tablestore.model.TableMeta;
import com.alicloud.openservices.tablestore.model.TableOptions;
import com.alicloud.openservices.tablestore.model.UpdateTableRequest;
import com.aliyun.tablestore.basic.common.Config;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class TableManage {
    private static SyncClient client;
    private static String TABLE_NAME = "table_test";

    @BeforeClass
    public static void beforeClass() {
        client = Config.newInstance().newClient();
    }

    @AfterClass
    public static void afterClass() {
        if (client != null) {
            client.shutdown();
        }
    }

    @Test
    // 创建普通表（不使用索引等功能）
    public void createTable() {
        TableMeta tableMeta = new TableMeta(TABLE_NAME);
        // 为主表添加主键列。
        tableMeta.addPrimaryKeyColumn(new PrimaryKeySchema("pk_1", PrimaryKeyType.STRING));
        tableMeta.addPrimaryKeyColumn(new PrimaryKeySchema("pk_2", PrimaryKeyType.INTEGER));
        tableMeta.addPrimaryKeyColumn(new PrimaryKeySchema("pk_3", PrimaryKeyType.BINARY));
        // 设置该主键为自增列
        tableMeta.addPrimaryKeyColumn(new PrimaryKeySchema("pk_4", PrimaryKeyType.INTEGER, PrimaryKeyOption.AUTO_INCREMENT));

        // 数据的过期时间，单位秒, -1代表永不过期，例如设置过期时间为一年, 即为 365 * 24 * 3600。
        int timeToLive = -1;
        // 保存的最大版本数，设置为3即代表每列上最多保存3个最新的版本。（如果使用索引，maxVersions只能等于1）
        int maxVersions = 3;

        TableOptions tableOptions = new TableOptions(timeToLive, maxVersions);
        CreateTableRequest request = new CreateTableRequest(tableMeta, tableOptions);
        // 设置读写预留值，容量型实例只能设置为0，高性能实例可以设置为非零值。
        request.setReservedThroughput(new ReservedThroughput(new CapacityUnit(0, 0)));
        client.createTable(request);
    }


    @Test
    // 主键自增
    public void createTableWithPKAutoIncrement() {
        TableMeta tableMeta = new TableMeta(TABLE_NAME);
        tableMeta.addPrimaryKeyColumn(new PrimaryKeySchema("pk_1", PrimaryKeyType.STRING));
        // 设置该主键为自增列
        tableMeta.addPrimaryKeyColumn(new PrimaryKeySchema("pk_auto_increment", PrimaryKeyType.INTEGER, PrimaryKeyOption.AUTO_INCREMENT));
        tableMeta.addPrimaryKeyColumn(new PrimaryKeySchema("pk_3", PrimaryKeyType.INTEGER));

        TableOptions tableOptions = new TableOptions(-1, 1);
        CreateTableRequest request = new CreateTableRequest(tableMeta, tableOptions);
        client.createTable(request);
    }

    @Test
    // 自增列的数据插入
    public void putRow(){
        PrimaryKey primaryKey = PrimaryKeyBuilder.createPrimaryKeyBuilder()
            .addPrimaryKeyColumn("pk_1", PrimaryKeyValue.fromString("test1"))
            .addPrimaryKeyColumn("pk_auto_increment", PrimaryKeyValue.AUTO_INCREMENT)
            .addPrimaryKeyColumn("pk_3", PrimaryKeyValue.fromLong(100))
            .build();
        RowPutChange rowPutChange = new RowPutChange(TABLE_NAME , primaryKey);
        rowPutChange.addColumn("attr_1", ColumnValue.fromLong(100));
        PutRowRequest request = new PutRowRequest(rowPutChange);
        client.putRow(request);
    }


    @Test
    // 创建普通表, 同时创建二级索引
    public void createTableWithIndex() {
        TableMeta tableMeta = new TableMeta(TABLE_NAME);
        // 为主表添加主键列。
        tableMeta.addPrimaryKeyColumn(new PrimaryKeySchema("pk_1", PrimaryKeyType.STRING));
        tableMeta.addPrimaryKeyColumn(new PrimaryKeySchema("pk_2", PrimaryKeyType.INTEGER));
        tableMeta.addPrimaryKeyColumn(new PrimaryKeySchema("pk_3", PrimaryKeyType.BINARY));
        // 设置该主键列式自增列
        tableMeta.addPrimaryKeyColumn(new PrimaryKeySchema("pk_4", PrimaryKeyType.INTEGER, PrimaryKeyOption.AUTO_INCREMENT));

        // 为主表添加预定义列。
        String definedColName1 = "defined_col_1";
        String definedColName2 = "defined_col_2";
        String definedColName3 = "defined_col_3";
        String definedColName4 = "defined_col_4";
        String definedColName5 = "defined_col_5";
        tableMeta.addDefinedColumn(new DefinedColumnSchema(definedColName1, DefinedColumnType.INTEGER));
        tableMeta.addDefinedColumn(new DefinedColumnSchema(definedColName2, DefinedColumnType.DOUBLE));
        tableMeta.addDefinedColumn(new DefinedColumnSchema(definedColName3, DefinedColumnType.BOOLEAN));
        tableMeta.addDefinedColumn(new DefinedColumnSchema(definedColName4, DefinedColumnType.BINARY));
        tableMeta.addDefinedColumn(new DefinedColumnSchema(definedColName5, DefinedColumnType.STRING));

        ArrayList<IndexMeta> indexMetas = new ArrayList<IndexMeta>();
        IndexMeta indexMeta = new IndexMeta("INDEX_NAME");
        // 为索引表添加主键列。
        indexMeta.addPrimaryKeyColumn(definedColName1);
        indexMeta.addPrimaryKeyColumn(definedColName3);
        indexMeta.addPrimaryKeyColumn(definedColName4);
        indexMeta.addPrimaryKeyColumn("pk_3");
        // 为索引表添加属性列。
        indexMeta.addDefinedColumn(definedColName2);
        indexMeta.addDefinedColumn(definedColName5);

        indexMetas.add(indexMeta);

        // 数据的过期时间，单位秒。 使用索引功能时候，需要value=-1
        int timeToLive = -1;
        // 保存的最大版本数。 使用索引功能时候，需要value=1
        int maxVersions = 1;
        TableOptions tableOptions = new TableOptions(timeToLive, maxVersions);
        CreateTableRequest request = new CreateTableRequest(tableMeta, tableOptions, indexMetas);
        // 设置读写预留值，容量型实例只能设置为0，高性能实例可以设置为非零值。
        request.setReservedThroughput(new ReservedThroughput(new CapacityUnit(0, 0)));
        client.createTable(request);
    }

    @Test
    public void deleteTable() {
        DeleteTableRequest request = new DeleteTableRequest(TABLE_NAME);
        client.deleteTable(request);
    }

    @Test
    public void listTable() {
        ListTableResponse response = client.listTable();
        System.out.println("表的列表如下：");
        for (String tableName : response.getTableNames()) {
            System.out.println(tableName);
        }
    }

    @Test
    public void updateTable() {
        UpdateTableRequest request = new UpdateTableRequest(TABLE_NAME);
        // 修改预留吞吐
        request.setReservedThroughputForUpdate(new ReservedThroughput(new CapacityUnit(0, 0)));
        // 修改表的最大保留版本、TTL等
        request.setTableOptionsForUpdate(new TableOptions(-1, 1));
        client.updateTable(request);
    }

    @Test
    public void updateTTL() {
        UpdateTableRequest request = new UpdateTableRequest(TABLE_NAME);
        int ttl = -1;
        request.setTableOptionsForUpdate(new TableOptions(ttl));
        client.updateTable(request);
    }

    @Test
    public void describeTable() {
        DescribeTableRequest request = new DescribeTableRequest(TABLE_NAME);
        DescribeTableResponse response = client.describeTable(request);
        TableMeta tableMeta = response.getTableMeta();
        System.out.println("表的名称：" + tableMeta.getTableName());

        System.out.println("表的主键：");
        for (PrimaryKeySchema schema : tableMeta.getPrimaryKeyList()) {
            System.out.println("\t主键名字:" + schema.getName() + "\t主键类型:" + schema.getType()
                + "\t自增列:" + (schema.getOption() == null ? "false" : schema.getOption().equals(PrimaryKeyOption.AUTO_INCREMENT)));
        }

        System.out.println("预定义列信息：");
        for (DefinedColumnSchema schema : tableMeta.getDefinedColumnsList()) {
            System.out.println("\t主键名字:" + schema.getName() + "\t主键类型:" + schema.getType());
        }

        System.out.println("二级索引信息：");
        for (IndexMeta meta : response.getIndexMeta()) {
            System.out.println("\t索引名字:" + meta.getIndexName());
            System.out.println("\t\t索引类型:" + meta.getIndexType());
            System.out.println("\t\t索引主键:" + meta.getPrimaryKeyList());
            System.out.println("\t\t索引预定义列:" + meta.getDefinedColumnsList());
        }

        TableOptions tableOptions = response.getTableOptions();
        System.out.println("TableOptions:");
        System.out.println("\t表的TTL:" + tableOptions.getTimeToLive());
        System.out.println("\t表的MaxVersions:" + tableOptions.getMaxVersions());

        ReservedThroughputDetails rtd = response.getReservedThroughputDetails();
        System.out.println("预留吞吐量:");
        System.out.println("\t读：" + rtd.getCapacityUnit().getReadCapacityUnit());
        System.out.println("\t写：" + rtd.getCapacityUnit().getWriteCapacityUnit());
        System.out.println("\t最近上调时间: " + new Date(rtd.getLastIncreaseTime() * 1000));
        System.out.println("\t最近下调时间: " + new Date(rtd.getLastDecreaseTime() * 1000));

        List<PrimaryKey> shardSplits = response.getShardSplits();
        System.out.println("表分区信息:");
        for (PrimaryKey primaryKey : shardSplits) {
            System.out.println("\t分裂点信息: " + primaryKey.getPrimaryKeyColumnsMap());
        }

        StreamDetails streamDetails = response.getStreamDetails();
        System.out.println("stream功能相关信息:");
        System.out.println("\t是否开启stream功能: " + streamDetails.isEnableStream());
        if (streamDetails.isEnableStream()) {
            System.out.println("\tstream id: " + streamDetails.getStreamId());
            System.out.println("\t获取Stream的过期时间（小时）: " + streamDetails.getExpirationTime());
            System.out.println("\t上次开启Stream的时间: " + new Date(streamDetails.getLastEnableTime()/1000));
        }
    }
}
