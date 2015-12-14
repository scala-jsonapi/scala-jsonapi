# scala-jsonapi WIP

[![Build Status](https://travis-ci.org/zalando/scala-jsonapi.svg)](https://travis-ci.org/zalando/scala-jsonapi)
[![Coverage Status](https://coveralls.io/repos/zalando/scala-jsonapi/badge.svg?branch=master&service=github)](https://coveralls.io/github/zalando/scala-jsonapi?branch=master)

## Description

A Scala library for producing JSON output based on [JSON API specification](http://jsonapi.org/). 

### Current status

At the moment the library supports all object conversions to JSON (write) but it does not yet support from JSON to objects (read).

Library is very much Work-In-Progress and expect API to change.

Currently library supports Scala version `2.11` and `2.10`.

## Setup

In order to use current version _scala-jsonapi_ you have to add library dependency assuming that you have [sonatype resolvers](http://www.scala-sbt.org/0.13/docs/Resolvers.html#Maven) set up.

    libraryDependencies += "org.zalando" %% "scala-jsonapi" % "0.2.1"

## Usage

See [the specs example](src/test/scala/org/zalando/jsonapi/json/ExampleSpec.scala).

## Publishing and Releasing

Publishing and releasing is made with help of [sbt-sonatype plugin](https://github.com/xerial/sbt-sonatype)