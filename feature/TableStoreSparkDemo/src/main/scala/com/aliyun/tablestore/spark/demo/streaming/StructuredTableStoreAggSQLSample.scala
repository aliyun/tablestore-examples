package com.aliyun.tablestore.spark.demo.streaming

import java.util.UUID

import com.aliyun.tablestore.spark.demo.streaming.StructuredTableStoreAggSample.sparkSession
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.{col, count, sum, to_timestamp, window}

object StructuredTableStoreAggSQLSample {
  val appName: String = this.getClass.getSimpleName.filterNot(_.equals('$'))

  val sparkSession: SparkSession = SparkSession.builder
    .appName("StructuredTableStoreAggSQLSample")
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
        s"Usage: $appName <ots-instanceName>" +
          "<ots-tableName> <ots-tunnelId> <access-key-id> <access-key-secret> <ots-endpoint>" +
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
    //    System.out.println(args.toSeq.toString)

    sparkSession.sparkContext.setLogLevel("WARN")

    val ordersDF = sparkSession.readStream
      .format("tablestore")
      .option("instance.name", instanceName)
      .option("table.name", tableName)
      .option("tunnel.id", tunnelId)
      .option("endpoint", endpoint)
      .option("access.key.id", accessKeyId)
      .option("access.key.secret", accessKeySecret)
      .option("maxoffsetsperchannel", maxOffsetsPerChannel) // default 10000
      .option("catalog", dataCatalog)
      .load()
      .createTempView("order_source_stream_view")

    // Use streaming sql
    val aggDF = sparkSession.sql(
      "SELECT CAST(window.start AS String) AS begin, CAST(window.end AS String) AS end, count(*) AS count, " +
        "CAST(sum(price) AS Double) AS totalPrice FROM order_source_stream_view " +
        "GROUP BY window(to_timestamp(timestamp / 1000), '30 seconds')")


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
