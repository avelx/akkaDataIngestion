name := "akkaDataIngestor"

version := "0.1"

scalaVersion := "2.13.3"

val AkkaVersion = "2.6.8"
val AkkaHttpVersion = "10.2.0"

libraryDependencies += "com.typesafe.akka" %% "akka-stream" % AkkaVersion
libraryDependencies += "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion
libraryDependencies += "com.typesafe.akka" %% "akka-actor-testkit-typed" % AkkaVersion % Test
libraryDependencies += "com.typesafe.akka" %% "akka-stream-testkit" % AkkaVersion % Test
libraryDependencies += "org.scalatest" %% "scalatest" % "3.1.1" % Test
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3"
libraryDependencies += "com.typesafe.akka" %% "akka-http-spray-json" % AkkaHttpVersion
libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.14.1"
libraryDependencies ++= Seq(
  "com.lightbend.akka" %% "akka-stream-alpakka-hdfs" % "2.0.2",
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion
)
libraryDependencies ++= Seq(
  "com.lightbend.akka" %% "akka-stream-alpakka-file" % "2.0.2"
)