# scala-jsonapi WIP

[![Build Status](https://travis-ci.org/zalando/scala-jsonapi.svg)](https://travis-ci.org/zalando/scala-jsonapi)
[![Coverage Status](https://coveralls.io/repos/zalando/scala-jsonapi/badge.svg?branch=master&service=github)](https://coveralls.io/github/zalando/scala-jsonapi?branch=master)

## Description

A Scala library for producing JSON output based on [JSON API specification](http://jsonapi.org/). 

### Current status

At the moment there is only support for _data_ part of the root object and both mandatory attributes _type_ and _id_.

Library is very much Work-In-Progress and expect API to change.

Currently library supports Scala version 2.11.

## Setup

In order to use current version _scala-jsonapi_ you have to add library dependency assuming that you have [sonatype resolvers](http://www.scala-sbt.org/0.13/docs/Resolvers.html#Maven) set up.

    libraryDependencies += "org.zalando" %% "scala-jsonapi" % "0.1.0"

## Usage

See [the specs example](src/test/scala/org/zalando/jsonapi/json/JsonapiJsonFormatSpec.scala).

## Publishing and Releasing

For publishing and releasing is made with help of [sbt-sonatype plugin](https://github.com/xerial/sbt-sonatype)