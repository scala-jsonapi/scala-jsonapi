import scoverage.ScoverageSbtPlugin

import scalariform.formatter.preferences._
import com.typesafe.sbt.SbtScalariform.ScalariformKeys
import com.typesafe.sbt.SbtScalariform.scalariformSettings

organization := "org.zalando"

name := "scala-jsonapi"

scalaVersion := "2.11.8"

scalacOptions ++= Seq("-feature", "-unchecked", "-deprecation")

crossScalaVersions := Seq("2.11.8", "2.10.6")

resolvers ++= Seq(
  "spray" at "http://repo.spray.io/",
  "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/"
)

libraryDependencies ++= Seq(
  "io.spray"          %% "spray-json"  % "1.3.2"  % "provided",
  "io.spray"          %% "spray-httpx" % "1.3.3"  % "provided",
  "com.typesafe.akka" %% "akka-actor"  % "2.3.6"  % "provided",
  "com.typesafe.play" %% "play-json"   % "2.3.8"  % "provided",
  "com.typesafe.akka" %% "akka-http-spray-json-experimental" % "2.4.4" % "provided",
  "org.scalatest"     %% "scalatest"   % "2.2.4"  % "test"
)

scalariformSettings ++ Seq(
  ScalariformKeys.preferences := ScalariformKeys.preferences.value
    .setPreference(AlignSingleLineCaseStatements, true)
    .setPreference(DoubleIndentClassDeclaration, true)
    .setPreference(PreserveDanglingCloseParenthesis, true)
    .setPreference(PreserveSpaceBeforeArguments, true)
    .setPreference(RewriteArrowSymbols, true)
)

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
