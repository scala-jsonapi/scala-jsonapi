package org.zalando.jsonapi.json

import org.scalatest.{ MustMatchers, WordSpec }
import org.zalando.jsonapi.model.{ DataProperty, RootObject }
import spray.json._

class JsonapiJsonFormatSpec extends WordSpec with MustMatchers with JsonapiJsonFormat with DefaultJsonProtocol {
  val rootObjectJsonWithData =
    """
      {
        "type": "person",
        "id": "1",
        "data": {
          "foo": "bar",
          "number": 42,
          "bool": true,
          "anotherObject": { "withNull": null },
          "array": [ "a", "b", "c" ]
        }
      }
    """.stripMargin.parseJson

  val rootObjectWithData = RootObject(
    `type` = "person",
    id = "1",
    data = Some(List(
      DataProperty("foo", DataProperty.StringValue("bar")),
      DataProperty("number", DataProperty.NumberValue(42)),
      DataProperty("bool", DataProperty.BooleanValue(true)),
      DataProperty("anotherObject", DataProperty.JsObjectValue(Seq(("withNull", DataProperty.NullValue)))),
      DataProperty("array", DataProperty.JsArrayValue(
        Seq(
          DataProperty.StringValue("a"),
          DataProperty.StringValue("b"),
          DataProperty.StringValue("c")
        )
      ))
    ))
  )

  val rootObjectWithoutDataJson =
    """
      {
        "type": "person",
        "id": "abc"
      }
    """.stripMargin.parseJson

  val rootObjectWithoutData = RootObject(
    `type` = "person",
    id = "abc",
    data = None
  )

  "JsonapiJsonFormat" must {
    "serialize Jsonapi rootObject with data" in {
      rootObjectWithData.toJson mustEqual rootObjectJsonWithData
    }
    "serialize Jsonapi rootObject without data" in {
      rootObjectWithoutData.toJson mustEqual rootObjectWithoutDataJson
    }
  }
}
