package org.zalando.jsonapi.json

import org.scalactic.TypeCheckedTripleEquals
import org.scalatest.{ EitherValues, WordSpec }
import org.zalando.jsonapi.{JsonapiRootObjectReader, JsonapiRootObjectWriter}
import org.zalando.jsonapi.json.sprayhttpx.SprayJsonapiSupport
import org.zalando.jsonapi.model.{ JsonApiObject, JsonApiProperty, RootObject }
import spray.http.HttpEntity
import spray.httpx.marshalling._
import spray.httpx.unmarshalling._
import spray.http.MediaTypes.`application/vnd.api+json`

trait JsonapiSupportSpec extends WordSpec with TypeCheckedTripleEquals with EitherValues with SprayJsonapiSupport {
  def jsonapiSupportClassName: String
  implicit def jsonapiRootObjectMarshaller: Marshaller[RootObject]
  implicit def jsonapiRootObjectUnmarshaller: Unmarshaller[RootObject]

  s"$jsonapiSupportClassName" must {
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

    "unmarshalling a malformed Json with the correct content type throw a malformedContent" in new Context {
      val httpEntity = HttpEntity(`application/vnd.api+json`, """{{{{"jsonapi":{"foo":"bar"}}""")
      assert(httpEntity.as[RootObject].left.value.isInstanceOf[MalformedContent])
    }
  }
}
