import scoverage.ScoverageSbtPlugin
import Dependencies._

scalaVersion := "2.11.8"

lazy val commonSettings = Seq(
  organization := "org.zalando",
  scalafmtConfig in ThisBuild := Some(file(".scalafmt")),
  scalaVersion := "2.11.8",
  scalacOptions ++= Seq(
    "-feature",
    "-unchecked",
    "-deprecation",
    "-encoding", "UTF-8"
  )
)

lazy val core = project.in(file("core")).
  settings(moduleName := "scala-jsonapi-core").
  settings(commonSettings: _*).
  settings(libraryDependencies ++= coreDeps)

lazy val spray = project.in(file("spray")).
  dependsOn(core % "test->test;compile->compile").
  settings(moduleName := "scala-jsonapi-spray").
  settings(commonSettings: _*).
  settings(libraryDependencies ++= sprayDeps)

lazy val `spray-json` = project.in(file("spray-json")).
  dependsOn(core % "test->test;compile->compile", spray).
  settings(moduleName := "scala-jsonapi-spray-json").
  settings(commonSettings: _*).
  settings(libraryDependencies ++= sprayJsonDeps)

lazy val circe = project.in(file("circe")).
  dependsOn(core % "test->test;compile->compile", spray).
  settings(moduleName := "scala-jsonapi-circe").
  settings(commonSettings: _*).
  settings(libraryDependencies ++= circeDeps)

lazy val `akka-http` = project.in(file("akka-http")).
  dependsOn(core % "test->test;compile->compile", `spray-json`).
  settings(moduleName := "scala-jsonapi-akka-http").
  settings(commonSettings: _*).
  settings(libraryDependencies ++= akkaHttpDeps)

lazy val play = project.in(file("play")).
  dependsOn(core % "test->test;compile->compile").
  settings(moduleName := "scala-jsonapi-play").
  settings(commonSettings: _*).
  settings(libraryDependencies ++= playDeps)

coverageMinimum := 80

coverageFailOnMinimum := true

publishMavenStyle := true

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

sonatypeProfileName := "org.zalando"

pomExtra := (
  <url>https://github.com/zalando/scala-jsonapi</url>
  <licenses>
    <license>
      <name>MIT</name>
      <url>http://opensource.org/licenses/MIT</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <url>git@github.com:zalando/scala-jsonapi.git</url>
    <connection>scm:git:git@github.com:zalando/scala-jsonapi.git</connection>
  </scm>
  <developers>
    <developer>
      <id>zmeda</id>
      <name>Boris Malensek</name>
      <url>https://github.com/zmeda</url>
    </developer>
  </developers>)
