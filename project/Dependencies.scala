import sbt._

object Dependencies {
  // Versions
  lazy val circeVersion = "0.7.0"
  lazy val akkaVersion = "2.4.8"
  lazy val sprayJsonVersion = "1.3.2"
  lazy val sprayHttpxVersion = "1.3.3"
  lazy val playJsonVersion = "2.3.10"
  lazy val scalatestVersion = "3.0.0"

  // Libraries
  lazy val sprayJson = "io.spray" %% "spray-json" % sprayJsonVersion
  lazy val sprayHttpx = "io.spray" %% "spray-httpx" % sprayHttpxVersion
  lazy val akkaActor = "com.typesafe.akka" %% "akka-actor" % akkaVersion
  lazy val akkaHttpCore = "com.typesafe.akka" %% "akka-http-core" % akkaVersion
  lazy val akkaHttpExperimental = "com.typesafe.akka" %% "akka-http-experimental" % akkaVersion
  lazy val playJson = "com.typesafe.play" %% "play-json" % playJsonVersion
  lazy val circeCore = "io.circe" %% "circe-core" % circeVersion
  lazy val circeGeneric = "io.circe" %% "circe-generic" % circeVersion
  lazy val circeParser = "io.circe" %% "circe-parser" % circeVersion
  lazy val scalaTest = "org.scalatest" %% "scalatest" % scalatestVersion % "test"
  lazy val akkaHttpTestkit = "com.typesafe.akka" %% "akka-http-testkit" % akkaVersion % "test"

  // Projects
  lazy val coreDeps = Seq(scalaTest)

  lazy val sprayDeps = Seq(sprayHttpx, sprayJson, akkaActor, scalaTest)

  lazy val circeDeps = Seq(circeCore, circeGeneric, circeParser, sprayJson, sprayHttpx, akkaActor, scalaTest)

  lazy val sprayJsonDeps = Seq(sprayJson)

  lazy val akkaHttpDeps = Seq(akkaHttpCore, akkaHttpExperimental, akkaHttpTestkit, sprayJson)
}
