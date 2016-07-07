package org.zalando.jsonapi.json.akka.http

import org.scalactic.TypeCheckedTripleEquals
import org.scalatest.{ EitherValues, WordSpec }
import org.zalando.jsonapi.JsonapiRootObjectWriter
import org.zalando.jsonapi.model._
import org.zalando.jsonapi._
import spray.json._

class AkkaHttpJsonapiSupportSpec extends WordSpec with TypeCheckedTripleEquals with EitherValues with AkkaHttpJsonapiSupport {
  s"AkkaHttpJsonapiSupport" must {
    trait Context {
      case class Foo(bar: String)
      val rootObject = RootObject(jsonApi = Some(List(JsonApiProperty("foo", JsonApiObject.StringValue("bar")))))
      val foo = Foo("bar")
      val fooJson = """{"jsonapi":{"foo":"bar"}}"""
    }

    "allow marshalling a Jsonapi root object with the correct content type" in new Context {
      val httpEntityString = """HttpEntity(application/vnd.api+json; charset=UTF-8,{"jsonapi":{"foo":"bar"}})"""
      implicit val fooWriter = new JsonapiRootObjectWriter[Foo] {
        override def toJsonapi(a: Foo) = rootObject
      }

      assert(foo.rootObject.toJson.compactPrint === fooJson)
    }

    "allow marshalling a value of a type that can be converted to a Jsonapi root object" in new Context {
      implicit val fooReader = new JsonapiRootObjectReader[Foo] {
        override def fromJsonapi(rootObject: RootObject) = foo
      }
      println(fooJson.parseJson.convertTo[RootObject].jsonapi[Foo] === foo)
    }
  }
}
