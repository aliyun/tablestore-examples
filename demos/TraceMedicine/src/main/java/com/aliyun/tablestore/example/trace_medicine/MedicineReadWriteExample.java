package com.aliyun.tablestore.example.trace_medicine;

import com.alicloud.openservices.tablestore.AsyncClient;
import com.alicloud.openservices.tablestore.timestream.*;
import com.alicloud.openservices.tablestore.timestream.model.AttributeIndexSchema;

import com.alicloud.openservices.tablestore.timestream.model.Point;
import com.alicloud.openservices.tablestore.timestream.model.TimestreamIdentifier;
import com.alicloud.openservices.tablestore.timestream.model.TimestreamMeta;
import com.alicloud.openservices.tablestore.timestream.model.filter.Attribute;
import com.alicloud.openservices.tablestore.timestream.model.filter.Filter;
import com.alicloud.openservices.tablestore.timestream.model.filter.Name;
import com.alicloud.openservices.tablestore.timestream.model.filter.Tag;
import com.aliyun.tablestore.example.trace_medicine.common.Conf;
import com.google.gson.Gson;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.joda.time.DateTime;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.alicloud.openservices.tablestore.timestream.model.filter.FilterFactory.and;

public class MedicineReadWriteExample {
    private TimestreamDB db;
    private Conf conf;

    public MedicineReadWriteExample(TimestreamDB db, Conf conf) {
        this.db = db;
        this.conf = conf;
    }

    public void createMetaTable() {
        List<AttributeIndexSchema> index = new ArrayList<AttributeIndexSchema>();
        index.add(new AttributeIndexSchema("produced_date", AttributeIndexSchema.Type.LONG));
        index.add(new AttributeIndexSchema("period_of_validity", AttributeIndexSchema.Type.LONG));
        index.add(new AttributeIndexSchema("loc", AttributeIndexSchema.Type.GEO_POINT));
        index.add(new AttributeIndexSchema("links", AttributeIndexSchema.Type.KEYWORD));
        index.add(new AttributeIndexSchema("status", AttributeIndexSchema.Type.KEYWORD));
        index.add(new AttributeIndexSchema("extension", AttributeIndexSchema.Type.KEYWORD).setIsArray(true));

        db.createMetaTable(index);
    }

    public void createDataTable() {
        db.createDataTable(conf.getDataTableName());
    }

    public void importMeta() throws IOException {
        TimestreamMetaTable metaTable = db.metaTable();
        String [] fileHeader = {"分类", "名称", "监管号", "受理号", "生产日期", "有效日期", "注册分类", "申请类型", "企业名称", "任务类型"};
        String csvFile = conf.getMetaFile();
        CSVFormat format = CSVFormat.DEFAULT.withHeader(fileHeader).withIgnoreHeaderCase().withTrim();
        Reader reader = Files.newBufferedReader(Paths.get(csvFile));
        CSVParser csvParser = new CSVParser(reader, format);
        for (CSVRecord r : csvParser.getRecords()) {
            TimestreamIdentifier identifier = new TimestreamIdentifier.Builder(r.get("分类"))
                    .addTag("名称", r.get("名称"))
                    .addTag("监管号", r.get("监管号"))
                    .build();
            TimestreamMeta meta = new TimestreamMeta(identifier);

            meta.addAttribute("produced_date", r.get("生产日期"));
            meta.addAttribute("period_of_validity", r.get("有效日期"));

            List<String> extension = new ArrayList();
            extension.add("受理号=" + r.get("受理号"));
            extension.add("注册分类=" + r.get("注册分类"));
            extension.add("申请类型=" + r.get("申请类型"));
            extension.add("企业名称=" + r.get("企业名称"));
            extension.add("任务类型=" + r.get("任务类型"));
            meta.addAttribute("extension", new Gson().toJson(extension));

            metaTable.put(meta);
            System.out.println(meta.toString());
        }
    }

    private long stringToTimestamp (String tsStr) throws ParseException {
        DateTime date = new DateTime(tsStr) ;
        return date.getMillis();
    }

    private String toLocationString(String input) {
        return input.replace(':', ',');
    }

    private long getTimestamp(CSVRecord r, String column) throws ParseException {
        return stringToTimestamp(r.get(column));
    }

