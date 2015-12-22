package org.zalando.jsonapi.json.sprayjson

import org.scalatest.MustMatchers
import org.zalando.jsonapi.json.JsonBaseSpec
import org.zalando.jsonapi.model._
import spray.json._

class SprayJsonJsonapiFormatSpec extends JsonBaseSpec[JsValue] with MustMatchers with SprayJsonJsonapiProtocol {

  override protected def parseJson(jsonString: String): JsValue = jsonString.parseJson

  "SprayJsonJsonapiFormat" when {
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
