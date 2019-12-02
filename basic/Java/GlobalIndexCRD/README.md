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

# Source Tree
```text
.
└── java
    └── com
        └── aliyun
            └── tablestore
                └── example
                    ├── BaseExample.java                   # Example base class
                    ├── GlobalIndexCRDExample.java         # Basic operations of Tablestore Global Index :)
                    ├── consts
                    │   └── ColumnConsts.java              # Column names
                    ├── model
                    │   └── CallDO.java                    # Domain object
                    └── utils
                        ├── ClientAndConfig.java           # TableStore related config
                        └── Utils.java                     # Common Utils
```

# Setup environment & Example
Run the main method in class  [`GlobalIndexCRDExample`](src/main/java/com/aliyun/tablestore/example/GlobalIndexCRDExample.java), the table & index would be created, mock data inserted, along with queries.

### createTableWithGlobalIndex
create table & global index at the same time
### createTable & createGlobalIndex
create table & global index independently

### listGlobalIndex
list all global indices on a specific table

### describeGlobalIndex
describe meta of a global index

### queryCalledNumber
query a global index

### deleteGlobalIndex
delete a global index
