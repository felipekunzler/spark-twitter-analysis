package com.twitteranalytics.spark.twitter

import com.twitteranalytics.spark.twitter.Base._
import org.apache.spark.sql.SparkSession

import scala.collection.mutable

object TwitterSparkRDD {

  val MonitoredKeywords = List("Microsoft", "Google", "Oracle", "Computer", "Internet", "Facebook", "Movies").map(_.toLowerCase)

  def twitter(spark: SparkSession, dataPath: String, dataMultiplierFactor: Int) : Unit = {

    spark.sparkContext.textFile(List.fill(dataMultiplierFactor)(dataPath).mkString(","))
      .map(lineToTweet)
      .filter(matchesAnyKeyword)
      .map(t => t) // makes sense to become keyword? it doesn have a keyword..
      .flatMap(tweet => {
        val keywords = mutable.Buffer[(String, Keyword)]()
        MonitoredKeywords.foreach(monitoredKeyword => {
          if (tweet.text.contains(monitoredKeyword)) {
            val keyword = Keyword(monitoredKeyword)
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
          val sentiments = a.trends.getOrElse(k, Sentiments())
          sentiments.positive += b.trends(k).positive
          sentiments.negative += b.trends(k).negative
          a.trends.update(k, sentiments)
        })
        a
      })
      .map(_._2)
      .coalesce(1)
      //.foreach(println)
      .saveAsTextFile(s"twitter-output/job-${System.currentTimeMillis()}")
  }

  def matchesAnyKeyword(tweet: Tweet): Boolean = {
    for (keyword <- MonitoredKeywords) {
      if (tweet.text.contains(keyword))
        return true
    }
    false
  }

  def main(args: Array[String]) {
    println("Running with arguments: " + args.mkString(", "))
    val data = getOrDefault(args, 0, "/Users/i851309/projects/spark-scala/projeto-integrador/dataset/twitter-dataset/training.1600000.processed.csv")
    val multiplier = getOrDefault(args, 1, "1").toInt

    val spark = SparkSession.builder()
      .appName("twitteranalysis")
      //.master("local[4]")
      //.config("spark.eventLog.enabled", value = true)
      .getOrCreate()

    spark.sparkContext.setLogLevel("WARN")
    val t0 = System.currentTimeMillis()
    twitter(spark, data, multiplier)
    val elapsed = (System.currentTimeMillis() - t0) / 1000d
    println("Elapsed time: " + elapsed + " seconds")
  }

}
