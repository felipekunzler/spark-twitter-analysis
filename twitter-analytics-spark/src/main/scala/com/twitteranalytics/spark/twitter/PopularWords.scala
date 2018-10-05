package com.twitteranalytics.spark.twitter

import com.twitteranalytics.spark.twitter.Base._
import org.apache.spark.sql.SparkSession

object PopularWords {

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("experiments").master("local[*]")
      //.config("spark.eventLog.enabled", value = true)
      .config("spark.sql.shuffle.partitions", "5")
      .getOrCreate()

    spark.sparkContext.setLogLevel("WARN")
    countWords(spark)
  }

  def countWords(spark: SparkSession): Unit = {
    val twitterData = "/Users/i851309/Downloads/training.1600000.processed.noemoticon.csv"
    spark.sparkContext.textFile(twitterData)
      .map(lineToTweetText)
      .flatMap(_.split(" "))
      .map(_.replaceAll("\\W+", ""))
      .map(stem)
      .filter(_.length > 3)
      .filter(w => !StopWords.contains(w))
      .map((_, 1))
      .reduceByKey(_ + _)
      .filter(_._2 > 10)
      .sortBy(_._2, ascending = false)
      .repartition(1)
      .saveAsTextFile("/Users/i851309/Downloads/wordCount")
  }

  def lineToTweetText(line: String): String = {
    val split = line.substring(1, line.length - 1).split("\",\"")
    split(5).toLowerCase
  }

}
