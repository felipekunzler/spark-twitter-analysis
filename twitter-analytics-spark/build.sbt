name := "spark-twitter-analytics"
version := "1.0"
scalaVersion := "2.11.12"

val sparkVersion = "2.3.1"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion,
  "org.apache.spark" %% "spark-streaming" % sparkVersion,
  "org.apache.spark" %% "spark-mllib" % sparkVersion
)