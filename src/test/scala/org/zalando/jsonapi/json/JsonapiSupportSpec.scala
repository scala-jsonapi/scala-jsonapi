package org.zalando.jsonapi.json

import org.scalactic.TypeCheckedTripleEquals
import org.scalatest.{ EitherValues, WordSpec }
import org.zalando.jsonapi.JsonapiRootObjectWriter
import org.zalando.jsonapi.model.{ JsonApiObject, JsonApiProperty, RootObject }
import spray.http.HttpEntity
import spray.httpx.marshalling._
import spray.httpx.unmarshalling._

trait JsonapiSupportSpec extends WordSpec with TypeCheckedTripleEquals with EitherValues {
  def jsonapiSupportClassName: String
  implicit def jsonapiRootObjectMarshaller: Marshaller[RootObject]
  implicit def jsonapiRootObjectUnmarshaller: Unmarshaller[RootObject]

  s"$jsonapiSupportClassName" must {
    trait Context {
      val rootObject = RootObject(jsonApi = Some(List(JsonApiProperty("foo", JsonApiObject.StringValue("bar")))))
      case class Foo(bar: String)
    }

    "allow marshalling a Jsonapi root object with the correct content type" in new Context {
      val httpEntityString = """HttpEntity(application/vnd.api+json; charset=UTF-8,{"jsonapi":{"foo":"bar"}})"""
      assert(marshal(rootObject).right.value.toString === httpEntityString)
    }

    "allow marshalling a value of a type that can be converted to a Jsonapi root object" in new Context {
      implicit val fooWriter = new JsonapiRootObjectWriter[Foo] {
        override def toJsonapi(a: Foo) = rootObject
      }
      assert(marshal(Foo("bar")).right.value.toString ===
        """HttpEntity(application/vnd.api+json; charset=UTF-8,{"jsonapi":{"foo":"bar"}})""")
    }

    // TODO Fix Play framework unmarshalling problem (issue #25 on github)
    //    "allow unmarshalling a Json to a root object" in {
    //      val rootObject = RootObject(jsonApi = Some(List(JsonApiProperty("foo", JsonApiObject.StringValue("bar")))))
    //      val httpEntity = HttpEntity(`application/vnd.api+json`, """{"jsonapi":{"foo":"bar"}}""")
    //      assert(httpEntity.as[RootObject] === Right(rootObject))
    //    }
  }
}
