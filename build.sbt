scalaVersion := "2.12.1"

crossScalaVersions := Seq(
  "2.11.8"
)

lazy val commonSettings = Seq(
  organization := "org.zalando",
  scalacOptions ++= Seq("-feature", "-unchecked", "-deprecation")
)

lazy val core = project
  .settings(
    commonSettings,
    name := "scala-jsonapi-core",
    libraryDependencies ++= {
      Seq(
        "org.scalatest"     %% "scalatest"              % "3.0.1"      % "test"
      )
    }
  )

lazy val implicits = project
  .settings(
    commonSettings,
    name := "scala-jsonapi-implicits",
    libraryDependencies ++= {
      Seq(
        "org.scalatest"     %% "scalatest"              % "3.0.1"      % "test"
      )
    }
  )
  .dependsOn(core)

lazy val circe = project
  .settings(
    commonSettings,
    name := "scala-jsonapi-circe",
    libraryDependencies ++= {
      val circeVersion = "0.7.1"

      Seq(
        "io.circe"          %% "circe-core"             % circeVersion % "provided",
        "io.circe"          %% "circe-generic"          % circeVersion % "provided",
        "io.circe"          %% "circe-parser"           % circeVersion % "provided"
      )
    }
  )
  .dependsOn(core % "test->test;compile->compile")
