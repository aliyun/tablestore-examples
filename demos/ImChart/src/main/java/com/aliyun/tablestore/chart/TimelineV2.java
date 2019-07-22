package com.aliyun.tablestore.chart;

import com.alicloud.openservices.tablestore.SyncClient;
import com.alicloud.openservices.tablestore.model.search.FieldSchema;
import com.alicloud.openservices.tablestore.model.search.FieldType;
import com.alicloud.openservices.tablestore.model.search.IndexSchema;
import com.alicloud.openservices.tablestore.timeline.TimelineMetaStore;
import com.alicloud.openservices.tablestore.timeline.TimelineStore;
import com.alicloud.openservices.tablestore.timeline.TimelineStoreFactory;
import com.alicloud.openservices.tablestore.timeline.core.TimelineStoreFactoryImpl;
import com.alicloud.openservices.tablestore.timeline.model.TimelineIdentifierSchema;
import com.alicloud.openservices.tablestore.timeline.model.TimelineMetaSchema;
import com.alicloud.openservices.tablestore.timeline.model.TimelineSchema;
import com.aliyun.tablestore.chart.common.ExampleCons;

import java.util.Arrays;

public class TimelineV2 {
    private TimelineMetaStore timelineMetaStoreInstance;
    private TimelineStore timelineStoreTableInstance;
    private TimelineStore timelineSyncTableInstance;

    private String timelineMetaTable = ExampleCons.TIMELINE_META_TABLE;
    private String timelineMetaIndex = ExampleCons.TIMELINE_META_INDEX;
    private String timelineStoreTable = ExampleCons.TIMELINE_STORE_TABLE;
    private String timelineStoreIndex = ExampleCons.TIMELINE_STORE_INDEX;
    private String timelineSyncTable = ExampleCons.TIMELINE_SYNC_TABLE;

    private int ttlForever = -1;
    private int ttl7Day = 7 * 24 * 60 * 60;

    public TimelineV2(SyncClient syncClient) {
        TimelineStoreFactory serviceFactory = new TimelineStoreFactoryImpl(syncClient);

        generateTimelineMetaStore(serviceFactory);
        generateTimelineStore(serviceFactory);
        generateTimelineSync(serviceFactory);
    }

    public TimelineMetaStore getTimelineMetaStoreInstance(){
        return timelineMetaStoreInstance;
    }

    public TimelineStore getTimelineStoreTableInstance() {
        return timelineStoreTableInstance;
    }

    public TimelineStore getTimelineSyncTableInstance() {
        return timelineSyncTableInstance;
    }


    private void generateTimelineMetaStore(TimelineStoreFactory serviceFactory) {
        TimelineIdentifierSchema idSchema = new TimelineIdentifierSchema.Builder()
                .addStringField("timeline_id").build();

        // index schema of meta table, take group meta for example
        IndexSchema metaIndex = new IndexSchema();
        metaIndex.setFieldSchemas(Arrays.asList(
                new FieldSchema("type", FieldType.KEYWORD).setIndex(true).setEnableSortAndAgg(true).setStore(true),
                new FieldSchema("group_name", FieldType.TEXT).setIndex(true).setStore(true).setAnalyzer(FieldSchema.Analyzer.MaxWord)
        ));

        // set timeline schema and prepare all tables include data table/index and meta table/index.
        TimelineMetaSchema metaSchema = new TimelineMetaSchema(timelineMetaTable, idSchema)
                .withIndex(timelineMetaIndex, metaIndex);

        timelineMetaStoreInstance = serviceFactory.createMetaStore(metaSchema);
    }


    private void generateTimelineStore(TimelineStoreFactory serviceFactory) {
        TimelineIdentifierSchema idSchema = new TimelineIdentifierSchema.Builder()
                .addStringField("timeline_id").build();

        // index schema of timeline table
        IndexSchema timelineIndex = new IndexSchema();
        timelineIndex.setFieldSchemas(Arrays.asList(
                new FieldSchema("timeline_id", FieldType.KEYWORD).setIndex(true).setEnableSortAndAgg(true).setStore(true),
                new FieldSchema("text", FieldType.TEXT).setIndex(true).setStore(true).setAnalyzer(FieldSchema.Analyzer.MaxWord),
                new FieldSchema("sender", FieldType.KEYWORD).setIndex(true).setEnableSortAndAgg(true).setStore(true),
                new FieldSchema("send_time", FieldType.LONG).setIndex(true).setEnableSortAndAgg(true).setStore(true)
        ));

        // set timeline schema and prepare all tables include data table/index and meta table/index.
        TimelineSchema timelineSchema = new TimelineSchema(timelineStoreTable, idSchema)
                .autoGenerateSeqId() //set auto-generated sequence id
                .setTimeToLive(ttlForever)
                .withIndex(timelineStoreIndex, timelineIndex);

        timelineStoreTableInstance = serviceFactory.createTimelineStore(timelineSchema);
    }


    private void generateTimelineSync(TimelineStoreFactory serviceFactory) {
        TimelineIdentifierSchema idSchema = new TimelineIdentifierSchema.Builder()
                .addStringField("timeline_id").build();

        // set timeline schema and prepare all tables include data table/index and meta table/index.
        TimelineSchema timelineSchema = new TimelineSchema(timelineSyncTable, idSchema)
                .autoGenerateSeqId() //set auto-generated sequence id
                .setTimeToLive(ttl7Day);

        timelineSyncTableInstance = serviceFactory.createTimelineStore(timelineSchema);
    }
}
