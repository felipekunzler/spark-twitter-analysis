package com.twitteranalytics.spark.twitter

import com.twitteranalytics.spark.twitter.Base._

import scala.collection.mutable
import scala.io.Source

object TwitterScala {

  def run() = {
    val twitterData = "/Users/i851309/Downloads/training.1600000.processed.noemoticon.csv"
    val keywords = List("Microsoft", "Google", "Oracle", "Computer", "Internet", "Facebook", "Movies", "Bing").map(new Keyword(_))

    for (line <- Source.fromFile(twitterData, enc = "ISO-8859-1").getLines()) {
      val tweet = lineToTweet(line)
      for (keyword <- keywords) {
        if (tweet.text.contains(keyword.keyword)) {
          incrementSentiment(keyword.sentiments, tweet.sentiment)
          tweet.text.split(" ")
            .map(_.replaceAll("\\W+", ""))
            .map(stem)
            .filter(_ != stem(keyword.keyword))
            .filter(_.length > 3)
            .filter(w => !StopWords.contains(w))
            .foreach { word =>
              val sentiments = keyword.wordCount.getOrElse(word, new Sentiments)
              incrementSentiment(sentiments, tweet.sentiment)
              keyword.wordCount.update(word, sentiments)
            }
        }
      }
    }
    keywords.foreach(println)
  }

  def main(args: Array[String]): Unit = {
    val start = System.currentTimeMillis()
    run()
    mutable.Map[String, Int]()
    val elapsed = (System.currentTimeMillis() - start) / 1000
    println(s"Elapsed time: $elapsed seconds")
  }

}
