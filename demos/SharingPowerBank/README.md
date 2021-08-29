# Configuration
Create a file with name `tablestoreConf.json` in `${user.home}`(For windows: `C:\Documents and Settings\%username%\tablestoreCong.json`, for linux `/home/${username}/tablestoreCong.json`, for mac `/Users/${username}/tablestoreCong.json` )
```json
{
  "endpoint": "*****",
  "accessId": "*******",
  "accessKey": "*************",
  "instanceName": "*****"
}
```

# Dependencies
Tablestore java sdk.
```xml
<dependency>
    <groupId>com.aliyun.openservices</groupId>
    <artifactId>tablestore</artifactId>
    <version>5.10.3</version>
</dependency>
```

# Source Tree
```text
.
└── java
    └── com
        └── aliyun
            └── tablestore
                ├── conmon
                ├── ├── Config.java                    # data size control
                ├── └── TablestoreConf.java            # Tablestore related config    
                ├── utils                              # data make and other
                ├── ApiService.java                    # Tablestore api 
                └── SharingPowerBankExample.java       # Demo start
```

# Demo introduce
# SharingPowerBank样例实现了共享充电宝场景下，亿级充电宝机柜元数据并发更新、多维度检索的样例。下面分别介绍几个class的功能 #
#    1、com.aliyun.tablestore.ApiService.java
#      1-1、提供了构建Tablestore表、多元索引的方法。
#      1-2、提供了机柜模拟数据高并发导入、订单测试数据导入、时序数据导入方法
#      1-3、提供了多元索引高级查询示例，实现机柜元数据多维度检索。
#           ep1 : 用户需要查询有可租用充电宝的机柜信息，并先按照距离远近倒序排序，再按照机柜时价倒序排序。
#                 select * from cabinet where cabinet_available_size > 0 and cabinet_powerPercent > 50 
#                 and cabinet_isonline = 'online'  order by cabinet_pricePerHour asc
#           ep2 : 用户需要查询租赁时价在2元/小时之内，并且有可租用充电宝的机柜。按照距离远近排序。
#                 select * from cabinet where cabinet_pricePerHour between 0,2 and cabinet_available_size > 0
#                 and cabinet_isonline = 'online'
#           ep3 : 运维人员需要查询浙江省内，机柜检修时间戳在半年之前或者已经下线的机柜。取十条记录。
#                 select * from cabinet where cabinet_province = '浙江省' and cabinet_overhaul_time < 2592000000
#                 and cabinet_isonline = 'offline' limit 10
#           ep4 : 运维人员需要统计每个省份已经上线的机柜个数，并按照机柜型号分组，取出前五个型号的数据。
#                 select count(*) from cabinet where cabinet_isonline = 'online' group by cabinet_province limit 5
#    2、com.aliyun.tablestore.common.Config.java
#    共享充电宝场景模拟数据规模配置。
#    3、com.aliyun.tablestore.common.TablestoreConf.java
#    读取配置文件中表格存储连接参数。
#    4、com.aliyun.tablestore.utils
#    工具包，包括了模拟数据生成类，Md5加密类等，无须改动。
#    5、com.aliyun.tablestore.SharingPowerBankExample.java
#    Demo启动类

# Create Table and index
Run main method in class [`SharingPowerBankExample`](src/main/java/com/aliyun/tablestore/SharingPowerBankExample.java)
it will create a table with name `cabinet`,`cabinet_time`,`order` and a index with name `cabinet_index` by default, you can change them by src/main/java/com/aliyun/tablestore/common/Config.java
```json
{
  "endpoint": "*****",
  "accessId": "*******",
  "accessKey": "*************",
  "instanceName": "*****"
}
```
