package org.zalando.jsonapi.json

import org.scalatest.WordSpec
import org.zalando.jsonapi.model.JsonApiObject._
import org.zalando.jsonapi.model.RootObject.{ ResourceObjects, ResourceObject }
import org.zalando.jsonapi.model._

trait JsonBaseSpec[JsonBaseType] extends WordSpec {
  protected def parseJson(jsonString: String): JsonBaseType

  protected lazy val attributesJson = parseJson(attributesJsonString)

  protected lazy val rootObjectWithResourceObjectWithoutAttributesJson = parseJson(rootObjectWithResourceObjectWithoutAttributesJsonString)

  protected lazy val rootObjectWithResourceObjectsJson = parseJson(rootObjectWithResourceObjectsJsonString)

  protected lazy val rootObjectWithResourceIdentifierObjectJson = parseJson(rootObjectWithResourceIdentifierObjectJsonString)

  protected lazy val rootObjectWithResourceIdentifierObjectsJson = parseJson(rootObjectWithResourceIdentifierObjectsJsonString)

  protected lazy val rootObjectWithResourceObjectsWithAllLinksJson = parseJson(rootObjectWithResourceObjectsWithAllLinksJsonString)

  protected lazy val rootObjectWithResourceObjectsWithMetaJson = parseJson(rootObjectWithResourceObjectsWithMetaJsonString)

  protected lazy val rootObjectWithMetaJson = parseJson(rootObjectWithMetaJsonString)

  protected lazy val rootObjectWithErrorsJson = parseJson(rootObjectWithErrorsJsonString)

  protected lazy val rootObjectWithErrorsNoneJson = parseJson(rootObjectWithErrorsNoneJsonString)

  protected lazy val rootObjectWithIncludedJson = parseJson(rootObjectWithIncludedJsonString)

  protected lazy val rootObjectWithJsonApiObjectJson = parseJson(rootObjectWithJsonApiObjectJsonString)

  protected lazy val rootObjectWithRelationshipsJson = parseJson(rootObjectWithRelationshipsJsonString)

  protected lazy val resourceObjectWithRelationshipsJson = parseJson(resourceObjectWithRelationshipsJsonString)

  protected lazy val relationshipsJson = parseJson(relationshipsJsonString)

  protected lazy val resourceObjectWithEmptyRelationshipsJson = parseJson(resourceObjectWithEmptyRelationshipsJsonString)

  protected lazy val attributesJsonString =
    """
      |{
      |  "foo": "bar",
      |  "number": 42,
      |  "bool": true,
      |  "anotherObject": { "withNull": null },
      |  "array": [ "a", "b", "c" ]
      |}
    """.stripMargin

