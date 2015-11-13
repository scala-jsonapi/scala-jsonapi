package org.zalando.jsonapi.json

import org.scalatest.{ MustMatchers, WordSpec }
import org.zalando.jsonapi.model.RootObject._
import org.zalando.jsonapi.model._
import spray.json._

class JsonapiJsonFormatSpec extends WordSpec with MustMatchers with JsonapiJsonProtocol {
  val attributesJson =
    """
      |{
      |  "foo": "bar",
      |  "number": 42,
      |  "bool": true,
      |  "anotherObject": { "withNull": null },
      |  "array": [ "a", "b", "c" ]
      |}
    """.stripMargin.parseJson

  val attributes: Attributes = List(
    Attribute("foo", Attribute.StringValue("bar")),
    Attribute("number", Attribute.NumberValue(42)),
    Attribute("bool", Attribute.BooleanValue(true)),
    Attribute("anotherObject", Attribute.JsObjectValue(List(Attribute("withNull", Attribute.NullValue)))),
    Attribute("array", Attribute.JsArrayValue(
      List(
        Attribute.StringValue("a"),
        Attribute.StringValue("b"),
        Attribute.StringValue("c")
      )
    ))
  )

  val rootObjectWithResourceObjectWithoutAttributesJson =
    """
      |{
      |  "data": {
      |    "type": "person",
      |    "id": "1"
      |  }
      |}
    """.stripMargin.parseJson

  val rootObjectWithResourceObjectWithoutAttributes =
    RootObject(ResourceObject(`type` = "person", id = "1", attributes = None))

  val rootObjectWithResourceObjectsJson =
    """
      |{
      |  "data": [{
      |    "type": "person",
      |    "id": "1",
      |    "attributes": {
      |      "name": "foobar"
      |    }
      |  }]
      |}
    """.stripMargin.parseJson

  val rootObjectWithResourceObjects =
    RootObject(ResourceObjects(List(
      ResourceObject(`type` = "person", id = "1", attributes = Some(
        List(Attribute("name", Attribute.StringValue("foobar")))
      ))
    )))

  val rootObjectWithResourceIdentifierObjectJson =
    """
      |{
      |  "data": {
      |    "type": "person",
      |    "id": "42"
      |  }
      |}
    """.stripMargin.parseJson

  val rootObjectWithResourceIdentifierObject =
    RootObject(ResourceIdentifierObject(`type` = "person", id = "42"))

  val rootObjectWithResourceIdentifierObjectsJson =
    """
      |{
      |  "data": [
      |    {
      |      "type": "person",
      |      "id": "42"
      |    },
      |    {
      |      "type": "cat",
      |      "id": "felix"
      |    }
      |  ]
      |}
    """.stripMargin.parseJson

  val rootObjectWithResourceIdentifierObjects =
    RootObject(ResourceIdentifierObjects(List(
      ResourceIdentifierObject(`type` = "person", id = "42"),
      ResourceIdentifierObject(`type` = "cat", id = "felix")
    )))

  "JsonapiJsonFormat" must {
    "serialize Jsonapi attributes" in {
      attributes.toJson mustEqual attributesJson
    }
    "serialize a Jsonapi resource object" in {
      rootObjectWithResourceObjectWithoutAttributes.toJson mustEqual rootObjectWithResourceObjectWithoutAttributesJson
    }
    "serialize a list of Jsonapi resource objects" in {
      rootObjectWithResourceObjects.toJson mustEqual rootObjectWithResourceObjectsJson
    }
    "serialize a Jsonapi resource identifier object" in {
      rootObjectWithResourceIdentifierObject.toJson mustEqual rootObjectWithResourceIdentifierObjectJson
    }
    "serialize a list of Jsonapi resource identifier objects" in {
      rootObjectWithResourceIdentifierObjects.toJson mustEqual rootObjectWithResourceIdentifierObjectsJson
    }
  }
}
