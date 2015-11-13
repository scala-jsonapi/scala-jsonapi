package org.zalando.jsonapi.json

import org.scalatest.{ MustMatchers, WordSpec }
import org.zalando.jsonapi.JsonapiRootObjectWriter
import org.zalando.jsonapi.model.Attribute.StringValue
import org.zalando.jsonapi.model.RootObject.ResourceObject
import org.zalando.jsonapi.model.{ Attribute, RootObject }
import spray.json._
import org.zalando.jsonapi._

class ExampleSpec extends WordSpec with MustMatchers with JsonapiJsonProtocol {
  "JsonapiRootObject" when {
    "using root object serializer" must {
      "serialize accordingly" in {
        case class Person(id: Int, name: String)

        implicit val personJsonapiRootObjectWriter: JsonapiRootObjectWriter[Person] = new JsonapiRootObjectWriter[Person] {
          override def toJsonapi(person: Person) = {
            RootObject(data = ResourceObject(
              `type` = "person",
              id = person.id.toString,
              attributes = Some(List(
                Attribute("name", StringValue(person.name))
              ))
            ))
          }
        }

        val json =
          """
          {
            "data": {
              "id": "42",
              "type": "person",
              "attributes": {
                "name": "foobar"
              }
            }
          }
          """.stripMargin.parseJson

        Person(42, "foobar").rootObject.toJson mustEqual json
      }
    }
  }
}
