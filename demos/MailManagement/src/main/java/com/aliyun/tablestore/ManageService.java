/***********************************************************
 * Author：潭潭
 * 样例说明：
 *  基于表格存储的时序模型[Timestream]创建的快递轨迹管理系统
 * 功能：
 *  1、快递的meta管理、查询；
 *  2、快递轨迹路线追踪；
 *  3、快递员meta管理、查询；
 * *********************************************************/
package com.aliyun.tablestore;

import com.alicloud.openservices.tablestore.AsyncClient;
import com.alicloud.openservices.tablestore.timestream.TimestreamDBClient;
import com.alicloud.openservices.tablestore.timestream.TimestreamDBConfiguration;
import com.alicloud.openservices.tablestore.timestream.TimestreamDataTable;
import com.alicloud.openservices.tablestore.timestream.TimestreamMetaTable;
import com.alicloud.openservices.tablestore.timestream.model.AttributeIndexSchema;
import com.alicloud.openservices.tablestore.timestream.model.Point;
import com.alicloud.openservices.tablestore.timestream.model.TimestreamIdentifier;
import com.alicloud.openservices.tablestore.timestream.model.TimestreamMeta;
import com.alicloud.openservices.tablestore.timestream.model.filter.*;
import com.aliyun.tablestore.common.Conf;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.aliyun.tablestore.common.Util.*;


public class ManageService {
    private String mailMetaTableName = "defaultMetaTableName";
    private String mailDataTableName = "defaultDataTableName";
    private TimestreamDBClient mailDb;

    private AsyncClient asyncClient = null;
    private String seperator = "/";
    private Random Rand = new Random(79);

    ManageService (String mailMetaTableName, String mailDataTableName) {
        this.mailMetaTableName = mailMetaTableName;
        this.mailDataTableName = mailDataTableName;
    }

    public void init() {
        String os = System.getProperty("os.name");
        if (os.toLowerCase().startsWith("win")) {
            seperator = "\\";
        }
        Conf conf = Conf.newInstance(System.getProperty("user.home") + seperator + "tablestoreConf.json");
        asyncClient = new AsyncClient(
                conf.getEndpoint(),
                conf.getAccessId(),
                conf.getAccessKey(),
                conf.getInstanceName());

        TimestreamDBConfiguration mailConf = new TimestreamDBConfiguration(mailMetaTableName);
        mailDb = new TimestreamDBClient(asyncClient, mailConf);
    }


    /**************************************** management of mails ****************************************/
    public void createMailTable() {
        mailDb.createMetaTable(Arrays.asList(
                new AttributeIndexSchema("fromMobile", AttributeIndexSchema.Type.KEYWORD),
                new AttributeIndexSchema("fromName", AttributeIndexSchema.Type.KEYWORD),
                new AttributeIndexSchema("toMobile", AttributeIndexSchema.Type.KEYWORD),
                new AttributeIndexSchema("toName", AttributeIndexSchema.Type.KEYWORD)
        ));
        mailDb.createDataTable(mailDataTableName);
    }

    public void deleteMailTable() {
        mailDb.deleteDataTable(mailDataTableName);
        mailDb.deleteMetaTable();
    }

    public void writeMail() {
        List<String> whos = Arrays.asList("张三", "李四", "王五", "赵六");
        List<String> actions = Arrays.asList("取件", "派送", "中转" , "中转", "签收");
        List<String> wheres = Arrays.asList(
                "杭州西湖区转塘",
                "吉林省长春市创业大街",
                "上海市闵行区东川路",
                "北京东站南广场西",
                "上海市浦东新区陆家嘴世纪金融广场"
        );
        TimestreamDataTable dataWriter = mailDb.dataTable(mailDataTableName);
        TimestreamMetaTable metaWriter = mailDb.metaTable();

        for (int no = 0; no < 100; no++) {
            String mId = formatMId(no);

            TimestreamIdentifier identifier = new TimestreamIdentifier.Builder(mId)
                    .build();

            TimestreamMeta meta = new TimestreamMeta(identifier)
                    .addAttribute("fromName", whos.get(Rand.nextInt(whos.size())))
                    .addAttribute("fromMobile", "15812345678")
                    .addAttribute("toName", whos.get(Rand.nextInt(whos.size())))
                    .addAttribute("toMobile", "15812345678");

            for (int i = 0; i < 5; i++) {
                String addr = wheres.get(Rand.nextInt(wheres.size()));
                String location = getMailRandomLocation();

                dataWriter.write(
                        meta.getIdentifier(),
                        new Point.Builder(i, TimeUnit.SECONDS)
                                .addField("who", whos.get(i % whos.size()))
                                .addField("do", actions.get(i % actions.size()))
                                .addField("where", addr)
                                .addField("location", location)
                                .build()
                );

                if (i == 0) {
                    meta.addAttribute("fromAddr", addr);
                    meta.addAttribute("fromLocation", location);
                } else if (i == 5 - 1) {
                    meta.addAttribute("toAddr", addr);
                    meta.addAttribute("toLocation", location);
                }
            }
            metaWriter.put(meta);
        }
        waitForSync();
    }

    public void searchMail() {
        Filter filter = new AndFilter(
                Name.equal("m0000000001"),
                Attribute.equal("fromMobile", "15812345678")
        );

        Iterator<TimestreamMeta> metaIterator = mailDb.metaTable()
                .filter(filter)
                .fetchAll();

        while (metaIterator.hasNext()) {
            System.out.println(metaIterator.next().getIdentifier().getName());
        }

    }

    public void scanMailTrace() {
        Filter filter = Name.equal("m0000000001");
        Iterator<TimestreamMeta> metaIterator = mailDb.metaTable()
                .filter(filter)
                .fetchAll();

        //Cause mailId is unique in mail meta，searching by mailId gets none or only one meta；
        TimestreamMeta meta = metaIterator.next();

        Iterator<Point> dataIterator = mailDb.dataTable(mailDataTableName)
                .get(meta.getIdentifier())
                .fetchAll();

        while (dataIterator.hasNext()) {
            System.out.println("\t" + dataIterator.next().getFields());
        }

    }


    public void close() {
        mailDb.close();
    }

    /**************************************** private tool functions ****************************************/
    private void waitForSync() {
        waitForSync(10000);
    }

    private void waitForSync(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
