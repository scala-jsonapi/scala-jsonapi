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
      ), links = Some(List(Links.Self("/persons/1"))))
    ))), links = Some(List(Links.Next("/persons/2"))))

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
    RootObject(Some(ResourceObject(`type` = "person", id = "42")))

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
    RootObject(Some(ResourceObjects(List(
      ResourceObject(`type` = "person", id = "42"),
      ResourceObject(`type` = "cat", id = "felix")
    ))))

  val rootObjectWithResourceObjectsWithAllLinks = RootObject(Some(ResourceObjects(List(
    ResourceObject(`type` = "person", id = "1", attributes = None, links = Some(
      List(
        Links.Self("/persons/2"),
        Links.Related("/persons/10"),
        Links.Next("/persons/3"),
        Links.Prev("/persons/1"),
        Links.About("/persons/11"),
        Links.First("/persons/0"),
        Links.Last("/persons/99")
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

  val rootObjectWithErrorsObject =
    RootObject(
      data = None,
      errors = Some(List(Error(
        id = Some("1"),
        links = Some(List(Links.Self("self-link"))),
        status = Some("status1"),
        code = Some("code1"),
        title = Some("title1"),
        detail = Some("something really bad happened"),
        source = Some(ErrorSource(Some("id"), Some("/data/attributes/id"))),
        meta = Some(List(MetaProperty("metaInfo", StringValue("MetaValue"))))
      )))
    )

  val rootObjectWithErrorsJson =
    """
      |{
      |  "errors": [
      |    {
      |      "id": "1",
      |      "links": {
      |        "self": "self-link"
      |      },
      |      "status": "status1",
      |      "code": "code1",
      |      "title": "title1",
      |      "detail": "something really bad happened",
      |      "source": {
      |        "pointer": "id",
      |        "parameter": "/data/attributes/id"
      |      },
      |      "meta": {
      |        "metaInfo": "MetaValue"
      |      }
      |    }
      |  ]
      |}
    """.stripMargin.parseJson

  val rootObjectWithErrorsNoneObject =
    RootObject(
      data = None,
      errors = Some(List(Error(
        id = Some("1"),
        status = Some("status1"),
        code = Some("code1"),
        title = Some("title1"),
        detail = Some("something really bad happened")
      )))
    )

  val rootObjectWithErrorsNoneJson =
    """
      |{
      |  "errors": [
      |    {
      |      "id": "1",
      |      "status": "status1",
      |      "code": "code1",
      |      "title": "title1",
      |      "detail": "something really bad happened"
      |    }
      |  ]
      |}
    """.stripMargin.parseJson

  val rootObjectWithIncluded = RootObject(Some(ResourceObjects(List(
    ResourceObject(`type` = "person", id = "1",
      attributes = Some(List(Attribute("name", StringValue("foo")))))))),
    included = Some(Included(ResourceObjects(List(ResourceObject(`type` = "person", id = "2"))))))

  val rootObjectWithIncludedJson =
    """
      |{
      |  "data": [{
      |    "type": "person",
      |    "id": "1",
      |    "attributes": {
      |      "name": "foo"
      |    }
      |  }],
      |  "included": [{
      |    "type": "person",
      |    "id": "2"
      |  }]
      |}
    """.stripMargin.parseJson

  val rootObjectWithJsonApiObject = RootObject(
    jsonApi = Some(List(JsonApiProperty("one", StringValue("two")), JsonApiProperty("foo", JsArrayValue(Seq(StringValue("bar1"), StringValue("bar2"), StringValue("bar3")))))))

  val rootObjectWithJsonApiObjectJson =
    """
      |{
      |  "jsonapi": {
      |    "one": "two",
      |    "foo": ["bar1", "bar2", "bar3"]
      |  }
      |}
    """.stripMargin.parseJson

  val resourceObjectWithRelationships =
    RootObject(Some(ResourceObjects(List(
      ResourceObject(
        `type` = "person",
        id = "1",
        attributes = Some(List(Attribute("name", StringValue("foobar")))),
        relationships =
          Some(Map("father" -> Relationship(
            data = Some(ResourceObject(`type` = "person", id = "2")),
            links = Some(List(Links.Self("http://link.to.self")))
          )))
      )
    ))))

  val resourceObjectWithRelationshipsJson =
    """
      |{
      |  "data": [{
      |    "type": "person",
      |    "id": "1",
      |    "attributes": {
      |      "name": "foobar"
      |    },
      |    "relationships": {
      |      "father": {
      |        "data": {
      |          "id": "2",
      |          "type": "person"
      |        },
      |        "links": {
      |          "self": "http://link.to.self"
      |        }
      |      }
      |    }
      |  }]
      |}
    """.stripMargin.parseJson

  val resourceObjectWithEmptyRelationshipsObject =
    RootObject(Some(ResourceObjects(List(
      ResourceObject(
        `type` = "person",
        id = "1",
        attributes = Some(List(Attribute("name", StringValue("foobar")))),
        relationships = Some(Map("father" -> Relationship()))
      )
    ))))

  val resourceObjectWithEmptyRelationshipsJson =
    """
      |{
      |  "data": [{
      |    "type": "person",
      |    "id": "1",
      |    "attributes": {
      |      "name": "foobar"
      |    },
      |    "relationships": {
      |      "father": {}
      |    }
      |  }]
      |}
    """.stripMargin.parseJson

  "JsonapiJsonFormat" when {
    "serializing Jsonapi" must {
      "transform attributes correctly" in {
        attributes.toJson mustEqual attributesJson
      }
      "transform resource object correctly" in {
        rootObjectWithResourceObjectWithoutAttributes.toJson mustEqual rootObjectWithResourceObjectWithoutAttributesJson
      }
      "transform a list of resource objects correctly" in {
        rootObjectWithResourceObjects.toJson mustEqual rootObjectWithResourceObjectsJson
      }
      "transform resource identifier object correctly" in {
        rootObjectWithResourceIdentifierObject.toJson mustEqual rootObjectWithResourceIdentifierObjectJson
      }
      "transform a list of resource identifier objects correctly" in {
        rootObjectWithResourceIdentifierObjects.toJson mustEqual rootObjectWithResourceIdentifierObjectsJson
      }
      "transform all link types correctly" in {
        rootObjectWithResourceObjectsWithAllLinks.toJson mustEqual rootObjectWithResourceObjectsWithAllLinksJson
      }
      "transform all meta object inside resource object correctly" in {
        rootObjectWithResourceObjectsWithMeta.toJson mustEqual rootObjectWithResourceObjectsWithMetaJson
      }
      "transform a meta object in root object correctly" in {
        rootObjectWithMeta.toJson mustEqual rootObjectWithMetaJson
      }
      "transform error object correctly" in {
        rootObjectWithErrorsObject.toJson mustEqual rootObjectWithErrorsJson
      }
      "transform error object without links and meta and source correctly" in {
        rootObjectWithErrorsNoneObject.toJson mustEqual rootObjectWithErrorsNoneJson
      }
      "transform included object correctly" in {
        rootObjectWithIncluded.toJson mustEqual rootObjectWithIncludedJson
      }
      "transform jsonapi object correctly" in {
        rootObjectWithJsonApiObject.toJson mustEqual rootObjectWithJsonApiObjectJson
      }
      "transform relationship object correctly" in {
        resourceObjectWithRelationships.toJson mustEqual resourceObjectWithRelationshipsJson
      }
      "transform empty relationship object correctly" in {
        resourceObjectWithEmptyRelationshipsObject.toJson mustEqual resourceObjectWithEmptyRelationshipsJson
      }

    }
    "deserializing Jsonapi" must {
      "transform attributes correctly" in {
        attributesJson.convertTo[Attributes] === attributes
      }
      "transform resource object correctly" in {
        rootObjectWithResourceObjectWithoutAttributesJson.convertTo[RootObject] === rootObjectWithResourceObjectWithoutAttributes
      }
      "transform a list of resource objects correctly" in {
        rootObjectWithResourceObjectsJson.convertTo[RootObject] === rootObjectWithResourceObjects
      }
      "transform resource identifier object correctly" in {
        rootObjectWithResourceIdentifierObjectJson.convertTo[RootObject] === rootObjectWithResourceIdentifierObject
      }
      "transform a list of resource identifier objects correctly" in {
        rootObjectWithResourceIdentifierObjectsJson.convertTo[RootObject] === rootObjectWithResourceIdentifierObjects
      }
      "transform all link types correctly" in {
        rootObjectWithResourceObjectsWithAllLinksJson.convertTo[RootObject] === rootObjectWithResourceObjectsWithAllLinks
      }
      "transform all meta object inside resource object correctly" in {
        rootObjectWithResourceObjectsWithMetaJson.convertTo[RootObject] === rootObjectWithResourceObjectsWithMeta
      }
      "transform a meta object in root object correctly" in {
        rootObjectWithMetaJson.convertTo[RootObject] === rootObjectWithMeta
      }
      "transform error object correctly" in {
        rootObjectWithErrorsJson.convertTo[RootObject] === rootObjectWithErrorsObject
      }
      "transform error object without links and meta and source correctly" in {
        rootObjectWithErrorsNoneJson.convertTo[RootObject] === rootObjectWithErrorsNoneObject
      }
      "transform included object correctly" in {
        rootObjectWithIncludedJson.convertTo[RootObject] === rootObjectWithIncluded
      }
      "transform jsonapi object correctly" in {
        rootObjectWithJsonApiObjectJson.convertTo[RootObject] === rootObjectWithJsonApiObject
      }
      "transform relationship object correctly" in {
        resourceObjectWithRelationshipsJson.convertTo[RootObject] === resourceObjectWithRelationships
      }
      "transform empty relationship object correctly" in {
        resourceObjectWithEmptyRelationshipsJson.convertTo[RootObject] === resourceObjectWithEmptyRelationshipsObject
      }
      "fail if data is not an array nor an object" in {
        val json = s"""{"data": "foo"}""".parseJson
        intercept[DeserializationException] {
          json.convertTo[RootObject]
        }
      }
    }
  }
}
