name := "spark-twitter-analytics"
version := "1.0"
scalaVersion := "2.11.12"

val sparkVersion = "2.3.1"

// %% = appends scala version in dependency
libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion,
  "org.apache.spark" %% "spark-streaming" % sparkVersion,
  "org.apache.spark" %% "spark-mllib" % sparkVersion,
  "edu.stanford.nlp" % "stanford-corenlp" % "3.9.1",
  "org.mongodb.scala" %% "mongo-scala-driver" % "2.4.2",
  "org.apache.hadoop" % "hadoop-aws" % "2.7.7"
    exclude("javax.servlet",     "servlet-api")
    exclude("javax.servlet.jsp", "jsp-api")
    exclude("org.mortbay.jetty", "servlet-api")
)

//retrieveManaged := true -- generates lib folder with all dependency jars

//mainClass in assembly := Some("com.twitteranalytics.spark.twitter.TwitterScala")
mainClass in assembly := Some("com.twitteranalytics.spark.twitter.TwitterSparkRDD")

assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false)

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}
