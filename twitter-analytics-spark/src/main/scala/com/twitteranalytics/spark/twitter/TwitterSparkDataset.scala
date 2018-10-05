package com.twitteranalytics.spark.twitter

import com.twitteranalytics.spark.twitter.Base._
import org.apache.spark.sql.SparkSession

object TwitterSparkDataset {

  private val Data = "/Users/i851309/Documents/TC/projects/Spark-The-Definitive-Guide/"

  def main(args: Array[String]) {
    val spark = SparkSession.builder()
      .appName("experiments").master("local[4]")
      //      .config("spark.eventLog.enabled", value = true)
      .config("spark.sql.shuffle.partitions", "5")
      .getOrCreate()

    spark.sparkContext.setLogLevel("WARN")
    val t0 = System.currentTimeMillis()
    twitter(spark)
    val elapsed = (System.currentTimeMillis() - t0) / 1000
    println("Elapsed time: " + elapsed + " seconds")
  }

  def experiments(spark: SparkSession): Unit = {
    val df = spark.read.format("json").load(Data + "data/flight-data/json/2015-summary.json")
    df.printSchema()
  }

  case class Tweet(sentiment: Int, id: Long, date: String, what: String, user: String, text: String)

  def twitter(spark: SparkSession): Unit = {
    val twitterData = "/Users/i851309/Downloads/training.1600000.processed.noemoticon.csv"
    import spark.implicits._

    val ds = spark.read.option("inferSchema", "true").csv(twitterData)
      .withColumnRenamed("_c0", "sentiment")
      .withColumnRenamed("_c1", "id")
      .withColumnRenamed("_c2", "date")
      .withColumnRenamed("_c3", "what")
      .withColumnRenamed("_c4", "user")
      .withColumnRenamed("_c5", "text")
      .as[Tweet]

    ds.map(t => parseDate(t.date).toString)
      .groupByKey(identity)
      .count()
      //.write.csv("results2")
      .foreach(kv => println(kv))
  }

}
