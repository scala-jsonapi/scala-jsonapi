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

lazy val circe = project.in(file("circe")).
  dependsOn(spray, core % "test->test;compile->compile").
  settings(moduleName := "scala-jsonapi-circe").
  settings(commonSettings: _*).
  settings(libraryDependencies ++= circeDeps)

ScoverageSbtPlugin.ScoverageKeys.coverageMinimum := 80

ScoverageSbtPlugin.ScoverageKeys.coverageFailOnMinimum := true

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
