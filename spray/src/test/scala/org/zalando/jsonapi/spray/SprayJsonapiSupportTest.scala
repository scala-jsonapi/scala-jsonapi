package org.zalando.jsonapi.spray

import org.scalactic.TypeCheckedTripleEquals
import org.scalatest.{EitherValues, MustMatchers, WordSpec}
import org.zalando.jsonapi.{JsonapiRootObjectReader, JsonapiRootObjectWriter}
import org.zalando.jsonapi.model.{JsonApiObject, JsonApiProperty, RootObject}
import SprayJsonapiSupport._
import spray.http.HttpEntity
import spray.http.MediaTypes._
import spray.httpx.marshalling._
import spray.httpx.unmarshalling._

class SprayJsonapiSupportTest extends WordSpec with MustMatchers with TypeCheckedTripleEquals with EitherValues {

  "CirceJsonapiSupport" must {
    trait Context {
      case class Foo(bar: String)
      val rootObject = RootObject(jsonApi = Some(List(JsonApiProperty("foo", JsonApiObject.StringValue("bar")))))
      val foo = Foo("bar")
    }

    "allow marshalling a Jsonapi root object with the correct content type" in new Context {
      val httpEntityString = """HttpEntity(application/vnd.api+json; charset=UTF-8,{"jsonapi":{"foo":"bar"}})"""
      assert(marshal(rootObject).right.value.toString === httpEntityString)
    }

    "allow marshalling a value of a type that can be converted to a Jsonapi root object" in new Context {
      implicit val fooWriter = new JsonapiRootObjectWriter[Foo] {
        override def toJsonapi(a: Foo) = rootObject
      }
      assert(marshal(foo).right.value.toString ===
        """HttpEntity(application/vnd.api+json; charset=UTF-8,{"jsonapi":{"foo":"bar"}})""")
    }

    "allow unmarshalling a Json to a root object with the correct content type" in new Context {
      val httpEntity = HttpEntity(`application/vnd.api+json`, """{"jsonapi":{"foo":"bar"}}""")
      assert(httpEntity.as[RootObject] === Right(rootObject))
    }

    "allow unmarshalling Jsonapi root object to a value type" in new Context {
      implicit val fooReader = new JsonapiRootObjectReader[Foo] {
        override def fromJsonapi(ro: RootObject) = foo
      }
      val httpEntity = HttpEntity(`application/vnd.api+json`, """{"jsonapi":{"foo":"bar"}}""")
      assert(httpEntity.as[Foo] === Right(foo))
    }
  }
}
