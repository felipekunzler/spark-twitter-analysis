package com.sparktwitter

import org.apache.spark.{SparkConf, SparkContext}

object Main {

  def main(args: Array[String]) {
    val config = new SparkConf().setAppName("twitter-stream-sentiment").setMaster("local[2]")
    val sc = new SparkContext(config)
    sc.setLogLevel("WARN")
    sc.textFile("in/data.txt")
      .flatMap(s => s.split(" "))
      .map(s => (s, 1))
      .reduceByKey(_ + _)
      .saveAsTextFile("target/out/main")
  }

}
