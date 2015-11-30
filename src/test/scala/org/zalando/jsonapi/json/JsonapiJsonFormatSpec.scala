package org.zalando.jsonapi.json

import org.scalatest.{ MustMatchers, WordSpec }
import org.zalando.jsonapi.model.RootObject._
import org.zalando.jsonapi.model._
import spray.json._

import org.zalando.jsonapi.model.JsonApiObject._

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
    Attribute("foo", StringValue("bar")),
    Attribute("number", NumberValue(42)),
    Attribute("bool", BooleanValue(true)),
    Attribute("anotherObject", JsObjectValue(List(Attribute("withNull", NullValue)))),
    Attribute("array", JsArrayValue(
      List(
        StringValue("a"),
        StringValue("b"),
        StringValue("c")
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
    RootObject(data = Some(ResourceObject(`type` = "person", id = "1")))

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
    RootObject(Some(ResourceObjects(List(
      ResourceObject(`type` = "person", id = "1", attributes = Some(
        List(Attribute("name", StringValue("foobar")))
      ), links = Some(List(Link(linkOption = Link.Self("/persons/1")))))
    ))), links = Some(List(Link(linkOption = Link.Next("/persons/2")))))

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
    RootObject(Some(ResourceIdentifierObject(`type` = "person", id = "42")))

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
    RootObject(Some(ResourceIdentifierObjects(List(
      ResourceIdentifierObject(`type` = "person", id = "42"),
      ResourceIdentifierObject(`type` = "cat", id = "felix")
    ))))

  val rootObjectWithResourceObjectsWithAllLinks = RootObject(Some(ResourceObjects(List(
    ResourceObject(`type` = "person", id = "1", attributes = None, links = Some(
      List(
        Link(linkOption = Link.Self("/persons/2")),
        Link(linkOption = Link.Related("/persons/10")),
        Link(linkOption = Link.Next("/persons/3")),
        Link(linkOption = Link.Prev("/persons/1")),
        Link(linkOption = Link.About("/persons/11")),
        Link(linkOption = Link.First("/persons/0")),
        Link(linkOption = Link.Last("/persons/99"))
      )))))))

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

  val rootObjectWithResourceObjectsWithMeta = RootObject(Some(ResourceObjects(List(ResourceObject(`type` = "person", id = "1", meta = Some(List(MetaProperty("foo", StringValue("bar")))))))))

  val rootObjectWithResourceObjectsWithMetaJson =
    """
      |{
      |  "data": [{
      |    "type": "person",
      |    "id": "1",
      |    "meta": {
      |      "foo": "bar"
      |    }
      |  }]
      |}
    """.stripMargin.parseJson

  val rootObjectWithMeta = RootObject(Some(ResourceObjects(List(ResourceObject(`type` = "person", id = "1")))), meta = Some(List(MetaProperty("foo", StringValue("bar")), MetaProperty("array", JsArrayValue(List(StringValue("one"), StringValue("two")))))))

  val rootObjectWithMetaJson =
    """
      |{
      |  "data": [{
      |    "type": "person",
      |    "id": "1"
      |  }],
      |  "meta": {
      |    "foo": "bar",
      |    "array": [
      |      "one", "two"
      |    ]
      |  }
      |}
    """.stripMargin.parseJson

  val rootObjectWithErrors = RootObject(Some(ResourceObjects(List(ResourceObject(`type` = "person", id = "1")))), errors = Some(List(Error(id = Some("1"),
    links = Some(List(Link(Link.Self("self-link")))), status = Some("status1"), code = Some("code1"), title = Some("title1"), detail = Some("something really bad happened")))))

  val rootObjectWithErrorsJson =
    """
      |{
      |  "data": [{
      |    "type": "person",
      |    "id": "1"
      |  }],
      |  "errors": [
      |    {
      |      "id": "1",
      |      "links": {
      |        "self": "self-link"
      |      },
      |      "status": "status1",
      |      "code": "code1",
      |      "title": "title1",
      |      "detail": "something really bad happened"
      |    }
      |  ]
      |}
    """.stripMargin.parseJson

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
    "serialize all meta object inside resource object correctly" in {
      rootObjectWithResourceObjectsWithMeta.toJson mustEqual rootObjectWithResourceObjectsWithMetaJson
    }
    "serialize a meta object in root object correctly" in {
      rootObjectWithMeta.toJson mustEqual rootObjectWithMetaJson
    }
    "serialize a Jsonapi error object correctly" in {
      rootObjectWithErrors.toJson mustEqual rootObjectWithErrorsJson
    }
  }
}
