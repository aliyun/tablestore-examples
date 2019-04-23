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
    <version>4.11.0</version>
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
                    ├── BaseExample.java                   # Example base class, used to construct client before example and shutdown client after example
                    ├── CleanExampleTableAndIndex.java     # Clean table and index
                    ├── CreateExampleTableAndIndex.java    # Create table and index
                    ├── ImportExampleData.java             # Import example data
                    ├── SearchPolicyExample.java           # Search example
                    ├── consts
                    │   ├── ColumnConsts.java              # Column name consts
                    │   └── NameConsts.java                # Used to generate applier name
                    ├── model
                    │   └── PolicyDO.java                  # Policy domain object
                    └── utils
                        ├── ClientAndConfig.java           # Tablestore related config
                        └── Utils.java                     # Common Utils
```

# Create Table and index
Run main method in class [`CreateExampleTableAndIndex`](src/main/java/com/aliyun/tablestore/example/CreateExampleTableAndIndex.java), it will create a table with name `insurance_policy` and a index with name `insurance_policy_index` by default, you can change them by add json fields `table` and `table` in `tablestoreCong.json`.
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
# Import Example data
Run main method in class [`ImportExampleData`](src/main/java/com/aliyun/tablestore/example/ImportExampleData.java), it will import 10,000,000 data into your table and index by default. You can change it by add a json field `importDataCount` in `tablestoreCong.json`.
```json
{
  "endpoint": "*****",
  "accessId": "*******",
  "accessKey": "*************",
  "instanceName": "*****",
  "importDataCount": 10000
}

``` 
# Search Example
Search related examples are in class [`SearchPolicyExample`](src/main/java/com/aliyun/tablestore/example/SearchPolicyExample.java)
## searchExample1
Search applier Vernon Richardson's policies 
## searchExample2
Search applier Vernon Richardson's polices which will expire after 2019-03-04
## searchExample3
Search applier Lance Rivera or David Hayes's polices
## searchExample4
Sort by profit desc, but policy with same applier_name will appear only once
## searchExample5
Search beneficiary with name "Tyrone Lee" and profit percentage greater than 50%
# Clean up
Run main method in class [`CleanExampleTableAndIndex`](src/main/java/com/aliyun/tablestore/example/CleanExampleTableAndIndex.java)