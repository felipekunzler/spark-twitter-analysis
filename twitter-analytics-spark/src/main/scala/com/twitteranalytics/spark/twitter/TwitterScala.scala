package com.twitteranalytics.spark.twitter

import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.GetObjectRequest
import com.twitteranalytics.spark.twitter.Base._

import scala.collection.{Iterator, mutable}
import scala.io.Source

object TwitterScala {

  def run(dataPath: String, dataMultiplierFactor: Int) = {
    val keywords = List("Microsoft", "Google", "Oracle", "Computer", "Internet", "Facebook", "Movies").map(new Keyword(_))

    for (i <- 1 to dataMultiplierFactor) {
      println(s"Pass number $i")

      for (line <- getDataLines(dataPath)) {
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
                val sentiments = keyword.trends.getOrElse(word, new Sentiments)
                incrementSentiment(sentiments, tweet.sentiment)
                keyword.trends.update(word, sentiments)
              }
          }
        }
      }
    }
    keywords.foreach(println)
  }

  def main(args: Array[String]): Unit = {
    println("Running with arguments: " + args.mkString(", "))
    val data = getOrDefault(args, 0, "/Users/i851309/Downloads/twitter-dataset/training.1600000.processed.csv")
    val multiplier = getOrDefault(args, 1, "1").toInt
    val start = System.currentTimeMillis()
    run(data, multiplier)
    mutable.Map[String, Int]()
    val elapsed = (System.currentTimeMillis() - start) / 1000d
    println(s"Elapsed time: $elapsed seconds")
  }

  def getDataLines(dataPath: String): Iterator[String] = {
    if (dataPath == "s3") {
      val s3 = new AmazonS3Client(new BasicAWSCredentials("myKey", "mySecretKey"))
      val s3Object = s3.getObject(new GetObjectRequest("felipekunzler", "training.1600000.processed.csv"))
      Source.fromInputStream(s3Object.getObjectContent, "ISO-8859-1").getLines
    } else {
      Source.fromFile(dataPath, enc = "ISO-8859-1").getLines()
    }
  }

}
