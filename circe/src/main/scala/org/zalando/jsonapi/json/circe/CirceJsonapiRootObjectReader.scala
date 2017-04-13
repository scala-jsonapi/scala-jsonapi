package org.zalando.jsonapi.json.circe

import io.circe.{Json, Encoder}
import org.zalando.jsonapi.JsonapiRootObjectReader
import org.zalando.jsonapi.json.FieldNames
import org.zalando.jsonapi.model.JsonApiObject._
import org.zalando.jsonapi.model.Links.Link
import org.zalando.jsonapi.model.RootObject.{Data, ResourceObject, ResourceObjects}
import org.zalando.jsonapi.model.{Error, _}

trait CirceJsonapiRootObjectReader extends JsonapiRootObjectReader[Json] {
  def readValue(generalValue: Value): Json = generalValue match {
    case NullValue =>
      Json.Null
    case StringValue(value) =>
      Json.fromString(value)
    case BooleanValue(value) =>
      Json.fromBoolean(value)
    case NumberValue(value) =>
      Json.fromBigDecimal(value)
    case JsArrayValue(values) =>
      Json.fromValues(values.map(readValue))
    case JsObjectValue(values) =>
      Json.fromFields(
        values.map {
          case Attribute(name, value) =>
            name -> readValue(value)
        }
      )
  }

  def jsonFromOptionalFields(entries: (String, Option[Json])*): Json = {
    Json.fromFields(
      entries.collect {
        case (name, Some(json)) =>
          name → json
      }
    )
  }

  def readAttributes(attributes: Attributes): Json = {
    Json.fromFields(
      attributes.map {
        case Attribute(name, value) ⇒
          name → readValue(value)
      }
    )
  }

  def readLink(link: Link): Json = {
    val href = link.url
    val name = link.linkType.name

    link.meta match {
      case None ⇒
        Json.obj(name → Json.fromString(href))
      case Some(meta) ⇒
        Json.obj(
          name → Json.obj(
            FieldNames.`href` -> Json.fromString(href),
            FieldNames.`meta` -> readMeta(meta)
          )
        )
    }
  }

  def readLinks(links: Links): Json = {
    links.map(readLink).reduce(_.deepMerge(_))
  }

  def readData(data: Data): Json = {
    data match {
      case resourceObject: ResourceObject =>
        readResourceObject(resourceObject)
      case resourceObjects: ResourceObjects =>
        readResourceObjects(resourceObjects)
    }
  }

  def readRelationship(relationship: Relationship): Json = {
    jsonFromOptionalFields(
      FieldNames.`links` -> relationship.links.map(readLinks),
      FieldNames.`data` -> relationship.data.map(readData)
    )
  }

  def readRelationships(relationships: Relationships): Json = {
    Json.fromFields(
      relationships.toSeq.map {
        case (name, relationship) ⇒
          name → readRelationship(relationship)
      }
    )
  }

  def readJsonApi(jsonApi: JsonApi): Json = {
    Json.fromFields(
      jsonApi.map {
        case JsonApiProperty(name, value) ⇒
          name → readValue(value)
      }
    )
  }

  def readMeta(meta: Meta): Json = {
    Json.fromFields(
      meta.toSeq.map {
        case (name, value) =>
          name -> readValue(value)
      }
    )
  }

  def readErrorSource(errorSource: ErrorSource): Json = {
    jsonFromOptionalFields(
      FieldNames.`pointer` -> errorSource.pointer.map(Json.fromString),
      FieldNames.`parameter` -> errorSource.parameter.map(Json.fromString)
    )
  }

  def readError(error: Error): Json = {
    jsonFromOptionalFields(
      FieldNames.`id` -> error.id.map(Json.fromString),
      FieldNames.`status` -> error.status.map(Json.fromString),
      FieldNames.`code` -> error.code.map(Json.fromString),
      FieldNames.`title` -> error.title.map(Json.fromString),
      FieldNames.`detail` -> error.detail.map(Json.fromString),
      FieldNames.`links` -> error.links.map(readLinks),
      FieldNames.`meta` -> error.meta.map(readMeta),
      FieldNames.`source` -> error.source.map(readErrorSource)
    )
  }

  def readErrors(errors: Errors): Json = {
    Json.fromValues(errors.map(readError))
  }

  def readResourceObject(resourceObject: ResourceObject): Json = {
    jsonFromOptionalFields(
      FieldNames.`type` -> Option(Json.fromString(resourceObject.`type`)),
      FieldNames.`id` -> resourceObject.id.map(Json.fromString),
      FieldNames.`attributes` -> resourceObject.attributes.map(readAttributes),
      FieldNames.`relationships` -> resourceObject.relationships.map(readRelationships),
      FieldNames.`links` -> resourceObject.links.map(readLinks),
      FieldNames.`meta` -> resourceObject.meta.map(readMeta)
    )
  }

  def readResourceObjects(resourceObjects: ResourceObjects): Json = {
    Json.fromValues(resourceObjects.array.map(readResourceObject))
  }

  def readIncluded(included: Included): Json = {
    readResourceObjects(included.resourceObjects)
  }

  implicit val encodeRootObject = new Encoder[RootObject] {
    final def apply(rootObject: RootObject): Json = {
      jsonFromOptionalFields(
        FieldNames.`data` -> rootObject.data.map(readData),
        FieldNames.`links` -> rootObject.links.map(readLinks),
        FieldNames.`errors` -> rootObject.errors.map(readErrors),
        FieldNames.`meta` -> rootObject.meta.map(readMeta),
        FieldNames.`included` -> rootObject.included.map(readIncluded),
        FieldNames.`jsonapi` -> rootObject.jsonApi.map(readJsonApi)
      )
    }
  }

  def fromJsonapi(rootObject: RootObject): Json = {
    encodeRootObject(rootObject)
  }
}

case object CirceJsonapiRootObjectReader extends CirceJsonapiRootObjectReader
