package com.twitteranalytics.spark.twitter

import java.time.{LocalDate, ZoneId}
import java.util.Date
import java.util.concurrent.TimeUnit

import com.twitteranalytics.spark.twitter.Base.{Keyword, _}
import org.apache.spark.sql.SparkSession
import org.mongodb.scala.bson.codecs.Macros._
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.mongodb.scala.bson.BsonDocument
import org.mongodb.scala.bson.collection.immutable.Document
import org.mongodb.scala.{MongoClient, MongoCollection, MongoDatabase}

import scala.collection.mutable
import scala.concurrent.Await
import scala.concurrent.duration.Duration

object TwitterSparkRDDMongoDB {

  case class CaseKeyword(keyword: String, sentiments: Sentiments, trends: mutable.Map[String, Sentiments], date: LocalDate) extends Serializable

  def twitter(spark: SparkSession): Unit = {
    val twitterData = "/Users/i851309/Downloads/twitter-dataset/training.1600000.processed.csv"
    val monitoredKeywords = List("Microsoft", "Google", "Oracle", "Computer", "Internet", "Facebook", "Movies", "Apple", "Android", "Brazil", "Bing", "Madonna", "Jackson").map(_.toLowerCase)

    val client: MongoClient = MongoClient()
    val database: MongoDatabase = client.getDatabase("twitteranalytics")
    val collection: MongoCollection[org.bson.BsonDocument] = database.getCollection("keywordAnalysis")

    val drop = collection.drop().toFuture()
    Await.result(drop, Duration(10, TimeUnit.SECONDS))

    val docs = spark.sparkContext.textFile(twitterData)
      .map(lineToTweet)
      .flatMap(tweet => {
        val keywords = mutable.Buffer[((String, LocalDate), Keyword)]()
        monitoredKeywords.foreach(monitoredKeyword => {
          if (tweet.text.contains(monitoredKeyword)) {
            val keyword = new Keyword(monitoredKeyword)
            keyword.date = tweet.date
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
            keywords.append(((keyword.keyword, keyword.date), keyword))
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
      .map(key => {
        val instant = key.date.atStartOfDay.atZone(ZoneId.systemDefault).toInstant

        val bsonDocument = new org.bson.BsonDocument()
        key.trends.foreach(t => {
          val sentiments = BsonDocument("positive" -> t._2.positive, "negative" -> t._2.negative, "neutral" -> t._2.neutral)
          bsonDocument.put(t._1, sentiments)
        })

        BsonDocument(
          "date" -> Date.from(instant),
          "keyword" -> key.keyword,
          "sentiments" -> Document("positive" -> key.sentiments.positive, "negative" -> key.sentiments.negative, "neutral" -> key.sentiments.neutral),
          "trends" -> bsonDocument,
          "_class" -> "com.twitteranalytics.web.domain.KeywordAnalysis"
        )
      })
      .collect()

      val future = collection.insertMany(docs).toFuture()
      Await.result(future, Duration(10, TimeUnit.SECONDS))
  }

  def main(args: Array[String]) {
    val spark = SparkSession.builder()
      .appName("experiments").master("local[4]")
      //      .config("spark.eventLog.enabled", value = true)
      .getOrCreate()

    spark.sparkContext.setLogLevel("WARN")
    val t0 = System.currentTimeMillis()
    twitter(spark)
    val elapsed = (System.currentTimeMillis() - t0) / 1000
    println("Elapsed time: " + elapsed + " seconds")
  }

}
