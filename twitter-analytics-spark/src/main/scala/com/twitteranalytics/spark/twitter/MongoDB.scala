package com.twitteranalytics.spark.twitter

import java.time.LocalDate
import java.util.Date
import java.util.concurrent.TimeUnit

import com.twitteranalytics.spark.twitter.Base.{Keyword, Sentiments}
import org.mongodb.scala.bson.BsonDocument
import org.mongodb.scala.bson.collection.immutable.Document
import org.mongodb.scala.{MongoClient, MongoCollection, MongoDatabase}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object MongoDB {

  def main(args: Array[String]) {

    class Ok(_name: String)

    val client: MongoClient = MongoClient()

    val database: MongoDatabase = client.getDatabase("twitteranalytics")//.withCodecRegistry(codecRegistry)
    val collection: MongoCollection[org.bson.BsonDocument] = database.getCollection("keywordAnalysis")

    val drop = collection.drop().toFuture()
    val res = Await.result(drop, Duration(10, TimeUnit.SECONDS))
    println(res)

    val key = Keyword("key")
    key.sentiments.negative = 1
    key.sentiments.positive = 2
    key.sentiments.neutral = 3
    key.trends.update("t1", Sentiments(1, 2, 3))
    key.trends.update("t2", Sentiments(2, 3, 1))
    key.date = LocalDate.now()

    import java.time.ZoneId
    val instant = key.date.atStartOfDay.atZone(ZoneId.systemDefault).toInstant

    val bsonDocument = new org.bson.BsonDocument()
    key.trends.foreach(t => {
      val sentiments = BsonDocument("positive" -> t._2.positive, "negative" -> t._2.negative, "neutral" -> t._2.neutral)
      bsonDocument.put(t._1, sentiments)
    })

    val doc = BsonDocument(
      "date" -> Date.from(instant),
      "keyword" -> key.keyword,
      "sentiments" -> Document("positive" -> key.sentiments.positive, "negative" -> key.sentiments.negative, "neutral" -> key.sentiments.neutral),
      "trends" -> bsonDocument,
      "_class" -> "com.twitteranalytics.web.domain.KeywordAnalysis"
    )

    val future = collection.insertOne(doc).toFuture()
    Await.result(future, Duration(10, TimeUnit.SECONDS))

    client.close()
  }

}
