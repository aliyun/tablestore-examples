package com.aliyun.tablestore;

import com.alicloud.openservices.tablestore.AsyncClient;
import com.alicloud.openservices.tablestore.timestream.TimestreamDBClient;
import com.alicloud.openservices.tablestore.timestream.TimestreamDBConfiguration;
import com.alicloud.openservices.tablestore.timestream.TimestreamDataTable;
import com.alicloud.openservices.tablestore.timestream.TimestreamMetaTable;
import com.alicloud.openservices.tablestore.timestream.model.*;
import com.alicloud.openservices.tablestore.timestream.model.filter.AndFilter;
import com.alicloud.openservices.tablestore.timestream.model.filter.Filter;
import com.alicloud.openservices.tablestore.timestream.model.filter.Name;
import com.alicloud.openservices.tablestore.timestream.model.filter.Tag;
import com.alicloud.openservices.tablestore.timestream.model.filter.Attribute;
import com.aliyun.tablestore.common.Conf;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.aliyun.tablestore.common.Util.getWifiRandomLocation;


public class ManageService {
    private String metaTableName = "DefaultMetaTableName";
    private String dataTableName = "DefaultDataTableName";
    private AsyncClient asyncClient = null;
    private TimestreamDBClient db = null;
    private String pathSeperator = "/";

    ManageService(String metaTableName, String dataTableName) {
        this.metaTableName = metaTableName;
        this.dataTableName = dataTableName;
    }

    public void init() {
        String os = System.getProperty("os.name");
        if (os.toLowerCase().startsWith("win")) {
            pathSeperator = "\\";
        }
        Conf conf = Conf.newInstance(System.getProperty("user.home") + pathSeperator + "tablestoreConf.json");
       asyncClient = new AsyncClient(
                conf.getEndpoint(),
                conf.getAccessId(),
                conf.getAccessKey(),
                conf.getInstanceName());

        TimestreamDBConfiguration dbConfiguration = new TimestreamDBConfiguration(metaTableName);
        db = new TimestreamDBClient(asyncClient, dbConfiguration);
    }

    public void createTable() {
        db.createMetaTable(Arrays.asList(
                new AttributeIndexSchema("group", AttributeIndexSchema.Type.KEYWORD),
                new AttributeIndexSchema("id", AttributeIndexSchema.Type.KEYWORD),
                new AttributeIndexSchema("status", AttributeIndexSchema.Type.KEYWORD),
                new AttributeIndexSchema("version", AttributeIndexSchema.Type.KEYWORD),
                new AttributeIndexSchema("location", AttributeIndexSchema.Type.GEO_POINT)
        ));
        db.createDataTable(dataTableName);
        //db.createDataTable(anotherDataTableWithDifferentAccuracy);
    }

    public void deleteTable() {
        db.deleteDataTable(dataTableName);
        db.deleteMetaTable();
    }

    public void writeData() {
        int groupCount = 2;
        int idCount = 10;
        String name = "wifi";
        long from = 1546272000l;//2019-01-01
        long to = 1546272000l + 60 * 60 * 24 * 1;//2019-01-02


        for (int groupNo = 0; groupNo < groupCount; groupNo++) {
            for (int idNo = 0; idNo < idCount; idNo++) {

                TimestreamIdentifier identifier = new TimestreamIdentifier.Builder(name)
                        .addTag("mac", "mock:mac:" + groupNo + ":" + idNo)
                        .build();

                TimestreamMeta meta = new TimestreamMeta(identifier)
                        .addAttribute("group", "group-" + (groupNo))
                        .addAttribute("id", "id-" + (idNo))
                        .addAttribute("version", "v1.1")
                        .addAttribute("status", "alert")
                        .addAttribute("location", getWifiRandomLocation());

                TimestreamMetaTable metaWriter = db.metaTable();
                metaWriter.put(meta);


                int cpu = (new Random()).nextInt(500);
                int ram = (new Random()).nextInt(500);
                int flash_used = (new Random()).nextInt(500);
                int flash_total = (new Random()).nextInt(500);


                for (long i = from; i < to; i += 60 * 60) {//60秒1个点
                    TimestreamDataTable dataWriter = db.dataTable(dataTableName);
                    dataWriter.asyncWrite(
                            meta.getIdentifier(),
                            new Point.Builder(i, TimeUnit.SECONDS)
                                    .addField("cpu", cpu + new Random().nextInt(40))
                                    .addField("ram", ram + new Random().nextInt(40))
                                    .addField("flash_used", flash_used + new Random().nextInt(40))
                                    .addField("flash_total", flash_total + new Random().nextInt(40))
                                    .build()
                    );
                }

            }
        }
        waitForSync(15000);
    }

    public void readMetaAndData() {
        /**
         * search meta with condition；
         * */
        searchMeta();
        /**
         * scan data by time range for a certain meta；
         * */
        readData();
    }

    public void searchMeta() {
        Filter filter = new AndFilter(
                Name.equal("wifi"),
                Tag.equal("mac", "mock:mac:1:2"),
                Attribute.inGeoDistance("location", "30,120", 1000000)
        );

        Iterator<TimestreamMeta> iterator = db.metaTable()
                .filter(filter)
                .fetchAll();

        while (iterator.hasNext()) {
            TimestreamMeta meta = iterator.next();
            System.out.println(meta.getIdentifier().getName() + ": " + meta.getIdentifier().getTags());
        }
    }

    public void readData() {
        TimestreamIdentifier identifier = new TimestreamIdentifier.Builder("wifi")
                .addTag("mac", "mock:mac:1:2")
                .build();

        Iterator<Point> iterator = db.dataTable(dataTableName)
                .get(identifier)
                .select("cpu", "ram")
                .timeRange(TimeRange.range(0, Long.MAX_VALUE, TimeUnit.SECONDS))
                .fetchAll();

        while (iterator.hasNext()) {
            System.out.println("\t" + iterator.next().toString());
        }
    }

    public void close() {
        db.close();
        System.out.println("[Close]: finish and close timestreamDbClient");
    }


    /**************************************** tool functions ****************************************/

    private void waitForSync() {
        waitForSync(10000);
    }

    private void waitForSync(long time) {
        System.out.println("[Wait]: wait for sync postman index: " + time + " ms...");
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
