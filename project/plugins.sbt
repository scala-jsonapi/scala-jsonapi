resolvers += Classpaths.sbtPluginReleases

addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.0.4")

addSbtPlugin("org.scoverage" % "sbt-coveralls" % "1.0.0")

// Plugin for code formatting:
addSbtPlugin("com.typesafe.sbt" % "sbt-scalariform" % "1.3.0")