package com.twitteranalytics.spark.misc

import org.apache.spark.{SparkConf, SparkContext}

object TCC {

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().set("spark.eventLog.enabled", "true")
    val spark = new SparkContext(master = "local[4]", appName = "sparkJob", conf)

    val bigMap = Map("a" -> 1, "b" -> 3, "c" -> 55)
    val mapBroadcast = spark.broadcast(bigMap)

    val accumulator = spark.longAccumulator("even count")

    val nums = spark.parallelize(seq = 1 to 40, numSlices = 4)
    nums.foreach(n => if (n % 2 == 0) accumulator.add(1))

    println(accumulator.value) // 20
  }

}
