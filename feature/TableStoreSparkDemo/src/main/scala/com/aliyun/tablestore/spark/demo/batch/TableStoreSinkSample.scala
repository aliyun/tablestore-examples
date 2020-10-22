package com.aliyun.tablestore.spark.demo.batch

import com.aliyun.openservices.tablestore.hadoop.TableStore
import org.apache.spark.sql.SparkSession

object TableStoreSinkSample {
  val appName: String = this.getClass.getSimpleName.filterNot(_.equals('$'))

  val sparkSession: SparkSession = SparkSession.builder
    .appName(appName)
    .master("local[*]")
    .getOrCreate()

  // custom
  val dataCatalog: String =
    s"""
       |{"columns": {
       |    "UserId": {"type":"string"},
       |    "OrderId": {"type":"string"},
       |    "price": {"type":"double"},
       |    "timestamp": {"type":"long"}
       | }
       |}""".stripMargin

  def main(args: Array[String]): Unit = {
    if (args.length < 5) {
      System.err.println(
        s"Usage: $appName <ots-instanceName> <source-table> <sink-table>" +
          "<access-key-id> <access-key-secret> <ots-endpoint>"
      )
    }

    val Array(
    instanceName,
    sourceTableName,
    sinkTableName,
    accessKeyId,
    accessKeySecret,
    endpoint
    ) = args

    sparkSession.sparkContext.setLogLevel("INFO")

    val sourceDF = sparkSession.read
      .format("tablestore")
      .option("instance.name", instanceName)
      .option("table.name", sourceTableName)
      .option("endpoint", endpoint)
      .option("access.key.id", accessKeyId)
      .option("access.key.secret", accessKeySecret)
      //      .option("catalog", dataCatalog)
      .schema("UserId STRING, OrderId STRING, price DOUBLE, timestamp LONG")
      .load()
      .select("*")


    // Table copy
    sourceDF.write
      .format("tablestore")
      .mode("append")
      .option("instance.name", instanceName)
      .option("table.name", sinkTableName)
      .option("endpoint", endpoint)
      .option("access.key.id", accessKeyId)
      .option("access.key.secret", accessKeySecret)
      .option("catalog", dataCatalog)
      .save()

    // when all finished, shutdown executors
    TableStore.shutdown()
  }
}
