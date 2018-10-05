package com.twitteranalytics.spark.misc

import org.tartarus.snowball.ext.EnglishStemmer

object Scala {

  def main(args: Array[String]): Unit = {
    //    val person = new Person(5)
    //    println(person.name)
    //    person.name = "Hello"
    //    println(person.name)
    //    val r = person.funcName(t => t.toString)
    //    println(r)
    //    run()
    val stemmer = new EnglishStemmer()
    stemmer.setCurrent("pretty")
    println(stemmer.stem())
    println(stemmer.getCurrent)
  }

  def run(): Unit = {
    Seq("a", "a", "b", "c", "c")
      .groupBy(identity)
      .mapValues(_.length)
      .foreach(println)
  }

  def compute(fun: String => String, b: String): String = {
    fun.apply(b)
  }

  class Person(var _name: String) {

    var age: Int = _

    def this(age: Int) = {
      this("default")
      this.age = age
    }

    def name: String = {
      println("Getting the name")
      _name
    }

    def name_=(n: String): Unit = {
      println("Setting the name")
      _name = n
    }

    def funcName(func: ((String, String)) => String): String = {
      func((name, name))
    }

  }

}


