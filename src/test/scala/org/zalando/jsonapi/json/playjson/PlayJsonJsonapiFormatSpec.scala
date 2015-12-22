package org.zalando.jsonapi.json.playjson

import org.scalatest.MustMatchers
import org.zalando.jsonapi.json.JsonBaseSpec
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
        Json.toJson(resourceObjectWithRelationships) mustEqual resourceObjectWithRelationshipsJson
      }
      "transform empty relationship object correctly" in {
        Json.toJson(resourceObjectWithEmptyRelationshipsObject) mustEqual resourceObjectWithEmptyRelationshipsJson
      }
    }
  }
}
