# Description

[![Build Status](https://travis-ci.org/zalando/scala-jsonapi.svg)](https://travis-ci.org/zalando/scala-jsonapi)
[![Coverage Status](https://coveralls.io/repos/zalando/scala-jsonapi/badge.svg?branch=master&service=github)](https://coveralls.io/github/zalando/scala-jsonapi?branch=master)
[![codecov.io](https://codecov.io/github/zalando/scala-jsonapi/coverage.svg?branch=master)](https://codecov.io/github/zalando/scala-jsonapi?branch=master)
[![Join the chat at https://gitter.im/zalando/scala-jsonapi](https://badges.gitter.im/zalando/scala-jsonapi.svg)](https://gitter.im/zalando/scala-jsonapi?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

scala-jsonapi is a Scala library that aims to help you produce JSON output based on the [JSON API specification][jsonapi] easily and painlessly. The library is compatible with Scala version `2.11`. It supports read and write for the following backends:

 * [Play-JSON]
 * [Spray-JSON]
 * [Circe]
 
In addition, scala-jsonapi provides out-of-the-box (un)marshallers for:

 * Spray and Play
 * Akka Http

## Current Status
This library is very much a work in progress, so expect its API to change. 

# Setup

To use scala-jsonapi, first add a library dependency—assuming that you have [sonatype resolvers] set up.

    libraryDependencies += "org.zalando" %% "scala-jsonapi" % "0.5.2"

You also have to provide the used backend (e.g. spray-json).

# Usage

The rich JSON API model is available via the following import:

    import org.zalando.jsonapi.model._
    
The library provides serialization and deserialization of JSON API root objects to JSON using either Spray-JSON or Play-JSON. Please note that you need to explicitly add a dependency to either spray-json or play-json to your project.

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

Along with adding the scala-jsonapi library dependency, also include spray-httpx for marshalling support

    libraryDependencies += "io.spray" %% "spray-httpx" % "1.3.3"

The JSON API support can then be imported using `PlayJsonJsonapiSupport` as follows

    import org.zalando.jsonapi.json.playjson.PlayJsonJsonapiSupport._
    import play.api.libs.json._
    
    // Serialization
    val rootObject: RootObject = ???
    Json.toJson(rootObject)
    
    // Deserialization
    val json: JsValue = ???
    Json.fromJson[RootObject](json)

## Creating a JSON API Root Object

scala-jsonapi provides type class `JsonapiRootEntityWriter` so that you can create a JSON API representation for your resources. The following code snippet demonstrates its usage:

    import org.zalando.jsonapi
    import jsonapi.Jsonapi
    
    case class Person(name: String)
    
    implicit val personJsonapiWriter = new JsonapiRootObjectWriter[Person] {
      override def toJsonapi(person: Person) = {
        ???
      }
    }
    
    val personRootObject: RootObject = Jsonapi.asJsonapi(Person("Boris M."))

In contrast there is a type class called `JsonapiRootEntityReader` that supports conversion from JSON API representation to your resources. To illustrate:

    import org.zalando.jsonapi
    import jsonapi.Jsonapi
    
    case class Person(name: String)
    
    implicit val personJsonapiReader = new JsonapiRootObjectReader[Person] {
      override def fromJsonapi(rootObject: RootObject) = {
        ???
      }
    }
    
    val person: Person = Jsonapi.fromJsonapi[Person](RootObject(...))

For complete usage, see [the specs example].

# Publishing and Releasing

Publishing and releasing is made with help of the [sbt-sonatype plugin].

# License

scala-jsonapi is licensed under the [MIT][MIT license].

[sbt-sonatype plugin]: https://github.com/xerial/sbt-sonatype
[the specs example]: src/test/scala/org/zalando/jsonapi/json/ExampleSpec.scala
[sonatype resolvers]: http://www.scala-sbt.org/0.13/docs/Resolvers.html#Maven
[jsonapi]: http://jsonapi.org/
[Play-JSON]: https://www.playframework.com/documentation/2.5.x/ScalaJson
[Spray-JSON]: https://github.com/spray/spray-json
[Circe]: https://github.com/travisbrown/circe
[MIT license]: https://opensource.org/licenses/MIT
