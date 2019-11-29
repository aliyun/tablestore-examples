# Data Manage @ Java

Data Manage Based On Java SDK @ 5.3.0.

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

Table will be created when you run the project.

### 1. Single Row Management

[SingleRowManage.java](src/main/java/com/aliyun/tablestore/basic/dataManage/SingleRowManage.java)

### 2. Single Row Management with Auto-Increment PrimaryKey

[SingleRowAutoIncManage.java](src/main/java/com/aliyun/tablestore/basic/dataManage/SingleRowAutoIncManage.java)

### 3. Multi Row Management

[MultiRowManage.java](src/main/java/com/aliyun/tablestore/basic/dataManage/MultiRowManage.java)
