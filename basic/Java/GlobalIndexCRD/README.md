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
    <version>5.3.0</version>
</dependency>
```

# Source Tree
```text
.
└── java
    └── com
        └── aliyun
            └── tablestore
                └── basic
                    ├── BaseExample.java                   # Example base class
                    ├── GlobalIndexCRDExample.java         # Basic operations of Tablestore Global Index :)
                    ├── common
                    │   └── Consts.java                    # Consts
                    └── model
                        └── CallDO.java                    # Domain object
```

# Setup environment & Example
Run the main method in class  [`GlobalIndexCRDExample`](src/main/java/com/aliyun/tablestore/basic/GlobalIndexCRDExample.java), the table & index would be created, mock data inserted, along with queries.

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
