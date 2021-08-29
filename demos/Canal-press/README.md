程序为
基于 MySQL + Tablestore 分层存储的大规模订单系统系列文章中，canal部分的压测程序。
程序为 Springboot 服务，通过 Application.java启动服务。服务启动后通过接口完成对应功能。
压测接口见controller。

部署说明
通过mvn指令打出jar包，假设jar包名为test-1.0-SNAPSHOT，（假设在linux上进行部署），
通过脚本启动jar包，指令为：
nohup java -jar   -Dspring.config.location=application.yml    test-1.0-SNAPSHOT.jar &
其中application.yml与jar包处于同一路径。可以在application.yml中进行数据库配置等。

车辆信息压测接口指令如下。carNum为要压入的车辆数，point为每辆车历史记录数。
curl "ip:8082/car/press?carNum=1000&point=10000" -X POST








