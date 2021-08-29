package com.aliyun.tablestore.utils;

import java.util.ArrayList;
import java.util.Arrays;

public class StaticData {
    public static ArrayList<CabinetInfo> cabinetInfos;
    public static ArrayList<CabinetGeo> cabinetGeos;
    public static ArrayList<String> cabinetRoad;
    public static void init()
    {

            cabinetInfos = new ArrayList<CabinetInfo>();
            cabinetGeos = new ArrayList<CabinetGeo>();
            cabinetRoad = new ArrayList<String>();

            cabinetRoad.add("和平大道");
            cabinetRoad.add("解放路");
            cabinetRoad.add("长安街");
            cabinetRoad.add("八里桥");
            cabinetRoad.add("中山路");
            cabinetRoad.add("幸福大道");
            cabinetRoad.add("三阳路");
            cabinetRoad.add("江虹路");
            cabinetRoad.add("西兴路");
            cabinetRoad.add("平安大道");
            cabinetRoad.add("蜀山路");
            cabinetRoad.add("萧然西路");
            cabinetRoad.add("南京东路");
            cabinetRoad.add("盘山路");
            cabinetRoad.add("晨辉大道");
            cabinetRoad.add("风情大道");
            cabinetRoad.add("亚太路");
            cabinetRoad.add("狮闻大道");
            cabinetRoad.add("六号路");
            cabinetRoad.add("河山大道");
            cabinetRoad.add("铜鉴湖路");
            cabinetRoad.add("云泉路");
            cabinetRoad.add("夏铜街道");
            cabinetRoad.add("灵龙路");
            cabinetRoad.add("东望路");
            cabinetRoad.add("大桥南路");
            cabinetRoad.add("江滨南大道");
            cabinetRoad.add("之江路");
            cabinetRoad.add("高新大道");
            cabinetRoad.add("雄楚路");
            cabinetRoad.add("珞瑜大道");
            cabinetRoad.add("关山路");
            cabinetRoad.add("民族大道");
            cabinetRoad.add("大学园路");
            cabinetRoad.add("夏江路");
            cabinetRoad.add("关堡路");
            cabinetRoad.add("黄龙山大道");
            cabinetRoad.add("新安街");
            cabinetRoad.add("八一大道");
            cabinetRoad.add("卓刀泉街道");
            cabinetRoad.add("桂山路");
            cabinetRoad.add("江城大道");
            cabinetRoad.add("罗七街");
            cabinetRoad.add("王家嘴大道");
            cabinetRoad.add("三角湖路");
            cabinetRoad.add("观湖街道");
            cabinetRoad.add("枫树三路");


            cabinetInfos.add(new CabinetInfo("厂商一","sharebox5000",7,3));
            cabinetInfos.add(new CabinetInfo("厂商一","sharebox3000",4,2));
            cabinetInfos.add(new CabinetInfo("厂商一","sharebox2000",4,4));
            cabinetInfos.add(new CabinetInfo("厂商一","sharebox4000",2,5));
            cabinetInfos.add(new CabinetInfo("厂商一","sharebox7000",6,2));
            cabinetInfos.add(new CabinetInfo("厂商一","sharebox9000",5,2));
            cabinetInfos.add(new CabinetInfo("厂商一","sharebox8000",5,2));

            cabinetInfos.add(new CabinetInfo("厂商二","D149",2,2));
            cabinetInfos.add(new CabinetInfo("厂商二","D228",4,3));
            cabinetInfos.add(new CabinetInfo("厂商二","D199",4,2));
            cabinetInfos.add(new CabinetInfo("厂商二","D015",5,3));
            cabinetInfos.add(new CabinetInfo("厂商二","D328",6,1));
            cabinetInfos.add(new CabinetInfo("厂商二","D981",4,2));
            cabinetInfos.add(new CabinetInfo("厂商二","D102",6,1));
            cabinetInfos.add(new CabinetInfo("厂商二","D229",6,2));
            cabinetInfos.add(new CabinetInfo("厂商二","D839",8,1));
            cabinetInfos.add(new CabinetInfo("厂商二","D114",6,1));
            cabinetInfos.add(new CabinetInfo("厂商二","D191",6,2));

            cabinetInfos.add(new CabinetInfo("厂商三","E102",6,5));
            cabinetInfos.add(new CabinetInfo("厂商三","E122",4,3));
            cabinetInfos.add(new CabinetInfo("厂商三","E111",2,1));
            cabinetInfos.add(new CabinetInfo("厂商三","E119",4,2));
            cabinetInfos.add(new CabinetInfo("厂商三","E121",5,4));
            cabinetInfos.add(new CabinetInfo("厂商三","E189",3,3));
            cabinetInfos.add(new CabinetInfo("厂商三","E132",2,2));
            cabinetInfos.add(new CabinetInfo("厂商三","E109",4,1));
            cabinetInfos.add(new CabinetInfo("厂商三","E110",1,2));
            cabinetInfos.add(new CabinetInfo("厂商三","E145",2,3));
            cabinetInfos.add(new CabinetInfo("厂商三","E191",5,2));

            cabinetInfos.add(new CabinetInfo("厂商四","DZL003",5,1));
            cabinetInfos.add(new CabinetInfo("厂商四","DZL005",2,4));
            cabinetInfos.add(new CabinetInfo("厂商四","DZL006",1,3));
            cabinetInfos.add(new CabinetInfo("厂商四","DZL010",7,2));
            cabinetInfos.add(new CabinetInfo("厂商四","DZL018",8,2));
            cabinetInfos.add(new CabinetInfo("厂商四","DZL020",4,3));
            cabinetInfos.add(new CabinetInfo("厂商四","DZL008",4,2));

            cabinetInfos.add(new CabinetInfo("厂商五","A01",4,2));
            cabinetInfos.add(new CabinetInfo("厂商五","A03",3,1));
            cabinetInfos.add(new CabinetInfo("厂商五","A05",2,4));
            cabinetInfos.add(new CabinetInfo("厂商五","A10",3,3));
            cabinetInfos.add(new CabinetInfo("厂商五","A12",8,1));
            cabinetInfos.add(new CabinetInfo("厂商五","A19",6,2));
            cabinetInfos.add(new CabinetInfo("厂商五","A28",4,2));
            cabinetInfos.add(new CabinetInfo("厂商五","A30",4,3));

            cabinetInfos.add(new CabinetInfo("厂商六","SL021",4,3));
            cabinetInfos.add(new CabinetInfo("厂商六","SM210",4,3));
            cabinetInfos.add(new CabinetInfo("厂商六","SD195",4,3));
            cabinetInfos.add(new CabinetInfo("厂商六","SL182",4,3));
            cabinetInfos.add(new CabinetInfo("厂商六","SL981",4,3));
            cabinetInfos.add(new CabinetInfo("厂商六","SM120",4,3));

            cabinetGeos.add(new CabinetGeo("山东省","117.000923, 36.675807",new ArrayList<String>(Arrays.asList("济南市","青岛市","泰安市","淄博市","烟台市","淮坊市","枣庄市",
                    "德州市","东营市","聊城市"))));
            cabinetGeos.add(new CabinetGeo("河北省","125.35000,43.88333",new ArrayList<String>(Arrays.asList("石家庄市","唐山市","秦皇岛市","邯郸市","邢台市","保定市","张家口市","沧州市"))));
            cabinetGeos.add(new CabinetGeo("黑龙江省","127.63333,47.75000",new ArrayList<String>(Arrays.asList("哈尔滨市","齐齐哈尔市","大庆市","牡丹江市","黑河市"))));
            cabinetGeos.add(new CabinetGeo("辽宁省","123.38333,41.80000",new ArrayList<String>(Arrays.asList("沈阳市","大连市","鞍山市","抚顺市","本溪市","丹东市","锦州市","营口市"))));
            cabinetGeos.add(new CabinetGeo("内蒙古自治区","111.670801, 41.818311",new ArrayList<>()));
            cabinetGeos.add(new CabinetGeo("新疆省","87.68333,43.76667",new ArrayList<String>(Arrays.asList("乌鲁木齐市","克拉玛依市","吐鲁番市","哈密市"))));
            cabinetGeos.add(new CabinetGeo("甘肃省","103.73333,36.03333",new ArrayList<String>(Arrays.asList("兰州市","嘉峪关市","金昌市","平凉市","庆阳市","定西市"))));
            cabinetGeos.add(new CabinetGeo("宁夏自治区","106.26667,37.46667",new ArrayList<String>(Arrays.asList())));
            cabinetGeos.add(new CabinetGeo("山西省","112.53333,37.86667",new ArrayList<String>(Arrays.asList("太原市","大同市","阳泉市","长治市","晋城市","朔州市","临汾市","运城市"))));
            cabinetGeos.add(new CabinetGeo("陕西省","108.95000,34.26667",new ArrayList<String>(Arrays.asList("西安市","铜川市","宝鸡市","咸阳市","延安市","汉中市","安康市","商洛市"))));
            cabinetGeos.add(new CabinetGeo("河南省","113.65000,34.76667",new ArrayList<String>(Arrays.asList("郑州市","开封市","洛阳市","平顶山市"))));
            cabinetGeos.add(new CabinetGeo("安徽省","117.283042, 31.86119",new ArrayList<String>(Arrays.asList("合肥市","芜湖市","淮南市","马鞍山市","淮北市","铜陵市"))));
            cabinetGeos.add(new CabinetGeo("江苏省","119.78333,32.05000",new ArrayList<String>(Arrays.asList("南京市","无锡市","徐州市","苏州市","南通市","盐城市","扬州市","镇江市"))));
            cabinetGeos.add(new CabinetGeo("浙江省","120.20000,30.26667",new ArrayList<String>(Arrays.asList("杭州市","宁波市","温州市","嘉兴市","湖州市","绍兴市","金华市","舟山市"))));
            cabinetGeos.add(new CabinetGeo("福建省","118.30000,26.08333",new ArrayList<String>(Arrays.asList("福州市","厦门市","莆田市","三明市","泉州市","漳州市","南平市","龙岩市"))));
            cabinetGeos.add(new CabinetGeo("广东省","113.23333,23.16667",new ArrayList<String>(Arrays.asList("广州市","韶关市","深圳市","珠海市","汕头市","佛山市","江门市","茂名市"))));
            cabinetGeos.add(new CabinetGeo("江西省","115.90000,28.68333",new ArrayList<String>(Arrays.asList("南昌市","景德镇市","萍乡市","九江市","新余市","鹰潭市","漳州市","宜春市"))));
            cabinetGeos.add(new CabinetGeo("海南省","110.35000,20.01667",new ArrayList<String>(Arrays.asList("海口市","三亚市","三沙市","五指山市","琼海市"))));
            cabinetGeos.add(new CabinetGeo("广西省","108.320004, 22.82402",new ArrayList<String>(Arrays.asList("南宁市","柳州市","桂林市","贺州市"))));
            cabinetGeos.add(new CabinetGeo("贵州省","106.71667,26.56667",new ArrayList<String>(Arrays.asList("贵阳市","六盘水市","遵义市","安顺市","毕节市","铜仁市"))));
            cabinetGeos.add(new CabinetGeo("湖南省","113.00000,28.21667",new ArrayList<String>(Arrays.asList("长沙市","株洲市","湘潭市","衡阳市","邵阳市","岳阳市","常德市","张家界市"))));
            cabinetGeos.add(new CabinetGeo("湖北省","114.298572, 30.584355",new ArrayList<String>(Arrays.asList("武汉市","黄石市","十堰市","宜昌市","黄冈市","襄阳市","鄂州市","荆门市"))));
            cabinetGeos.add(new CabinetGeo("四川省","104.06667,30.66667",new ArrayList<String>(Arrays.asList("成都市","自贡市","攀枝花市","泸州市","绵阳市","广元市"))));
            cabinetGeos.add(new CabinetGeo("云南省","102.73333,25.05000",new ArrayList<String>(Arrays.asList("昆明市","曲靖市","少同时","玉溪市","普洱市"))));
            cabinetGeos.add(new CabinetGeo("西藏自治区","91.00000,30.60000",new ArrayList<String>()));
            cabinetGeos.add(new CabinetGeo("青海省","96.75000,36.56667",new ArrayList<String>(Arrays.asList("西宁市","海东市"))));
            cabinetGeos.add(new CabinetGeo("天津市","117.20000,39.13333",new ArrayList<>()));
            cabinetGeos.add(new CabinetGeo("上海市","121.55333,31.20000",new ArrayList<>()));
            cabinetGeos.add(new CabinetGeo("重庆市","106.45000, 29.56667",new ArrayList<>()));
            cabinetGeos.add(new CabinetGeo("北京市","116.41667,39.91667",new ArrayList<>()));
            cabinetGeos.add(new CabinetGeo("台湾省","121.30, 25.03",new ArrayList<String>(Arrays.asList("台北市","高雄市","基隆市","台中市","台南市"))));
            cabinetGeos.add(new CabinetGeo("香港特别行政区","114.10000,22.20000",new ArrayList<>()));
            cabinetGeos.add(new CabinetGeo("澳门特别行政区","113.50000,22.20000",new ArrayList<>()));
    }
}

