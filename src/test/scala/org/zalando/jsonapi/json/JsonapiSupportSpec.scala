package org.zalando.jsonapi.json

import org.scalactic.TypeCheckedTripleEquals
import org.scalatest.{ EitherValues, WordSpec }
import org.zalando.jsonapi.JsonapiRootObjectWriter
import org.zalando.jsonapi.model.{ JsonApiObject, JsonApiProperty, RootObject }
import spray.httpx.marshalling._

trait JsonapiSupportSpec extends WordSpec with TypeCheckedTripleEquals with EitherValues {
  def jsonapiSupportClassName: String
  implicit def jsonapiRootObjectMarshaller: Marshaller[RootObject]

  "SprayJsonJsonapiSupport" must {
    "allow marshalling a Jsonapi root object with the correct content type" in {
      val rootObject = RootObject(jsonApi = Some(List(JsonApiProperty("foo", JsonApiObject.StringValue("bar")))))
      val httpEntityString = """HttpEntity(application/vnd.api+json; charset=UTF-8,{"jsonapi":{"foo":"bar"}})"""
      assert(marshal(rootObject).right.value.toString === httpEntityString)
    }

    "allow marshalling a value of a type that can be converted to a Jsonapi root object" in {
      case class Foo(bar: String)
      implicit val fooWriter = new JsonapiRootObjectWriter[Foo] {
        override def toJsonapi(a: Foo) = RootObject(jsonApi = Some(List(JsonApiProperty("foo", JsonApiObject.StringValue("bar")))))
      }
      assert(marshal(Foo("bar")).right.value.toString ===
        """HttpEntity(application/vnd.api+json; charset=UTF-8,{"jsonapi":{"foo":"bar"}})""")
    }
  }
}
