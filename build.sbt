organization := "org.zalando"

name := "scala-jsonapi"

scalaVersion := "2.11.12"

scalacOptions ++= Seq("-feature", "-unchecked", "-deprecation")

resolvers ++= Seq(
  "spray" at "http://repo.spray.io/",
  "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/"
)

libraryDependencies ++= {
  val circeVersion    = "0.7.1"
  val akkaVersion     = "2.4.20"
  val akkaHttpVersion = "2.4.11.2"
  val sprayVersion    = "1.3.4"

  Seq(
    "io.spray"          %% "spray-json"             % sprayVersion    % "provided",
    "io.spray"          %% "spray-httpx"            % sprayVersion    % "provided",
    "com.typesafe.akka" %% "akka-actor"             % akkaVersion     % "provided",
    "com.typesafe.akka" %% "akka-http-core"         % akkaHttpVersion % "provided",
    "com.typesafe.akka" %% "akka-http-experimental" % akkaHttpVersion % "provided",
    "com.typesafe.play" %% "play-json"              % "2.3.10"        % "provided",
    "io.circe"          %% "circe-core"             % circeVersion    % "provided",
    "io.circe"          %% "circe-generic"          % circeVersion    % "provided",
    "io.circe"          %% "circe-parser"           % circeVersion    % "provided",
    "org.scalatest"     %% "scalatest"              % "3.0.5"         % "test",
    "com.typesafe.akka" %% "akka-http-testkit"      % akkaHttpVersion % "test"
  )
}

lazy val root = (project in file("."))
  .enablePlugins(ScoverageSbtPlugin)

scalafmtConfig in ThisBuild := Some(file(".scalafmt"))

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
