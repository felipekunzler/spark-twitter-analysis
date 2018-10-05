package com.twitteranalytics.spark.misc

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import com.twitteranalytics.spark.twitter.TwitterScala.{Tweet, parseDate}
import edu.stanford.nlp.process.Stemmer
import org.apache.spark.sql.SparkSession
import org.tartarus.snowball.ext.EnglishStemmer

import scala.io.Source

object Main {

  val Data = "/Users/i851309/Documents/TC/projects/Spark-The-Definitive-Guide/"

  def main(args: Array[String]) {
    val t0 = System.currentTimeMillis()
    val spark = SparkSession.builder()
      .appName("experiments").master("local[*]")
      .config("spark.eventLog.enabled", value = false)
      .getOrCreate()
    spark.sparkContext.setLogLevel("WARN")

    val elapsed = (System.currentTimeMillis() - t0) / 1000
    println("Elapsed time: " + elapsed + " seconds")
  }

  case class Flight(DEST_COUNTRY_NAME: String, ORIGIN_COUNTRY_NAME: String, count: BigInt)

  def datasets(spark: SparkSession): Unit = {
    import spark.implicits._
    val flightsDF = spark.read.parquet(Data + "data/flight-data/parquet/2010-summary.parquet/")
    val flights = flightsDF.as[Flight]
    flights.show(5)
    flights.first().ORIGIN_COUNTRY_NAME
  }

  def rdd(spark: SparkSession): Unit = {
    def indexedFunc(partitionIndex: Int, withinPartIterator: Iterator[String]) = {
      withinPartIterator.toList
        .map(value => s"Partition: $partitionIndex => $value")
        .iterator
    }

    val values = List("a1 a2 a3 b1 b2")
    val words = spark.sparkContext.parallelize(values, 2).flatMap(_.split(" "))

    words.aggregate(0)(
      (acc, _) => acc + 1, // within the partition
      (a1, a2) => a1 + a2 // reduce all results
    )

  }

  def pairRDD(spark: SparkSession): Unit = {
    val sc = spark.sparkContext
    sc.parallelize(List("a1", "a2", "b1", "b2", "a3"))
      .map(s => (s.charAt(0), s))
      .groupByKey()
      .mapValues(_.size)
      .foreach(println)
  }

  def dataset(spark: SparkSession): Unit = {
    import spark.implicits._
    val r = spark.createDataset(List("a1", "a2", "b1", "b2", "a3"))
      .map(s => (s.charAt(0).toString, 1))
      .groupByKey(_._1)
      .reduceGroups((a, b) => (a._1, 0 + a._2 + b._2))
      .show()

    println(r)
  }

  case class Tweet(sentiment: Int, id: Long, date: String, what: String, user: String, text: String)

  private val DateFormat = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss z yyyy")
  implicit val LocalDateOrdering: Ordering[LocalDate] = Ordering.by(_.toEpochDay)

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

    val result = ds
      .limit(1000)
      .groupByKey(t => t.user)
      .count()
      .filter(_._2 >= 2)
      .foreach(kv => println(kv))

    // reduce by key with datasets???

    println("Finished: " + result)
  }

}
