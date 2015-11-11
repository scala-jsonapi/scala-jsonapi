package org.zalando.jsonapi.json

import org.scalatest.{ MustMatchers, WordSpec }
import org.zalando.jsonapi.model._
import spray.json._

class JsonapiJsonFormatSpec extends WordSpec with MustMatchers with JsonapiJsonFormat with DefaultJsonProtocol {
  val rootObjectJsonWithAttributes =
    """
      {
        "data": {
          "type": "person",
          "id": "1",
          "attributes" : {
            "foo": "bar",
            "number": 42,
            "bool": true,
            "anotherObject": { "withNull": null },
            "array": [ "a", "b", "c" ]
          }
        }
      }
    """.stripMargin.parseJson

  val rootObjectWithAttributes = RootObject(
    data = Data(
      `type` = "person",
      id = "1",
      attributes = Some(List(
        Attribute("foo", Attribute.StringValue("bar")),
        Attribute("number", Attribute.NumberValue(42)),
        Attribute("bool", Attribute.BooleanValue(true)),
        Attribute("anotherObject", Attribute.JsObjectValue(List(Attribute("withNull", Attribute.NullValue)))),
        Attribute("array", Attribute.JsArrayValue(
          Seq(
            Attribute.StringValue("a"),
            Attribute.StringValue("b"),
            Attribute.StringValue("c")
          )
        ))
      ))
    )
  )

  val rootObjectWithoutAttributesJson =
    """
      {
        "data": {
          "type": "person",
          "id": "abc"
        }
      }
    """.stripMargin.parseJson

  val rootObjectWithoutAttributes = RootObject(
    data = Data(
      `type` = "person",
      id = "abc",
      attributes = None
    )
  )

  "JsonapiJsonFormat" must {
    "serialize Jsonapi rootObject with attributes" in {
      rootObjectWithAttributes.toJson mustEqual rootObjectJsonWithAttributes
    }
    "serialize Jsonapi rootObject without attributes" in {
      rootObjectWithoutAttributes.toJson mustEqual rootObjectWithoutAttributesJson
    }
  }
}
