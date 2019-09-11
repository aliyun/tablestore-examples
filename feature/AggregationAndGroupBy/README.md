# Configuration
Create a file with name `tablestoreConf.json` in `${user.home}`(For windows: `C:\Documents and Settings\%username%\tablestoreCong.json`, for linux `/home/${username}/tablestoreCong.json`, for mac `/Users/${username}/tablestoreConf.json` )
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
    <version>5.3.0</version>
</dependency>
```
 
JSON parsing tool for parsing config and jsonizing java bean.
```xml
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.9.8</version>
</dependency>
```

# Source Tree
```text
.
└── java
    └── com
        └── aliyun
            └── tablestore
                └── example
                    ├── BaseExample.java                   # Example base class
                    ├── CleanupEnv.java                    # Cleanup environment
                    ├── AggregationAndGroupBy.java            # Create table & index, mock data, test aggregation & groupby :)
                    ├── consts
                    │   └── ColumnConsts.java              # Column names
                    ├── model
                    │   └── PriceDO.java                  # Domain object
                    └── utils
                        ├── ClientAndConfig.java           # TableStore related config
                        └── Utils.java                     # Common Utils
```

# Setup environment & Search Example
Run the main method in class  [`AggregationAndGroupBy`](src/main/java/com/aliyun/tablestore/example/AggregationAndGroupBy.java), the table & index would be created, mock data inserted, along with aggregation & groupby examples.

### testAgg
show the usage of aggregation
### testGroupBy
show the usage of groupby

# Cleanup enviroment
Run the main method in class [`CleanupEnv`](src/main/java/com/aliyun/tablestore/example/CleanupEnv.java)
```json
{
  "endpoint": "*****",
  "accessId": "*******",
  "accessKey": "*************",
  "instanceName": "*****",
  "table": "another_table_name",
  "index": "another_index_name"
}
```