# Aliyun TableStore Examples

[![License Status](https://img.shields.io/badge/license-apache2-brightgreen.svg)](https://travis-ci.org/aliyun/aliyun-tablestore-nodejs-sdk)
## [中文版(Link to Chinese README)](README.md)

**Contents**
- [1.Repository Structure](#1%E9%A1%B9%E7%9B%AE%E7%BB%93%E6%9E%84)
- [2.Get Start](#2%E5%BC%80%E9%80%9A%E4%B8%8E%E9%85%8D%E7%BD%AE)
  - [Open Service](#%E5%BC%80%E9%80%9A%E6%9C%8D%E5%8A%A1%E5%88%9B%E5%BB%BA%E5%AE%9E%E4%BE%8B%E8%8E%B7%E5%8F%96ak)
  - [Config](#%E5%AE%8C%E6%88%90%E9%85%8D%E7%BD%AE)
- [3.Modules](#3%E6%A0%B7%E4%BE%8B%E7%BB%9F%E8%AE%A1)
  - [demos(samples)](#demos%E5%9C%BA%E6%99%AF%E6%A0%B7%E4%BE%8B)
  - [tools(tools)](#tools%E8%BF%81%E7%A7%BB%E8%AE%A1%E7%AE%97%E7%AD%89%E5%B7%A5%E5%85%B7)
  - [feature(SDK)](#featuresdk%E5%9F%BA%E7%A1%80%E5%8A%9F%E8%83%BD)
- [4.Remind](#4%E4%BD%BF%E7%94%A8%E6%8F%90%E9%86%92)
  - [Release resources](#%E8%B5%84%E6%BA%90%E9%87%8A%E6%94%BE)
- [5.Advisory and Feedback](#5%E5%92%A8%E8%AF%A2%E7%AD%94%E7%96%91%E5%8F%8D%E9%A6%88)
  - [Contact Information](#%E8%81%94%E7%B3%BB%E6%96%B9%E5%BC%8F)


# 1, Repository Structure
- Divided into three parts according to function: samples, tools and the usage of SDK.
- Every submodule has seperate folder.

```
├── README.md
├── demos                                   #samples
│   ├── IMChart
│   ├── SharingCarManagement
│   ├── SportTrack
│   ├── insurance-policy-management
│   ├── TraceMedicine
│   ├── TableStore-Grid
│   ├── MailManagement
│   └── WifiMonitor
│
├── tools                                   #tools
│   ├── Dts-MySQL2TableStore
│   └── Datax-MySQL2TableStore
│
└── feature                                 #usage of feature
    ├── TableCopy
    ├── FuzzySearch
    └── AggregationAndGroupBy
```

# 2.Get Start
## Open Service
- [Console](https://ots.console.aliyun.com): https://ots.console.aliyun.com
- [Open Service](https://help.aliyun.com/document_detail/27287.html): https://help.aliyun.com/document_detail/27287.html
- [Create Instance](https://help.aliyun.com/document_detail/55211.html): https://help.aliyun.com/document_detail/55211.html
- [Get AK](https://usercenter.console.aliyun.com/#/manage/ak): https://usercenter.console.aliyun.com/#/manage/ak

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

# 3.Modules

## [demos(samples)](/demos)
Sample Name | Language | Project
--- | --- | ---
[Instant Chart Room(IM)](https://yq.aliyun.com/articles/710363) | java | [IMChart](/demos/ImChart)
[Sharing car management](https://yq.aliyun.com/articles/703177) | java | [SharingCarManagement](/demos/SharingCarManagement)
[Sport track management](https://yq.aliyun.com/articles/702482) | java | [SportTrack](/demos/SportTrack)
[Insurance policy management](https://yq.aliyun.com/articles/699669) | java | [insurance-policy-management](/demos/insurance-policy-management)
[Medicine track management](https://yq.aliyun.com/articles/699636) | java | [TraceMedicine](/demos/TraceMedicine)
[Store and query gridded data](https://yq.aliyun.com/articles/698313) | java | [TableStore-Grid](/demos/TableStore-Grid)
[Equipment monitoring and management](https://yq.aliyun.com/articles/698591) | java | [WifiMonitor](/demos/WifiMonitor)
[Express track management](https://yq.aliyun.com/articles/698551) | java | [MailManagement](/demos/MailManagement)

## tools(tools)
Application | Tool Name | Project
--- | --- | ---
[Data Migration from MySQL to Tablestore](https://yq.aliyun.com/articles/698973) | datax | [Datax-MySQL2TableStore](/tools/Datax-MySQL2TableStore)
[Migrate incrementing data from MySQL to Tablestore](https://yq.aliyun.com/articles/708325) | DTS | [Dts-MySQL2TableStore](/tools/Dts-MySQL2TableStore)

## feature(Tablestore feature)
Feature | Language(SDK) | Project
--- | --- | ---
[Table Copy](https://yq.aliyun.com/articles/706791) | java | [TableCopy](/feature/TableCopy)
[Fuzzy search](https://yq.aliyun.com/articles/703707) | java | [FuzzySearch](/feature/FuzzySearch)
Aggregation & GroupBy | java | [AggregationAndGroupBy](/feature/AggregationAndGroupBy)

## basic(basic usage of SDK)
Usage | Language(SDK) | Project
--- | --- | ---
basic usage | java | [basic usage of SDK](/basic/Java)

# 4.Remind

## Release Resource
- Delete data, indexs and tables if not need any longer.
- Release useless resource in case unnecessary expense.


# 5.Advisory and Feedback
## Contact information
- Dingding Group: Open communication group(Chinese Name: 表格存储技术交流群)
- Group No.: 11789671
- QR code:

![QR code](image/QRcode.png)
