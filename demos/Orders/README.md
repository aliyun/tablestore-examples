 程序为
 基于 MySQL + Tablestore 分层存储的大规模订单系统系列文章中 测试代码
 
 程序为 Springboot 服务，通过 Application.java启动服务。服务启动后通过接口完成对应功能。
 IndexController中接口为使用Tablestore sdk，根据多元索引和二级索引来查询数据，具体功能可以参考各接口注释
 TypeController中为MySQL 以及DLA的连通测试接口，订单的压测接口也在TypeController中，具体功能可以参考各接口注释
 
 
 