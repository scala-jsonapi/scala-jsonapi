# Description

[![Build Status](https://travis-ci.org/zalando/scala-jsonapi.svg)](https://travis-ci.org/zalando/scala-jsonapi)
[![Coverage Status](https://coveralls.io/repos/zalando/scala-jsonapi/badge.svg?branch=master&service=github)](https://coveralls.io/github/zalando/scala-jsonapi?branch=master)
[![Join the chat at https://gitter.im/zalando/scala-jsonapi](https://badges.gitter.im/zalando/scala-jsonapi.svg)](https://gitter.im/zalando/scala-jsonapi?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

A Scala library for producing JSON output based on [JSON API specification](http://jsonapi.org/). 

## Current status

The library supports read and write for both Play-JSON and Spray-JSON.

Library is very much Work-In-Progress and expect API to change.

Currently library supports Scala version `2.11` and `2.10`.

# Setup

In order to use current version _scala-jsonapi_ you have to add library dependency assuming that you have [sonatype resolvers](http://www.scala-sbt.org/0.13/docs/Resolvers.html#Maven) set up.

    libraryDependencies += "org.zalando" %% "scala-jsonapi" % "0.3.1"

# Usage

The rich JSON API model is available by following import:

    import org.zalando.jsonapi.model._
    
Yhe library provides serialization and deserialization of Jsonapi root objects to JSON using either Spray-JSON or Play-JSON. 
Please note that you need to explicitly add a dependency to either spray-json or play-json to your project.

## Spray-JSON

    import org.zalando.jsonapi.json.sprayjson.SprayJsonJsonapiProtocol._
    import spray.json._
    
    // Serialization
    val rootObject: RootObject = ???
    rootObject.toJson
    
    // Deserialization
    val json: JsValue = ???
    json.convertTo[RootObject]

## Play-JSON

    import org.zalando.jsonapi.json.playjson.PlayJsonJsonapiProtocol._
    import play.api.libs.json._
    
    // Serialization
    val rootObject: RootObject = ???
    Json.toJson(rootObject)
    
    // Deserialization
    val json: JsValue = ???
    Json.fromJson[RootObject](json)

## Creating JSON API root object

The library provides type class `JsonapiRootEntityWriter` in order to enable you to create JSON API representation for your resources.
Following code snippet demonstrate the usage of that:

    import org.zalando.jsonapi
    import jsonapi.Jsonapi
    
    case class Person(name: String)
    
    implicit val personJsonapiWriter = new JsonapiRootObjectWriter[Person] {
      override def toJsonapi(person: Person) = {
        ???
      }
    }
    
    val personRootObject: RootObject = Jsonapi.asJsonapi(Person("Boris M."))

For complete usage see [the specs example](src/test/scala/org/zalando/jsonapi/json/ExampleSpec.scala).

# Publishing and Releasing

Publishing and releasing is made with help of [sbt-sonatype plugin](https://github.com/xerial/sbt-sonatype)