  protected lazy val attributes: Attributes = List(
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

  protected lazy val rootObjectWithResourceObjectWithoutAttributesJsonString =
    """
      |{
      |  "data": {
      |    "type": "person"
      |  }
      |}
    """.stripMargin

  protected lazy val rootObjectWithResourceObjectWithoutAttributes =
    RootObject(data = Some(ResourceObject(`type` = "person")))

  protected lazy val rootObjectWithResourceObjectsJsonString =
    """
      |{
      |  "data": [{
      |    "type": "person",
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
    """.stripMargin

  protected lazy val rootObjectWithResourceObjects =
    RootObject(
      data = Some(ResourceObjects(List(
        ResourceObject(
          `type` = "person",
          attributes = Some(List(Attribute("name", StringValue("foobar")))),
          links = Some(List(Link("self", "/persons/1")))
        )))),
      links = Some(List(Link("next", "/persons/2")))
    )

  protected lazy val rootObjectWithResourceIdentifierObjectJsonString =
    """
      |{
      |  "data": {
      |    "type": "person"
      |  }
      |}
    """.stripMargin

  protected lazy val rootObjectWithResourceIdentifierObject =
    RootObject(Some(ResourceObject(`type` = "person")))

  protected lazy val rootObjectWithResourceIdentifierObjectsJsonString =
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
    """.stripMargin

  protected lazy val rootObjectWithResourceIdentifierObjects =
    RootObject(Some(ResourceObjects(List(
      ResourceObject(`type` = "person", id = Some("42")),
      ResourceObject(`type` = "cat", id = Some("felix"))
    ))))

  protected lazy val rootObjectWithResourceObjectsWithAllLinksJsonString =
    """
      |{
      |  "data": [{
      |    "type": "person",
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
    """.stripMargin

  protected lazy val rootObjectWithResourceObjectsWithAllLinks = RootObject(Some(ResourceObjects(List(
    ResourceObject(`type` = "person", links = Some(
      List(
        Link("self", "/persons/2"),
        Link("related", "/persons/10"),
        Link("next", "/persons/3"),
        Link("prev", "/persons/1"),
        Link("about", "/persons/11"),
        Link("first", "/persons/0"),
        Link("last", "/persons/99")
      )))))))

  protected lazy val rootObjectWithResourceObjectsWithMetaJsonString =
    """
    |{
    |  "data": [{
    |    "type": "person",
    |    "meta": {
    |      "foo": "bar"
    |    }
    |  }]
    |}
  """.stripMargin

  protected lazy val rootObjectWithResourceObjectsWithMeta = RootObject(Some(
    ResourceObjects(List(
      ResourceObject(`type` = "person", meta = Some(Map("foo" -> StringValue("bar"))))
    ))
  ))

  protected lazy val rootObjectWithMetaJsonString =
    """
    |{
    |  "data": [{
    |    "type": "person"
    |  }],
    |  "meta": {
    |    "foo": "bar",
    |    "array": [
    |      "one", "two"
    |    ]
    |  }
    |}
  """.stripMargin

  protected lazy val rootObjectWithMeta = RootObject(
    data = Some(
      ResourceObjects(List(
        ResourceObject(
          `type` = "person"
        )))
    ),
    meta = Some(Map(
      "foo" -> StringValue("bar"),
      "array" -> JsArrayValue(List(StringValue("one"), StringValue("two")))
    ))
  )

  protected lazy val rootObjectWithErrorsJsonString =
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
  """.stripMargin

  protected lazy val rootObjectWithErrorsObject =
    RootObject(
      data = None,
      errors = Some(List(Error(
        id = Some("1"),
        links = Some(List(Link("self", "self-link"))),
        status = Some("status1"),
        code = Some("code1"),
        title = Some("title1"),
        detail = Some("something really bad happened"),
        source = Some(ErrorSource(Some("id"), Some("/data/attributes/id"))),
        meta = Some(Map("metaInfo" -> StringValue("MetaValue")))
      )))
    )

  protected lazy val rootObjectWithErrorsNoneJsonString =
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
  """.stripMargin

  protected lazy val rootObjectWithErrorsNoneObject =
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

  protected lazy val rootObjectWithIncludedJsonString =
    """
    |{
    |  "data": [{
    |    "type": "person",
    |    "attributes": {
    |      "name": "foo"
    |    }
    |  }],
    |  "included": [{
    |    "type": "person"
    |  }]
    |}
  """.stripMargin

  protected lazy val rootObjectWithIncluded = RootObject(Some(ResourceObjects(List(
    ResourceObject(`type` = "person",
      attributes = Some(List(Attribute("name", StringValue("foo")))))))),
    included = Some(Included(ResourceObjects(List(ResourceObject(`type` = "person"))))))

  protected lazy val rootObjectWithJsonApiObjectJsonString =
    """
    |{
    |  "jsonapi": {
    |    "one": "two",
    |    "foo": ["bar1", "bar2", "bar3"]
    |  }
    |}
  """.stripMargin

  protected lazy val rootObjectWithJsonApiObject = RootObject(
    jsonApi = Some(List(
      JsonApiProperty("one", StringValue("two")),
      JsonApiProperty("foo", JsArrayValue(Seq(StringValue("bar1"), StringValue("bar2"), StringValue("bar3"))))
    ))
  )

  protected lazy val rootObjectWithRelationshipsJsonString =
    """
    |{
    |  "data": [{
    |    "type": "person",
    |    "attributes": {
    |      "name": "foobar"
    |    },
    |    "relationships": {
    |      "father": {
    |        "data": {
    |          "type": "person"
    |        },
    |        "links": {
    |          "self": "http://link.to.self"
    |        }
    |      }
    |    }
    |  }]
    |}
  """.stripMargin

  protected lazy val RootObjectWithRelationships = RootObject(
    data = Some(
      ResourceObjects(List(
        ResourceObject(
          `type` = "person",
          attributes = Some(List(Attribute("name", StringValue("foobar")))),
          relationships =
            Some(Map("father" -> Relationship(
              data = Some(ResourceObject(`type` = "person")),
              links = Some(List(Link("self", "http://link.to.self")))
            )))
        )
      ))
    )
  )

  protected lazy val resourceObjectWithRelationshipsJsonString =
    """
      |{
      |  "type": "person",
      |  "attributes": {
      |    "name": "foobar"
      |  },
      |  "relationships": {
      |    "father": {
      |      "data": {
      |        "type": "person"
      |      },
      |      "links": {
      |        "self": "http://link.to.self"
      |      }
      |    }
      |  }
      |}
    """.stripMargin

  protected lazy val resourceObjectWithRelationshipsObject = ResourceObject(
    `type` = "person",
    attributes = Some(List(Attribute("name", StringValue("foobar")))),
    relationships =
      Some(Map("father" -> Relationship(
        data = Some(ResourceObject(`type` = "person")),
        links = Some(List(Link("self", "http://link.to.self")))
      )))
  )

  protected lazy val relationshipsJsonString =
    """
      |{
      |  "father": {
      |    "data": {
      |      "type": "person"
      |    },
      |    "links": {
      |      "self": "http://link.to.self"
      |    }
      |  }
      |}
    """.stripMargin

  protected lazy val relationshipsObject = Map(
    "father" -> Relationship(
      data = Some(ResourceObject(`type` = "person")),
      links = Some(List(Link("self", "http://link.to.self")))
    )
  )

  protected lazy val resourceObjectWithEmptyRelationshipsJsonString =
    """
    |{
    |  "data": [{
    |    "type": "person",
    |    "attributes": {
    |      "name": "foobar"
    |    },
    |    "relationships": {
    |      "father": {}
    |    }
    |  }]
    |}
  """.stripMargin

  protected lazy val resourceObjectWithEmptyRelationshipsObject = RootObject(Some(
    ResourceObjects(List(
      ResourceObject(
        `type` = "person",
        attributes = Some(List(Attribute("name", StringValue("foobar")))),
        relationships = Some(Map("father" -> Relationship()))
      )
    ))
  ))
}
