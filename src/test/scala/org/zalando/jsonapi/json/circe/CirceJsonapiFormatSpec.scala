package org.zalando.jsonapi.json.circe

import io.circe.Json
import io.circe.parser.parse
import io.circe.syntax._
import org.scalatest.MustMatchers
import org.zalando.jsonapi.json.JsonBaseSpec
import org.zalando.jsonapi.model._

class CirceJsonapiFormatSpec extends JsonBaseSpec[Json] with MustMatchers with CirceJsonapiEncoders with CirceJsonapiDecoders {

  override protected def parseJson(jsonString: String): Json = parse(jsonString).toOption.get
  protected def decodeJson[T](json: Json)(implicit d: io.circe.Decoder[T]): T = json.as[T].toOption.get

  "CirceJsonapiFormat" when {
    "serializing Jsonapi" must {
      "transform attributes correctly" in {
        attributes.asJson mustEqual attributesJson
      }
      "transform resource object correctly" in {
        rootObjectWithResourceObjectWithoutAttributes.asJson mustEqual rootObjectWithResourceObjectWithoutAttributesJson
      }
      "transform a list of resource objects correctly" in {
        rootObjectWithResourceObjects.asJson mustEqual rootObjectWithResourceObjectsJson
      }
      "transform resource identifier object correctly" in {
        rootObjectWithResourceIdentifierObject.asJson mustEqual rootObjectWithResourceIdentifierObjectJson
      }
      "transform a list of resource identifier objects correctly" in {
        rootObjectWithResourceIdentifierObjects.asJson mustEqual rootObjectWithResourceIdentifierObjectsJson
      }
      "transform all link types correctly" in {
        rootObjectWithResourceObjectsWithAllLinks.asJson mustEqual rootObjectWithResourceObjectsWithAllLinksJson
      }
      "transform all meta object inside resource object correctly" in {
        rootObjectWithResourceObjectsWithMeta.asJson mustEqual rootObjectWithResourceObjectsWithMetaJson
      }
      "transform a meta object in root object correctly" in {
        rootObjectWithMeta.asJson mustEqual rootObjectWithMetaJson
      }
      "transform error object correctly" in {
        rootObjectWithErrorsObject.asJson mustEqual rootObjectWithErrorsJson
      }
      "transform error object without links and meta and source correctly" in {
        rootObjectWithErrorsNoneObject.asJson mustEqual rootObjectWithErrorsNoneJson
      }
      "transform included object correctly" in {
        rootObjectWithIncluded.asJson mustEqual rootObjectWithIncludedJson
      }
      "transform jsonapi object correctly" in {
        rootObjectWithJsonApiObject.asJson mustEqual rootObjectWithJsonApiObjectJson
      }
      "transform relationship object correctly" in {
        RootObjectWithRelationships.asJson mustEqual rootObjectWithRelationshipsJson
      }
      "transform empty relationship object correctly" in {
        resourceObjectWithEmptyRelationshipsObject.asJson mustEqual resourceObjectWithEmptyRelationshipsJson
      }
    }
    "deserializing Jsonapi" must {
      "transform attributes correctly" in {
        decodeJson[Attributes](attributesJson) === attributes
      }
      "transform resource object correctly" in {
        decodeJson[RootObject](rootObjectWithResourceObjectWithoutAttributesJson) === rootObjectWithResourceObjectWithoutAttributes
      }
      "transform a list of resource objects correctly" in {
        decodeJson[RootObject](rootObjectWithResourceObjectsJson) === rootObjectWithResourceObjects
      }
      "transform resource identifier object correctly" in {
        decodeJson[RootObject](rootObjectWithResourceIdentifierObjectJson) === rootObjectWithResourceIdentifierObject
      }
      "transform a list of resource identifier objects correctly" in {
        decodeJson[RootObject](rootObjectWithResourceIdentifierObjectsJson) === rootObjectWithResourceIdentifierObjects
      }
      "transform all link types correctly" in {
        decodeJson[RootObject](rootObjectWithResourceObjectsWithAllLinksJson) === rootObjectWithResourceObjectsWithAllLinks
      }
      "transform all meta object inside resource object correctly" in {
        decodeJson[RootObject](rootObjectWithResourceObjectsWithMetaJson) === rootObjectWithResourceObjectsWithMeta
      }
      "transform a meta object in root object correctly" in {
        decodeJson[RootObject](rootObjectWithMetaJson) === rootObjectWithMeta
      }
      "transform error object correctly" in {
        decodeJson[RootObject](rootObjectWithErrorsJson) === rootObjectWithErrorsObject
      }
      "transform error object without links and meta and source correctly" in {
        decodeJson[RootObject](rootObjectWithErrorsNoneJson) === rootObjectWithErrorsNoneObject
      }
      "transform included object correctly" in {
        decodeJson[RootObject](rootObjectWithIncludedJson) === rootObjectWithIncluded
      }
      "transform jsonapi object correctly" in {
        decodeJson[RootObject](rootObjectWithJsonApiObjectJson) === rootObjectWithJsonApiObject
      }
      "transform relationship object correctly" in {
        decodeJson[RootObject](rootObjectWithRelationshipsJson) === RootObjectWithRelationships
      }
      "transform empty relationship object correctly" in {
        decodeJson[RootObject](resourceObjectWithEmptyRelationshipsJson) === resourceObjectWithEmptyRelationshipsObject
      }
    }
  }
}
