package org.zalando.jsonapi.json.circe

import io.circe._
import io.circe.generic.auto._
import io.circe.syntax._
import org.zalando.jsonapi.json.FieldNames
import org.zalando.jsonapi.model.JsonApiObject._
import org.zalando.jsonapi.model.Links.Link
import org.zalando.jsonapi.model.RootObject.{Data, ResourceObject, ResourceObjects}
import org.zalando.jsonapi.model.{Error, _}

// scalastyle:off public.methods.have.type
trait CirceJsonapiEncoders {
  def valueToJson(generalValue: Value): Json = generalValue match {
    case NullValue =>
      Json.Null
    case StringValue(value) =>
      Json.fromString(value)
    case BooleanValue(value) =>
      Json.fromBoolean(value)
    case NumberValue(value) =>
      Json.fromBigDecimal(value)
    case JsArrayValue(values) =>
      Json.fromValues(values.map(valueToJson))
    case JsObjectValue(values) =>
      Json.fromFields(values.map {
        case Attribute(name, value) =>
          name -> valueToJson(value)
      })
  }

  def jsonFromOptionalFields(entries: (String, Option[Json])*): Json = {
    Json.fromFields(entries.flatMap {
      case (name, valueOption) => valueOption.map(name -> _)
    })
  }

  implicit def valueEncoder[V <: Value] = Encoder.instance[V](valueToJson)

  implicit val attributeEncoder = Encoder.instance[Attribute] {
    case Attribute(name, value) =>
      Json.fromFields(Seq(name -> value.asJson))
  }
  implicit val attributesEncoder = Encoder.instance[Attributes] {
    case Seq(attributes @ _ *) =>
      attributes.map(_.asJson).reduce(_.deepMerge(_))
  }

  implicit val linkEncoder = Encoder.instance[Link] { link =>
    val (name: String, href: String, metaOpt: Option[Meta]) = link match {
      case Links.Self(url, None) => (FieldNames.`self`, url, None)
      case Links.Self(url, Some(meta)) => (FieldNames.`self`, url, Some(meta))

      case Links.About(url, None) => (FieldNames.`about`, url, None)
      case Links.About(url, Some(meta)) => (FieldNames.`about`, url, Some(meta))

      case Links.First(url, None) => (FieldNames.`first`, url, None)
      case Links.First(url, Some(meta)) => (FieldNames.`first`, url, Some(meta))

      case Links.Last(url, None) => (FieldNames.`last`, url, None)
      case Links.Last(url, Some(meta)) => (FieldNames.`last`, url, Some(meta))

      case Links.Next(url, None) => (FieldNames.`next`, url, None)
      case Links.Next(url, Some(meta)) => (FieldNames.`next`, url, Some(meta))

      case Links.Prev(url, None) => (FieldNames.`prev`, url, None)
      case Links.Prev(url, Some(meta)) => (FieldNames.`prev`, url, Some(meta))

      case Links.Related(url, None) => (FieldNames.`related`, url, None)
      case Links.Related(url, Some(meta)) => (FieldNames.`related`, url, Some(meta))
    }
    metaOpt match {
      case None => Json.fromFields(Seq(name -> Json.fromString(href)))
      case Some(meta) =>
        val linkObjectJson = Json.fromFields(Seq("href" -> Json.fromString(href), "meta" -> meta.asJson))
        Json.fromFields(Seq(name -> linkObjectJson))
    }
  }

  implicit val linksEncoder = Encoder.instance[Links](_.map(_.asJson).reduce(_.deepMerge(_)))

  def dataToJson(data: Data): Json = {
    data match {
      case ro: ResourceObject =>
        ro.asJson
      case ros: ResourceObjects =>
        ros.asJson
    }
  }

  lazy implicit val relationshipEncoder = Encoder.instance[Relationship](relationship => {
    jsonFromOptionalFields(
        FieldNames.`links` -> relationship.links.map(_.asJson),
        // TODO: there's prolly a cleaner way here. there's a circular dependency Data -> ResourceObject(s) -> Relationship(s) -> Data that's giving circe problems
        FieldNames.`data` -> relationship.data.map(dataToJson)
    )
  })

  implicit val relationshipsEncoder = Encoder.instance[Relationships](relationships =>
        Json.fromFields(relationships.map {
      case (name, value) => name -> value.asJson
    }))

  implicit val jsonApiEncoder = Encoder.instance[JsonApi] {
    case Seq(jsonApiPropertys @ _ *) =>
      Json.fromFields(jsonApiPropertys.map {
        case JsonApiProperty(name, value) =>
          name -> value.asJson
      })
  }

  implicit val metaEncoder = Encoder.instance[Meta](meta => {
    Json.fromFields(meta.toSeq.map {
      case (name, value) => name -> value.asJson
    })
  })

  implicit val errorSourceEncoder = Encoder.instance[ErrorSource](errorSource => {
    jsonFromOptionalFields(
        FieldNames.`pointer` -> errorSource.pointer.map(Json.fromString),
        FieldNames.`parameter` -> errorSource.parameter.map(Json.fromString)
    )
  })

  implicit val errorEncoder = Encoder.instance[Error](error => {
    jsonFromOptionalFields(
        FieldNames.`id` -> error.id.map(Json.fromString),
        FieldNames.`status` -> error.status.map(Json.fromString),
        FieldNames.`code` -> error.code.map(Json.fromString),
        FieldNames.`title` -> error.title.map(Json.fromString),
        FieldNames.`detail` -> error.detail.map(Json.fromString),
        FieldNames.`links` -> error.links.map(_.asJson),
        FieldNames.`meta` -> error.meta.map(_.asJson),
        FieldNames.`source` -> error.source.map(_.asJson)
    )
  })

  implicit val resourceObjectEncoder = Encoder.instance[ResourceObject](resourceObject => {
    jsonFromOptionalFields(
        FieldNames.`type` -> Option(Json.fromString(resourceObject.`type`)),
        FieldNames.`id` -> resourceObject.id.map(Json.fromString),
        FieldNames.`attributes` -> resourceObject.attributes.map(_.asJson),
        FieldNames.`relationships` -> resourceObject.relationships.map(_.asJson),
        FieldNames.`links` -> resourceObject.links.map(_.asJson),
        FieldNames.`meta` -> resourceObject.meta.map(_.asJson)
    )
  })

  implicit val resourceObjectsEncoder = Encoder.instance[ResourceObjects] {
    case ResourceObjects(resourceObjects) =>
      Json.fromValues(resourceObjects.map(_.asJson))
  }

  lazy implicit val dataEncoder = Encoder.instance[Data](dataToJson)

  implicit val includedEncoder = Encoder.instance[Included](_.resourceObjects.asJson)

  implicit val rootObjectEncoder = Encoder.instance[RootObject](rootObject => {
    jsonFromOptionalFields(
        FieldNames.`data` -> rootObject.data.map(_.asJson),
        FieldNames.`links` -> rootObject.links.map(_.asJson),
        FieldNames.`errors` -> rootObject.errors.map(_.asJson),
        FieldNames.`meta` -> rootObject.meta.map(_.asJson),
        FieldNames.`included` -> rootObject.included.map(_.asJson),
        FieldNames.`jsonapi` -> rootObject.jsonApi.map(_.asJson)
    )
  })
}

object CirceJsonapiEncoders extends CirceJsonapiEncoders
