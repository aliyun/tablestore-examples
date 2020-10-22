package com.aliyun.tablestore.spark.demo.batch

import com.aliyun.openservices.tablestore.hadoop.TableStore
import org.apache.spark.internal.Logging
import org.apache.spark.sql.SparkSession


object TableStoreSearchIndexSample extends Logging {
  val appName: String = this.getClass.getSimpleName.filterNot(_.equals('$'))

  val sparkSession: SparkSession = SparkSession.builder
    .appName(appName)
    .master("local[*]")
    .getOrCreate()

  def main(args: Array[String]): Unit = {
    if (args.length < 5) {
      System.err.println(
        s"Usage: $appName <ots-instanceName> <ots-tableName> <search-index-name> " +
          "<access-key-id> <access-key-secret> <ots-endpoint>"
      )
    }

    val Array(
    instanceName,
    tableName,
    searchIndexName,
    accessKeyId,
    accessKeySecret,
    endpoint
    ) = args

    sparkSession.sparkContext.setLogLevel("INFO")

    val df = sparkSession.read
      .format("tablestore")
      .schema("salt LONG, UserId STRING, OrderId STRING, price DOUBLE, timestamp LONG")
      .option("instance.name", instanceName)
      .option("table.name", tableName)
      .option("endpoint", endpoint)
      .option("access.key.id", accessKeyId)
      .option("access.key.secret", accessKeySecret)
      .option("search.index.name", searchIndexName) // lead to use SearchIndex
      .load()

    println("With DataFrame")
    df.filter("salt = 1 AND UserId = 'user_A'").show(20, truncate = false)

    println("With Spark SQL")
    df.createTempView("search_view")
    val searchDF = sparkSession.sql("SELECT COUNT(*) FROM search_view WHERE salt = 1 AND UserId = 'user_A'")
    searchDF.show()

    // when all finished, shutdown executors.
    TableStore.shutdown()
  }
}
