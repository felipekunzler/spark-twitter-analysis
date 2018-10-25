package com.twitteranalytics.spark.misc

import org.apache.spark.sql.SparkSession

object TCC {

  case class Person(name: String, var birth: Long = 0, var age: Long)

  def main(args: Array[String]): Unit = {
    //val conf = new SparkConf().set("spark.eventLog.enabled", "true")
    //val spark = new SparkContext(master = "local[4]", appName = "sparkJob", conf)

    val spark = SparkSession.builder().master("local[*]").getOrCreate()
    val sc = spark.sparkContext
    //
    //    val bigMap = Map("a" -> 1, "b" -> 3, "c" -> 55)
    //    val mapBroadcast = sc.broadcast(bigMap)
    //
    //    val accumulator = sc.longAccumulator("even count")
    //
    //    val nums = sc.parallelize(seq = 1 to 40, numSlices = 4)
    //    //nums.foreach(n => if (n % 2 == 0) accumulator.add(1))
    //
    //    //println(accumulator.value) // 20


    val df = spark.read.json("/Users/i851309/Desktop/people.json")
    df.show()
    df.printSchema()
    import spark.implicits._
    val ds = df.as[Person]
    ds.map(p => Person(p.name, p.birth, 2018 - p.age))
      .filter(_.age < 50)
      .groupByKey(_.age)
      .count()
      .show()


  }

}
