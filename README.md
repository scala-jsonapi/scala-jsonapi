# Description

[![Build Status](https://travis-ci.org/zalando/scala-jsonapi.svg)](https://travis-ci.org/zalando/scala-jsonapi)
[![Coverage Status](https://coveralls.io/repos/zalando/scala-jsonapi/badge.svg?branch=master&service=github)](https://coveralls.io/github/zalando/scala-jsonapi?branch=master)
[![codecov.io](https://codecov.io/github/zalando/scala-jsonapi/coverage.svg?branch=master)](https://codecov.io/github/zalando/scala-jsonapi?branch=master)
[![Join the chat at https://gitter.im/zalando/scala-jsonapi](https://badges.gitter.im/zalando/scala-jsonapi.svg)](https://gitter.im/zalando/scala-jsonapi?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

A Scala library for producing JSON output based on [JSON API specification][jsonapi]. 

## Current status

The library supports read and write for following backends:

 * [Play-JSON]
 * [Spray-JSON]
 * [Circe]
 
Besides the supported backends, the library provides out-of-the-box (un)marshallers for:

 * Spray and Play
 * Akka Http

Library is very much Work-In-Progress and expect API to change.

Currently library supports Scala version `2.11`.

# Setup

In order to use current version _scala-jsonapi_ you have to add library dependency assuming that you have [sonatype resolvers] set up.

    libraryDependencies += "org.zalando" %% "scala-jsonapi" % "0.5.0"

As a dependency you have to provide also the used backend (e.g. spray-json).

# Usage

The rich JSON API model is available by following import:

    import org.zalando.jsonapi.model._
    
The library provides serialization and deserialization of Jsonapi root objects to JSON using either Spray-JSON or Play-JSON. 
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

In contrast there is a type class called `JsonapiRootEntityReader` which support to convert from JSON API representation to your resources.
Following code snippet demonstrate the usage of that:

    import org.zalando.jsonapi
    import jsonapi.Jsonapi
    
    case class Person(name: String)
    
    implicit val personJsonapiReader = new JsonapiRootObjectReader[Person] {
      override def fromJsonapi(rootObject: RootObject) = {
        ???
      }
    }
    
    val person: Person = Jsonapi.fromJsonapi[Person](RootObject(...))

For complete usage see [the specs example].

# Publishing and Releasing

Publishing and releasing is made with help of [sbt-sonatype plugin]

# License

_scala-jsonapi_ is licensed under [MIT][MIT license].

[sbt-sonatype plugin]: https://github.com/xerial/sbt-sonatype
[the specs example]: src/test/scala/org/zalando/jsonapi/json/ExampleSpec.scala
[sonatype resolvers]: http://www.scala-sbt.org/0.13/docs/Resolvers.html#Maven
[jsonapi]: http://jsonapi.org/
[Play-JSON]: https://www.playframework.com/documentation/2.5.x/ScalaJson
[Spray-JSON]: https://github.com/spray/spray-json
[Circe]: https://github.com/travisbrown/circe
[MIT license]: https://opensource.org/licenses/MIT