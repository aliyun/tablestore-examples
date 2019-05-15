package com.aliyun.tablestore.sporttrack;

import com.alicloud.openservices.tablestore.AsyncClient;
import com.alicloud.openservices.tablestore.SyncClient;
import com.alicloud.openservices.tablestore.timestream.TimestreamDBClient;
import com.alicloud.openservices.tablestore.timestream.TimestreamDBConfiguration;
import com.alicloud.openservices.tablestore.timestream.model.AttributeIndexSchema;

import java.util.ArrayList;
import java.util.List;

public class TablestoreTrack implements ITrackStore {
    public TimestreamDBClient timestreamClient;
    public SyncClient client;
    public TablestoreSportTrackConfig config;


    public TablestoreTrack(TablestoreSportTrackConfig config) {
        this.config = config;
        AsyncClient asyncClient = new AsyncClient(config.getEndPoint(), config.getAccessId(), config.getAccessKey(), config.getInstanceName());
        TimestreamDBConfiguration conf = new TimestreamDBConfiguration(config.getTrackMetaTableName());
        conf.setDumpMeta(false);
        timestreamClient = new TimestreamDBClient(asyncClient, conf);

        client = new SyncClient(config.getEndPoint(), config.getAccessId(), config.getAccessKey(), config.getInstanceName());
    }

    @Override
    public void createTable() {
        List<AttributeIndexSchema> indexSchemas = new ArrayList<AttributeIndexSchema>();
        indexSchemas.add(new AttributeIndexSchema("timestamp", AttributeIndexSchema.Type.LONG));
        indexSchemas.add(new AttributeIndexSchema("location", AttributeIndexSchema.Type.GEO_POINT));
        indexSchemas.add(new AttributeIndexSchema("starttime", AttributeIndexSchema.Type.LONG));
        indexSchemas.add(new AttributeIndexSchema("endtime", AttributeIndexSchema.Type.LONG));

        //create meta table
        timestreamClient.createMetaTable(indexSchemas);
        //create data table
        timestreamClient.createDataTable(config.getTrackDataTableName());
    }
}
