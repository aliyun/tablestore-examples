package com.aliyun.tablestore.spark.demo.streaming

import java.util.UUID

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.{col, count, sum, to_timestamp, window}

object StructuredTableStoreAggSample {
  val appName: String = this.getClass.getSimpleName.filterNot(_.equals('$'))

  val sparkSession: SparkSession = SparkSession.builder
    .appName(appName)
    .master("local[*]")
    .getOrCreate()

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
    if (args.length < 7) {
      System.err.println(
        s"Usage: $appName <ots-instanceName> <ots-tableName> <ots-tunnelId> " +
          "<access-key-id> <access-key-secret> <ots-endpoint> " +
          "<max-offsets-per-channel> [<checkpoint-location>]")
    }

    val Array(
    instanceName,
    tableName,
    tunnelId,
    accessKeyId,
    accessKeySecret,
    endpoint,
    maxOffsetsPerChannel,
    _*
    ) = args

    val checkpointLocation = if (args.length > 7) args(7) else "/tmp/temporary-" + UUID.randomUUID.toString
    println(args.toSeq.toString)

    sparkSession.sparkContext.setLogLevel("WARN")

    val ordersDF = sparkSession.readStream
      .format("org.apache.spark.sql.aliyun.tablestore.TableStoreSourceProvider")
      .option("instance.name", instanceName)
      .option("table.name", tableName)
      .option("tunnel.id", tunnelId)
      .option("endpoint", endpoint)
      .option("access.key.id", accessKeyId)
      .option("access.key.secret", accessKeySecret)
      .option("maxoffsetsperchannel", maxOffsetsPerChannel) // default 10000
      .option("catalog", dataCatalog)
      .load()

    // Your logic
    val aggDF = ordersDF
      .groupBy(window(to_timestamp(col("timestamp") / 1000), "30 seconds"))
      .agg(count("*") as "count", sum("price") as "totalPrice")
      .select("window.start", "window.end", "count", "totalPrice")
      .orderBy("window")

    val query = aggDF.writeStream
      .outputMode("complete")
      .format("console")
      .option("truncate", value = false)
      .option("checkpointLocation", checkpointLocation)
      .option("triggerInterval", 10000) // custom
      .start()

    query.awaitTermination()
  }

}
