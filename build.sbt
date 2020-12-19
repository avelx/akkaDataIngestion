
name := "akkaDataIngestor"

version := "0.1"

scalaVersion := "2.13.3"

val AkkaVersion = "2.6.8"
val AkkaHttpVersion = "10.2.0"
val hadoopVersion = "3.1.0"

libraryDependencies += "com.typesafe.akka" %% "akka-stream" % AkkaVersion
libraryDependencies += "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion
libraryDependencies += "com.typesafe.akka" %% "akka-actor-testkit-typed" % AkkaVersion % Test
libraryDependencies += "com.typesafe.akka" %% "akka-stream-testkit" % AkkaVersion % Test
libraryDependencies += "org.scalatest" %% "scalatest" % "3.1.1" % Test
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3"
libraryDependencies += "com.typesafe.akka" %% "akka-http-spray-json" % AkkaHttpVersion
libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.14.1"
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % AkkaVersion
libraryDependencies += "com.lightbend.akka" %% "akka-stream-alpakka-file" % "2.0.2"
libraryDependencies += "com.lightbend.akka" %% "akka-stream-alpakka-hdfs" % "2.0.2"
libraryDependencies += "com.lightbend.akka" %% "akka-stream-alpakka-cassandra" % "2.0.2"
libraryDependencies += "software.aws.mcs" % "aws-sigv4-auth-cassandra-java-driver-plugin" % "4.0.3"
libraryDependencies += "org.apache.hadoop" % "hadoop-common" % hadoopVersion % Test classifier "tests"
libraryDependencies += "org.apache.hadoop" % "hadoop-client" % "3.2.1"


val circeVersion = "0.12.3"

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)

mainClass in assembly := Some("HdfsPersistance")

assemblyJarName in assembly := "data-ingestor.jar"

assemblyOption in assembly :=
  (assemblyOption in assembly).value.copy(includeScala = true, includeDependency = true)

test in assembly := {}

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs@_*) => MergeStrategy.discard
  case x => MergeStrategy.first
}

libraryDependencies += "io.kamon" %% "kamon-core" % "2.1.8"
libraryDependencies += "io.kamon" %% "kamon-akka" % "2.1.8"
libraryDependencies += "io.kamon" %% "kamon-prometheus" % "2.1.8"
libraryDependencies += "io.kamon" %% "kamon-logback" % "2.1.8"