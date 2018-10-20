name := "spark-twitter-analytics"
version := "1.0"
scalaVersion := "2.11.12"

val sparkVersion = "2.3.1"

// %% = appends scala version in dependency
libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion % "provided",
  "org.apache.spark" %% "spark-streaming" % sparkVersion % "provided",
  "org.apache.spark" %% "spark-mllib" % sparkVersion % "provided",
  "edu.stanford.nlp" % "stanford-corenlp" % "3.9.1",
  "org.mongodb.scala" %% "mongo-scala-driver" % "2.4.2"
)

assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false)
