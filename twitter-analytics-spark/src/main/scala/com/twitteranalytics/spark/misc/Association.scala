package com.twitteranalytics.spark.misc

import org.apache.spark.ml.fpm.FPGrowth
import org.apache.spark.sql.SparkSession

object Association {

  def main(args: Array[String]) {
    val spark = SparkSession.builder().appName("association").master("local[2]").getOrCreate()
    spark.sparkContext.setLogLevel("WARN")
    import spark.implicits._
    val dataset = spark.createDataset(Seq(
      "1 2 5",
      "1 2 3 5",
      "1 2")
    ).map(t => t.split(" ")).toDF("items")

    val fpgrowth = new FPGrowth().setItemsCol("items").setMinSupport(0.5).setMinConfidence(0.6)
    val model = fpgrowth.fit(dataset)

    // Display frequent itemsets.
    model.freqItemsets.show()

    // Display generated association rules.
    model.associationRules.show()

    // transform examines the input items against all the association rules and summarize the
    // consequents as prediction
    model.transform(dataset).show()
  }

}
