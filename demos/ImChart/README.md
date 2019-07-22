# Chart Room

Chart Room (Instant Message) Based On Timeline 2.0.

## Configuration

Create the file tablestoreCong.json in the home path, and config the parameters.
```
# Linux or mac system: /home/userhome/tablestoreCong.json
# Windows system: C:\Documents and Settings\%userhome%\tablestoreCong.json
{
 "endpoint": "http://instanceName.cn-hangzhou.ots.aliyuncs.com",
 "accessId": "***********",
 "accessKey": "***********************",
 "instanceName": "instanceName"
}
```
- endpoint: The endpoint of instance.
- accessId: The id of AK.
- accessKey: The secret of AK.
- instanceName: The name of Tablestore instance.


## Getting started

Before start, you should make sure that GlobalIndex service has been opened.

### 1. Init tables and indexes

[InitChartRoomExample.java](src/main/java/com/aliyun/tablestore/example/InitChartRoomExample.java)

### 2. Mock user's request

[ClientRequestExample.java](src/main/java/com/aliyun/tablestore/example/ClientRequestExample.java)

### 3. Drop Indexes and Tables

[ReleaseChartRoomExample.java](src/main/java/com/aliyun/tablestore/example/ReleaseChartRoomExample.java)
