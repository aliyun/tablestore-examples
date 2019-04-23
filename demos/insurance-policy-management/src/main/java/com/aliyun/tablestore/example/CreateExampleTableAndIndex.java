package com.aliyun.tablestore.example;

import com.alicloud.openservices.tablestore.model.CreateTableRequest;
import com.alicloud.openservices.tablestore.model.PrimaryKeyType;
import com.alicloud.openservices.tablestore.model.TableMeta;
import com.alicloud.openservices.tablestore.model.TableOptions;
import com.alicloud.openservices.tablestore.model.search.CreateSearchIndexRequest;
import com.alicloud.openservices.tablestore.model.search.FieldSchema;
import com.alicloud.openservices.tablestore.model.search.FieldType;
import com.alicloud.openservices.tablestore.model.search.IndexSchema;
import com.aliyun.tablestore.example.utils.ClientAndConfig;
import com.aliyun.tablestore.example.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import static com.aliyun.tablestore.example.consts.ColumnConsts.*;

/**
 * @author hydrogen
 */
public class CreateExampleTableAndIndex extends BaseExample {

    public CreateExampleTableAndIndex(ClientAndConfig clientAndConfig) {
        super(clientAndConfig);
    }


    public static void main(String[] args) {
        ClientAndConfig clientAndConfig = Utils.getClientAndConfig(args);
        new CreateExampleTableAndIndex(clientAndConfig).main();
    }

    private void createTable() {
        // Create table
        TableMeta tableMeta = new TableMeta(tableName);
        // Only one primary key column: policy_id_md5 with type String
        tableMeta.addPrimaryKeyColumn(POLICY_ID_MD5, PrimaryKeyType.STRING);
        // Set TTL to -1, never expire; Set maxVersions 1, only have one version per column
        TableOptions tableOptions = new TableOptions(-1, 1);
        CreateTableRequest createTableRequest = new CreateTableRequest(tableMeta, tableOptions);
        syncClient.createTable(createTableRequest);
        System.out.println("Create table success");
    }

    private void createSearchIndex() {
        // Create SearchIndex
        CreateSearchIndexRequest createRequest = new CreateSearchIndexRequest(tableName, indexName);
        IndexSchema indexSchema = new IndexSchema();
        indexSchema.addFieldSchema(new FieldSchema(POLICY_ID, FieldType.KEYWORD));

        indexSchema.addFieldSchema(new FieldSchema(PRODUCT_NAME, FieldType.TEXT).setAnalyzer(FieldSchema.Analyzer.MaxWord));
        indexSchema.addFieldSchema(new FieldSchema(OPERATE_TIME, FieldType.LONG).setEnableSortAndAgg(true));
        indexSchema.addFieldSchema(new FieldSchema(EFFECTIVE_TIME, FieldType.LONG).setEnableSortAndAgg(true));
        indexSchema.addFieldSchema(new FieldSchema(EXPIRATION_TIME, FieldType.LONG).setEnableSortAndAgg(true));

        indexSchema.addFieldSchema(new FieldSchema(APPLIER_USER_ID, FieldType.KEYWORD));
        indexSchema.addFieldSchema(new FieldSchema(BROKER_USER_ID, FieldType.KEYWORD));

        indexSchema.addFieldSchema(new FieldSchema(APPLIER_NAME, FieldType.KEYWORD));
        indexSchema.addFieldSchema(new FieldSchema(APPLIER_ID, FieldType.KEYWORD));
        indexSchema.addFieldSchema(new FieldSchema(APPLIER_GENDER, FieldType.KEYWORD));

        indexSchema.addFieldSchema(new FieldSchema(INSURED_NAME, FieldType.KEYWORD));
        indexSchema.addFieldSchema(new FieldSchema(INSURED_ID, FieldType.KEYWORD));
        indexSchema.addFieldSchema(new FieldSchema(INSURED_GENDER, FieldType.KEYWORD));

        List<FieldSchema> beneficiaryInfoSchema = new ArrayList<>();
        beneficiaryInfoSchema.add(new FieldSchema(NAME, FieldType.KEYWORD));
        beneficiaryInfoSchema.add(new FieldSchema(ID, FieldType.KEYWORD));
        beneficiaryInfoSchema.add(new FieldSchema(GENDER, FieldType.KEYWORD));
        beneficiaryInfoSchema.add(new FieldSchema(BENEFIT_PERCENTAGE, FieldType.LONG));

        indexSchema.addFieldSchema(new FieldSchema(BENEFICIARY_INFO, FieldType.NESTED).setSubFieldSchemas(beneficiaryInfoSchema));

        indexSchema.addFieldSchema(new FieldSchema(PREMIUM, FieldType.LONG));
        indexSchema.addFieldSchema(new FieldSchema(PROFIT, FieldType.LONG));

        createRequest.setIndexSchema(indexSchema);

        syncClient.createSearchIndex(createRequest);
        System.out.println("Create SearchIndex success");
    }

    @Override
    protected void doMain() {
        createTable();
        createSearchIndex();
    }
}
