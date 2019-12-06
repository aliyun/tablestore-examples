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
├── README.md
├── pom.xml
└── src
    ├── main
    │   └── java
    └── test
        └── java
            └── com
                └── aliyun
                    └── tablestore
                        └── basic
                            ├── BaseExample.java
                            └── LocalTransactionExample.java    # examples



```

# Setup environment & Example
Run tests in  [`LocalTransactionExample`](src/test/java/com/aliyun/tablestore/basic/LocalTransactionExample.java).

# Examples
1. testPutMultipleRow: put two row separately in one transaction
1. testPutRowWithAbortTransaction: put row then abort transaction
1. testPutRowWithoutTransactionId: put one row A in transaction and put row B with same partition key with row A out of transaction
1. testBatchWriteRow: two `batchWriteRow` in one transaction
1. testReadThenWriteWithConcurrentModification: concurrent modification out of transaction