    public void importData() throws Exception {
        TimestreamMetaTable metaTable = db.metaTable();
        TimestreamDataTable dataTable = db.dataTable(conf.getDataTableName());

        String [] fileHeader = {"分类", "名称", "监管号", "生产日期", "位置", "环节", "状态"};
        String csvFile = conf.getDataFile();
        CSVFormat format = CSVFormat.DEFAULT.withHeader(fileHeader).withIgnoreHeaderCase().withTrim();
        Reader reader = Files.newBufferedReader(Paths.get(csvFile));
        CSVParser csvParser = new CSVParser(reader, format);
        for (CSVRecord r : csvParser.getRecords()) {
            TimestreamIdentifier identifier = new TimestreamIdentifier.Builder(r.get("分类"))
                    .addTag("名称", r.get("名称"))
                    .addTag("监管号", r.get("监管号"))
                    .build();

            TimestreamMeta meta = new TimestreamMeta(identifier);

            String loc = toLocationString(r.get("位置"));
            String links = r.get("环节");
            String status = r.get("状态");

            meta.addAttribute("loc", loc);
            meta.addAttribute("links", links);
            meta.addAttribute("status", status);
            metaTable.update(meta);

            Point point = new Point.Builder(this.getTimestamp(r, "生产日期"), TimeUnit.MILLISECONDS)
                    .addField("loc", loc)
                    .addField("links", links)
                    .addField("status", status)
                    .build();
            dataTable.asyncWrite(identifier, point);
            System.out.println(point.toString());
        }
        dataTable.flush();
    }

    public void query() {
        TimestreamMetaTable metaTable = db.metaTable();
        TimestreamDataTable dataTable = db.dataTable(conf.getDataTableName());

        {
            System.out.println("=================================================");
            Filter filter = and(
                    Name.equal("中药"),
                    Tag.equal("名称", "复方阿胶"),
                    Attribute.in("extension", new String[]{"企业名称=山东****也有限公司"})
            );

            Iterator<TimestreamMeta> iter = metaTable.filter(filter).fetchAll();
            while (iter.hasNext()) {
                TimestreamMeta m = iter.next();
                System.out.println(m);
            }
        }
        {
            System.out.println("=================================================");
            Filter filter = and(
                    Name.equal("中药"),
                    Tag.equal("名称", "复方阿胶"),
                    Tag.equal("监管号", "8160000000000019")
            );
            Iterator<TimestreamMeta> iter = metaTable.filter(filter).selectAttributes("status").fetchAll();
            while (iter.hasNext()) {
                TimestreamMeta m = iter.next();
                System.out.println(m.getAttributeAsString("status"));
            }
        }
        {
            System.out.println("=================================================");
            Filter filter = and(
                    Name.equal("化药"),
                    Tag.prefix("名称", "阿莫西林"),
                    Attribute.equal("links", "售卖"),
                    Attribute.inGeoDistance("loc", "31.6533906593,103.8427768645", 5 * 1000)
            );
            Iterator<TimestreamMeta> iter = metaTable.filter(filter).returnAll().fetchAll();
            while (iter.hasNext()) {
                TimestreamMeta m = iter.next();
                System.out.println(m);
            }
        }
        {
            System.out.println("=================================================");
            TimestreamIdentifier identifier = new TimestreamIdentifier.Builder("化药")
                    .addTag("名称", "阿莫西林")
                    .addTag("监管号", "8150000000000000")
                    .build();

            Iterator<Point> iter = dataTable.get(identifier).select("loc").fetchAll();
            while (iter.hasNext()) {
                Point p = iter.next();
                System.out.println(p);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Conf conf = Conf.newInstance();

        // Init Tablestore client
        AsyncClient asyncClient = new AsyncClient(
                conf.getEndpoint(),
                conf.getAccessId(),
                conf.getAccessKey(),
                conf.getInstanceName());

        // Init Timestream client
        TimestreamDBConfiguration config = new TimestreamDBConfiguration(conf.getMetaTableName());
        config.setIntervalDumpMeta(5, TimeUnit.SECONDS);
        TimestreamDB db = new TimestreamDBClient(asyncClient, config);

        try {
            MedicineReadWriteExample demo = new MedicineReadWriteExample(db, conf);
            demo.createMetaTable();
            demo.createDataTable();

            Thread.sleep(TimeUnit.SECONDS.toMillis(5));

            demo.importMeta();
            demo.importData();

            // waiting build index
            Thread.sleep(TimeUnit.SECONDS.toMillis(60));

            demo.query();
        } finally {
            db.close();
            asyncClient.shutdown();
        }
    }
}
