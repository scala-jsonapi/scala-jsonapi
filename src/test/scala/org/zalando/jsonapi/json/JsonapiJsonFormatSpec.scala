package org.zalando.jsonapi.json

import java.nio.file.LinkOption

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
    RootObject(ResourceObject(`type` = "person", id = "1", attributes = None, links = None), links = None)

  val rootObjectWithResourceObjectsJson =
    """
      |{
      |  "data": [{
      |    "type": "person",
      |    "id": "1",
      |    "attributes": {
      |      "name": "foobar"
      |    },
      |    "links": {
      |      "self": "/persons/1"
      |    }
      |  }],
      |  "links": {
      |    "next": "/persons/2"
      |  }
      |}
    """.stripMargin.parseJson

  val rootObjectWithResourceObjects =
    RootObject(ResourceObjects(List(
      ResourceObject(`type` = "person", id = "1", attributes = Some(
        List(Attribute("name", Attribute.StringValue("foobar")))
      ), links = Some(List(Link(linkOption = Link.Self("/persons/1")))))
    )), links = Some(List(Link(linkOption = Link.Next("/persons/2")))))

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
    RootObject(ResourceIdentifierObject(`type` = "person", id = "42"), links = None)

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

  val rootObjectWithResourceObjectsWithAllLinks = RootObject(ResourceObjects(List(
    ResourceObject(`type` = "person", id = "1", attributes = None, links = Some(
      List(
        Link(linkOption = Link.Self("/persons/2")),
        Link(linkOption = Link.Related("/persons/10")),
        Link(linkOption = Link.Next("/persons/3")),
        Link(linkOption = Link.Prev("/persons/1")),
        Link(linkOption = Link.About("/persons/11")),
        Link(linkOption = Link.First("/persons/0")),
        Link(linkOption = Link.Last("/persons/99"))
      ))))), links = None)

  val rootObjectWithResourceObjectsWithAllLinksJson =
    """
      |{
      |  "data": [{
      |    "type": "person",
      |    "id": "1",
      |    "links": {
      |      "self": "/persons/2",
      |      "related": "/persons/10",
      |      "next": "/persons/3",
      |      "prev": "/persons/1",
      |      "about": "/persons/11",
      |      "first": "/persons/0",
      |      "last": "/persons/99"
      |    }
      |  }]
      |}
    """.stripMargin.parseJson

  val rootObjectWithResourceIdentifierObjects =
    RootObject(ResourceIdentifierObjects(List(
      ResourceIdentifierObject(`type` = "person", id = "42"),
      ResourceIdentifierObject(`type` = "cat", id = "felix")
    )), links = None)

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
    "serialize all link types correctly" in {
      rootObjectWithResourceObjectsWithAllLinks.toJson mustEqual rootObjectWithResourceObjectsWithAllLinksJson
    }
  }
}
