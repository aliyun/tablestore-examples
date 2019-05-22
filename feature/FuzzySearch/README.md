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
    <version>4.12.0</version>
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

Help tools for manipulating String, Number, etc.
```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
    <version>3.8.1</version>
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
                    ├── FuzzySearchExample.java            # Create table & index, mock data, fuzzy search :)
                    ├── consts
                    │   └── ColumnConsts.java              # Column names
                    ├── model
                    │   └── OrderDO.java                  # Domain object
                    └── utils
                        ├── ClientAndConfig.java           # TableStore related config
                        └── Utils.java                     # Common Utils
```

# Setup environment & Search Example
Run the main method in class  [`FuzzySearchExample`](src/main/java/com/aliyun/tablestore/example/FuzzySearchExample.java), the table & index would be created, mock data inserted, along with several fuzzy search examples.

### prefixSearchExample
query consumer's cell number by prefix
### wildcardSearchExample
query consumer's cell number by wildcard
### singleWordSearchExample
query product name using SingleWord analyzer
### fuzzySearchExample
query product type using Fuzzy analyzer

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