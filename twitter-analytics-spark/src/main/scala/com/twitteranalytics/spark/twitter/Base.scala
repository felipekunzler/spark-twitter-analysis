package com.twitteranalytics.spark.twitter

import java.time.format.DateTimeFormatter
import java.time.{LocalDate, ZonedDateTime}

import edu.stanford.nlp.process.Stemmer

import scala.collection.mutable
import scala.io.Source

object Base {

  val Positive: Int = 4
  val Negative: Int = 0
  val StopWords = Source.fromFile("/Users/i851309/Downloads/stopwords.txt").getLines.toSet
  val DateFormat = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss z yyyy")

  implicit val LocalDateOrdering: Ordering[LocalDate] = Ordering.by(_.toEpochDay)

  case class Tweet(sentiment: Int, date: LocalDate, text: String)

  case class Sentiments(var positive: Int = 0, var negative: Int = 0, var neutral: Int = 0) extends Serializable {
    def total(): Int = positive + negative + neutral
  }

  case class Keyword(_keyword: String) extends Serializable {
    val sentiments = new Sentiments()
    val trends = mutable.Map[String, Sentiments]()
    var date: LocalDate = _

    def keyword: String = _keyword.toLowerCase

    override def toString: String = {
      val sentimentStr = (t: (String, Sentiments)) => s"(${t._1} -> ${t._2.total()} (+${t._2.positive} -${t._2.negative}))"
      val topWords = trends.toVector.sortWith(_._2.total > _._2.total).map(sentimentStr).slice(0, 20)
      s"Keyword($keyword, +${sentiments.positive}, -${sentiments.negative})\n" + topWords.mkString("\n") + "\n"
    }
  }

  def incrementSentiment(sentiments: Sentiments, sentiment: Int): Unit = {
    if (sentiment == Positive)
      sentiments.positive = sentiments.positive + 1
    else if (sentiment == Negative)
      sentiments.negative = sentiments.negative + 1
  }

  def lineToTweet(line: String): Tweet = {
    val split = line.substring(1, line.length - 1).split("\",\"")
    val sentiment = split(0).toInt
    val date = parseDate(split(2))

    Tweet(sentiment, date, split(5).toLowerCase)
  }

  def stem(word: String): String = {
    val stemmer = new Stemmer()
    stemmer.stem(word)
  }

  def parseDate(date: String): LocalDate = {
    ZonedDateTime.parse(date, DateFormat).toLocalDate
  }

}
