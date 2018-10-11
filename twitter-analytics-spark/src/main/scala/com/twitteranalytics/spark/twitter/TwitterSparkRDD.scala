package com.twitteranalytics.spark.twitter

import com.twitteranalytics.spark.twitter.Base._
import org.apache.spark.sql.SparkSession

import scala.collection.mutable

object TwitterSparkRDD {

  def twitter(spark: SparkSession): Unit = {
    val twitterData = "/Users/i851309/Downloads/training.1600000.processed.noemoticon.csv"
    val monitoredKeywords = List("Microsoft", "Google", "Oracle", "Computer", "Internet", "Facebook", "Movies").map(_.toLowerCase)

    spark.sparkContext.textFile(twitterData)
      .map(lineToTweet)
      .flatMap(tweet => {
        val keywords = mutable.Buffer[(String, Keyword)]()
        monitoredKeywords.foreach(monitoredKeyword => {
          if (tweet.text.contains(monitoredKeyword)) {
            val keyword = new Keyword(monitoredKeyword)
            incrementSentiment(keyword.sentiments, tweet.sentiment)
            tweet.text.split(" ")
              .map(_.replaceAll("\\W+", ""))
              .map(stem)
              .filter(_ != stem(monitoredKeyword))
              .filter(_.length > 3)
              .filter(w => !StopWords.contains(w))
              .foreach { word =>
                val sentiments = keyword.trends.getOrElse(word, new Sentiments)
                incrementSentiment(sentiments, tweet.sentiment)
                keyword.trends.update(word, sentiments)
              }
            keywords.append((keyword.keyword, keyword))
          }
        })
        keywords
      })
      .reduceByKey((a, b) => {
        a.sentiments.positive += b.sentiments.positive
        a.sentiments.negative += b.sentiments.negative
        b.trends.keys.foreach(k => {
          val sentiments = a.trends.getOrElse(k, new Sentiments())
          sentiments.positive += b.trends(k).positive
          sentiments.negative += b.trends(k).negative
          a.trends.update(k, sentiments)
        })
        a
      })
      .map(_._2)
      .foreach(println)
  }

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

}
