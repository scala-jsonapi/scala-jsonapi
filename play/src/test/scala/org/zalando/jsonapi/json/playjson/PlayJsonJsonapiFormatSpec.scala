package org.zalando.jsonapi.json.playjson

import org.scalatest.MustMatchers
import org.zalando.jsonapi.json.JsonBaseSpec
import org.zalando.jsonapi.model.RootObject.ResourceObject
import org.zalando.jsonapi.model._
import play.api.libs.json._

class PlayJsonJsonapiFormatSpec extends JsonBaseSpec[JsValue] with MustMatchers with PlayJsonJsonapiFormat {

  override protected def parseJson(jsonString: String) = Json.parse(jsonString)

  "PlayJsonJsonapiFormat" when {
    "serializing Jsonapi" must {
      "transform attributes correctly" in {
        Json.toJson(attributes) mustEqual attributesJson
      }
      "transform resource object correctly" in {
        Json.toJson(rootObjectWithResourceObjectWithoutAttributes) mustEqual rootObjectWithResourceObjectWithoutAttributesJson
      }
      "transform a list of resource objects correctly" in {
        Json.toJson(rootObjectWithResourceObjects) mustEqual rootObjectWithResourceObjectsJson
      }
      "transform resource identifier object correctly" in {
        Json.toJson(rootObjectWithResourceIdentifierObject) mustEqual rootObjectWithResourceIdentifierObjectJson
      }
      "transform a list of resource identifier objects correctly" in {
        Json.toJson(rootObjectWithResourceIdentifierObjects) mustEqual rootObjectWithResourceIdentifierObjectsJson
      }
      "transform all link types correctly" in {
        Json.toJson(rootObjectWithResourceObjectsWithAllLinks) mustEqual rootObjectWithResourceObjectsWithAllLinksJson
      }
      "transform all meta object inside resource object correctly" in {
        Json.toJson(rootObjectWithResourceObjectsWithMeta) mustEqual rootObjectWithResourceObjectsWithMetaJson
      }
      "transform a meta object in root object correctly" in {
        Json.toJson(rootObjectWithMeta) mustEqual rootObjectWithMetaJson
      }
      "transform error object correctly" in {
        Json.toJson(rootObjectWithErrorsObject) mustEqual rootObjectWithErrorsJson
      }
      "transform error object without links and meta and source correctly" in {
        Json.toJson(rootObjectWithErrorsNoneObject) mustEqual rootObjectWithErrorsNoneJson
      }
      "transform included object correctly" in {
        Json.toJson(rootObjectWithIncluded) mustEqual rootObjectWithIncludedJson
      }
      "transform jsonapi object correctly" in {
        Json.toJson(rootObjectWithJsonApiObject) mustEqual rootObjectWithJsonApiObjectJson
      }
      "transform relationship object correctly" in {
        Json.toJson(RootObjectWithRelationships) mustEqual rootObjectWithRelationshipsJson
      }
      "transform empty relationship object correctly" in {
        Json.toJson(resourceObjectWithEmptyRelationshipsObject) mustEqual resourceObjectWithEmptyRelationshipsJson
      }
    }
    "deserializing Jsonapi" must {
      "transform attributes correctly" in {
        Json.fromJson[Attributes](attributesJson) mustEqual JsSuccess(attributes)
      }
      "transform resource object correctly" in {
        Json.fromJson[RootObject](rootObjectWithResourceObjectWithoutAttributesJson) mustEqual JsSuccess(rootObjectWithResourceObjectWithoutAttributes)
      }
      "transform a list of resource objects correctly" in {
        Json.fromJson[RootObject](rootObjectWithResourceObjectsJson) mustEqual JsSuccess(rootObjectWithResourceObjects)
      }
      "transform resource identifier object correctly" in {
        Json.fromJson[RootObject](rootObjectWithResourceIdentifierObjectJson) mustEqual JsSuccess(rootObjectWithResourceIdentifierObject)
      }
      "transform a list of resource identifier objects correctly" in {
        Json.fromJson[RootObject](rootObjectWithResourceIdentifierObjectsJson) mustEqual JsSuccess(rootObjectWithResourceIdentifierObjects)
      }
      "transform all link types correctly" in {
        Json.fromJson[RootObject](rootObjectWithResourceObjectsWithAllLinksJson) mustEqual JsSuccess(rootObjectWithResourceObjectsWithAllLinks)
      }
      "transform all meta object inside resource object correctly" in {
        Json.fromJson[RootObject](rootObjectWithResourceObjectsWithMetaJson) mustEqual JsSuccess(rootObjectWithResourceObjectsWithMeta)
      }
      "transform a meta object in root object correctly" in {
        Json.fromJson[RootObject](rootObjectWithMetaJson) mustEqual JsSuccess(rootObjectWithMeta)
      }
      "transform error object correctly" in {
        Json.fromJson[RootObject](rootObjectWithErrorsJson) mustEqual JsSuccess(rootObjectWithErrorsObject)
      }
      "transform error object without links and meta and source correctly" in {
        Json.fromJson[RootObject](rootObjectWithErrorsNoneJson) mustEqual JsSuccess(rootObjectWithErrorsNoneObject)
      }
      "transform included object correctly" in {
        Json.fromJson[RootObject](rootObjectWithIncludedJson) mustEqual JsSuccess(rootObjectWithIncluded)
      }
      "transform jsonapi object correctly" in {
        Json.fromJson[RootObject](rootObjectWithJsonApiObjectJson) mustEqual JsSuccess(rootObjectWithJsonApiObject)
      }
      "transform resource object with relationships correctly" in {
        Json.fromJson[ResourceObject](resourceObjectWithRelationshipsJson) mustEqual JsSuccess(resourceObjectWithRelationshipsObject)
      }
      "transform relationships object correctly" in {
        Json.fromJson[Relationships](relationshipsJson) mustEqual JsSuccess(relationshipsObject)
      }
      "transform root object with relationship correctly" in {
        Json.fromJson[RootObject](rootObjectWithRelationshipsJson) mustEqual JsSuccess(RootObjectWithRelationships)
      }
      "transform empty relationship object correctly" in {
        Json.fromJson[RootObject](resourceObjectWithEmptyRelationshipsJson) mustEqual JsSuccess(resourceObjectWithEmptyRelationshipsObject)
      }
    }
  }
}
