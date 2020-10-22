package com.aliyun.tablestore.spark.demo.batch

import com.aliyun.openservices.tablestore.hadoop.TableStore
import org.apache.spark.internal.Logging
import org.apache.spark.sql.SparkSession


object TableStoreBatchSample extends Logging {
  val appName: String = this.getClass.getSimpleName.filterNot(_.equals('$'))

  val sparkSession: SparkSession = SparkSession.builder
    .appName(appName)
    .master("local[*]")
    .getOrCreate()

  val dataCatalog: String =
    s"""
       |{"columns": {
       |    "salt": {"type":"long"},
       |    "UserId": {"type":"string"},
       |    "OrderId": {"type":"string"},
       |    "price": {"type":"double"},
       |    "timestamp": {"type":"long"}
       | }
       |}""".stripMargin

  def main(args: Array[String]): Unit = {
    if (args.length < 5) {
      System.err.println(
        s"Usage: $appName <ots-instanceName> <ots-tableName> " +
          "<access-key-id> <access-key-secret> <ots-endpoint>"
      )
    }

    val Array(
    instanceName,
    tableName,
    accessKeyId,
    accessKeySecret,
    endpoint
    ) = args

    sparkSession.sparkContext.setLogLevel("INFO")

    val df = sparkSession.read
      .format("tablestore")
      .option("instance.name", instanceName)
      .option("table.name", tableName)
      .option("endpoint", endpoint)
      .option("access.key.id", accessKeyId)
      .option("access.key.secret", accessKeySecret)
      .option("split.size.mbs", 100)
      //      .option("catalog", dataCatalog)
      .schema("salt LONG, UserId STRING, OrderId STRING, price DOUBLE, timestamp LONG")
      .load()

    println("With DataFrame")
    df.filter("salt = 1 AND UserId = 'user_A'").show(20, truncate = false)

    println("With Spark SQL")
    df.createTempView("search_view")
    val searchDF = sparkSession.sql("SELECT COUNT(*) FROM search_view WHERE salt = 1 AND UserId = 'user_A'")
    searchDF.show()
    val searchDF2 = sparkSession.sql("SELECT COUNT(*) FROM search_view WHERE salt = 1 AND UserId = 'user_A'" +
      " AND OrderId = '00002664-9d8b-441b-bad7-845202f3b142'")
    searchDF2.show()
    val searchDF3 = sparkSession.sql("SELECT COUNT(*) FROM search_view WHERE salt = 1 AND UserId >= 'user_A' AND UserId < 'user_B'")
    searchDF3.show()

    // when all finished, shutdown
    TableStore.shutdown()
  }
}
