## 项目简单描述
+ 该项目主要为阿里云DTS数据订阅SDK的简单Demo演示，包含如下：
+ 如何使用公网Maven依赖获取指定二方jar包，详情参考pom.xml
+ 如何使用properties或者xml的形式配置log4j日志输出，详情参考log4j.properties/log4j.xml，其他日志框架请自行查询资料配置
+ 如何使用SDK编写代码获取增量消息

## 开发环境以及其他依赖
+ JDK 1.6+
+ Maven 3.2+
+ Eclipse / InteliJ IDEA / 其他IDE
+ 项目依赖的Maven: [最新版本查询](http://search.maven.org/#search%7Cga%7C1%7Ccom.aliyun.dts)

```
<dependency>
    <groupId>com.aliyun.dts</groupId>
    <artifactId>dts-subscribe-sdk</artifactId>
    <version>4.6.27.12.0</version>
</dependency>
```

## 如何编译、运行、使用示例项目
+ 如果使用eclipse开发，建议使用如下命令将工程初始化成Eclipse项目，可直接IDE中本地调试
  - mvn eclipse:eclipse
+ 配置日志输出文件，log4j.properties和log4j.xml两种形式保留一个文件即可，将另一个文件重命名即可
+ 打包成可执行的jar包：
  - mvn clean assembly:assembly -Dmaven.test.skip=true
  - 或者 mvn clean package -Dmaven.test.skip=true
+ 运行命令：
  - cd target && java -jar demo-1.0-SNAPSHOT-jar-with-dependencies.jar --accessKey ${accessKey} --accessSecret ${accessSecret} --subscribeInstanceID ${subscribeInstanceID}

## dts-subscribe-cli.jar工具使用
+ 该工具为示例代码打包编译后的jar包，是一个独立的消费客户端，需使用accessKey/accessSecret/guid参数获取DTS增量数据
+ usage：java -jar dts-subscribe-cli.jar --accessKey ${accessKey} --accessSecret ${accessSecret} --subscribeInstanceID ${subscribeInstanceID}