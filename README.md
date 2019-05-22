# Aliyun TableStore Examples

[![License Status](https://img.shields.io/badge/license-apache2-brightgreen.svg)](https://travis-ci.org/aliyun/aliyun-tablestore-nodejs-sdk)
## [Click here for the English README](README_EN.md)

**目录**
- [1、项目结构](#1%E9%A1%B9%E7%9B%AE%E7%BB%93%E6%9E%84)
- [2、开通与配置](#2%E5%BC%80%E9%80%9A%E4%B8%8E%E9%85%8D%E7%BD%AE)
  - [开通服务、创建实例、获取AK](#%E5%BC%80%E9%80%9A%E6%9C%8D%E5%8A%A1%E5%88%9B%E5%BB%BA%E5%AE%9E%E4%BE%8B%E8%8E%B7%E5%8F%96ak)
  - [完成配置](#%E5%AE%8C%E6%88%90%E9%85%8D%E7%BD%AE)
- [3、样例统计](#3%E6%A0%B7%E4%BE%8B%E7%BB%9F%E8%AE%A1)
  - [demos(场景样例)](#demos%E5%9C%BA%E6%99%AF%E6%A0%B7%E4%BE%8B)
  - [tools(迁移、计算等工具)](#tools%E8%BF%81%E7%A7%BB%E8%AE%A1%E7%AE%97%E7%AD%89%E5%B7%A5%E5%85%B7)
  - [feature(SDK基础功能)](#featuresdk%E5%9F%BA%E7%A1%80%E5%8A%9F%E8%83%BD)
- [4、使用提醒](#4%E4%BD%BF%E7%94%A8%E6%8F%90%E9%86%92)
  - [资源释放](#%E8%B5%84%E6%BA%90%E9%87%8A%E6%94%BE)
- [5、咨询/答疑/反馈](#5%E5%92%A8%E8%AF%A2%E7%AD%94%E7%96%91%E5%8F%8D%E9%A6%88)
  - [联系方式：](#%E8%81%94%E7%B3%BB%E6%96%B9%E5%BC%8F)


# 1、项目结构
- 根据功能，分为3个模块(场景样例/迁移、计算等工具/SDK基础功能)，
- 每个项目名下对应独立的Example项目

```
├── README.md
├── demos                                   #场景样例
│   ├── SharingCarManagement                #共享汽车管理
│   ├── SportTrack                          #运动轨迹
│   ├── insurance-policy-management         #保险单管理系统
│   ├── TraceMedicine                       #药品监管（溯源）系统
│   ├── TableStore-Grid                     #气象格点数据解决方案
│   ├── MailManagement                      #基于Timestream的快递轨迹管理
│   └── WifiMonitor                         #基于Timestream的Wifi监控系统
│
├── tools                                   #工具/产品
│   └── Datax-MySQL2TableStore
│
└── feature                                 #SDK基础功能
    └── FuzzySearch                         #模糊查询
```

# 2、开通与配置
## 开通服务、创建实例、获取AK
- [控制台](https://ots.console.aliyun.com): https://ots.console.aliyun.com
- [开通服务](https://help.aliyun.com/document_detail/27287.html): https://help.aliyun.com/document_detail/27287.html
- [创建实例](https://help.aliyun.com/document_detail/55211.html): https://help.aliyun.com/document_detail/55211.html
- [获取AK](https://usercenter.console.aliyun.com/#/manage/ak): https://usercenter.console.aliyun.com/#/manage/ak

## 完成配置
在home目录下创建tablestoreCong.json文件，填写相应参数，所有独立项目都会使用该配置
```
# mac 或 linux系统下：/home/userhome/tablestoreCong.json
# windows系统下: C:\Documents and Settings\%用户名%\tablestoreCong.json
{
  "endpoint": "http://instanceName.cn-hangzhou.ots.aliyuncs.com",
  "accessId": "***********",
  "accessKey": "***********************",
  "instanceName": "instanceName"
}
```
- endpoint：实例的接入地址，控制台实例详情页获取；
- accessId：AK的ID，获取AK链接提供；
- accessKey：AK的密码，获取AK链接提供；
- instanceName：使用的实例名；

# 3、项目统计

## [demos(场景样例)](/demos)
样例 | 语言 | 项目名
--- | --- | ---
[共享汽车管理](https://yq.aliyun.com/articles/703177) | java | [SharingCarManagement](/demos/SharingCarManagement)
[运动轨迹管理](https://yq.aliyun.com/articles/702482) | java | [SportTrack](/demos/SportTrack)
[保险单管理系统](https://yq.aliyun.com/articles/699669) | java | [insurance-policy-management](/demos/insurance-policy-management)
[药品监管（溯源）系统](https://yq.aliyun.com/articles/699636) | java | [TraceMedicine](/demos/TraceMedicine)
[气象格点数据解决方案](https://yq.aliyun.com/articles/698313) | java | [TableStore-Grid](/demos/TableStore-Grid)
[基于Timestream的Wifi监控系统](https://yq.aliyun.com/articles/698591) | java | [WifiMonitor](/demos/WifiMonitor)
[基于Timestream的快递轨迹管理](https://yq.aliyun.com/articles/698551) | java | [MailManagement](/demos/MailManagement)

## tools(迁移、计算等工具)
场景 | 工具 | 项目名
--- | --- | ---
[MySQL数据迁移表格存储](https://yq.aliyun.com/articles/698973) | datax | [Datax-MySQL2TableStore](/tools/Datax-MySQL2TableStore)

## feature(SDK基础功能)
功能 | 语言(SDK) | 项目名
--- | --- | ---
模糊查询 | java | [FuzzySearch](/feature/FuzzySearch)


# 4、使用提醒

## 资源释放
- 删除无用索引、无用数据、无用表格等
- 释放相应资源，避免持续收费


# 5、咨询/答疑/反馈
## 联系方式：
- 钉钉群: 表格存储技术交流群
- 群号: 11789671
- 二维码:

![二维码](image/QRcode.png)
