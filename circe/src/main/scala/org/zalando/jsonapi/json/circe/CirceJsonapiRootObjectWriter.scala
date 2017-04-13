package org.zalando.jsonapi.json.circe

import io.circe.{Json, HCursor, DecodingFailure}
import org.zalando.jsonapi.JsonapiRootObjectWriter
import org.zalando.jsonapi.json.FieldNames
import org.zalando.jsonapi.model.JsonApiObject._
import org.zalando.jsonapi.model.Links.Link
import org.zalando.jsonapi.model.RootObject.{Data, ResourceObject, ResourceObjects}
import org.zalando.jsonapi.model.{Error, _}
import io.circe.Decoder, Decoder.Result
// TODO: some of these fail tests b/c of ordering, maybe should be modeled as Maps instead of ImmutableSeqs upstream?
import scala.collection.immutable.ListMap

trait CirceJsonapiRootObjectWriter extends JsonapiRootObjectWriter[Json] {
  // proxying Decoder.Result with transparent right biasing for scala 2.11
  implicit class CrossVersionResult[R](result: Either[DecodingFailure, R]) {

    def map[Y](f: (R) ⇒ Y): Either[DecodingFailure, Y] = result.right.map(f)
    def flatMap[AA >: DecodingFailure, Y](f: (R) ⇒ Either[AA, Y]): Either[AA, Y] = result.right.flatMap(f)
  }

  def writeValue(json: Json): Value = {
    json.fold[Value](
      NullValue,
      BooleanValue.apply,
      value ⇒ NumberValue(value.toBigDecimal.get),
      StringValue.apply,
      values ⇒ JsArrayValue(values.map(writeValue)),
      values ⇒ JsObjectValue(
        values.toList.map {
          case (key, value) ⇒
            Attribute(key, writeValue(value))
        }
      )
    )
  }

  def writeAttributes(hcursor: HCursor): Result[Attributes] = {
    for {
      attributes ← hcursor.as[ListMap[String, Json]]
    } yield {
      attributes.toList.map {
        case (name, value) ⇒
          Attribute(name, writeValue(value))
      }
    }
  }

  implicit val decodeAttributes = new Decoder[Attributes] {
    final def apply(c: HCursor): Result[Attributes] = writeAttributes(c)
  }

  def writeMeta(hcursor: HCursor): Result[Meta] = {
    for {
      meta ← hcursor.as[Map[String, Json]]
    } yield {
      meta.map {
        case (name, value) ⇒
          name → writeValue(value)
      }
    }
  }

  implicit val decodeMeta = new Decoder[Meta] {
    final def apply(c: HCursor): Result[Meta] = writeMeta(c)
  }

  def writeLink(name: String, hrefMeta: Json): Result[Link] = {
    if (hrefMeta.isString) {
      Right(Link(name, hrefMeta.asString.get, None))
    } else {
      val hcursor = hrefMeta.hcursor
      for {
        href ← hcursor.get[String](FieldNames.`href`)
        meta ← hcursor.get[Option[Meta]](FieldNames.`meta`)
      } yield {
        Link(name, href, meta)
      }
    }
  }

  def writeLinks(hcursor: HCursor): Result[Links] = {
    for {
      linkMap ← hcursor.as[ListMap[String, Json]]
      links ← linkMap.foldLeft[Result[Links]](Right(Nil)) {
        case (linksOrError, (name, hrefMeta)) ⇒
          for {
            links ← linksOrError
            link ← writeLink(name, hrefMeta)
          } yield {
            links :+ link
          }
      }
    } yield {
      links
    }
  }

  implicit val decodeLinks = new Decoder[Links] {
    final def apply(c: HCursor): Result[Links] = writeLinks(c)
  }

  implicit val decodeData = new Decoder[Data] {
    final def apply(c: HCursor): Result[Data] = writeData(c)
  }

  def writeRelationship(json: Json): Result[Relationship] = {
    val hcursor = json.hcursor
    for {
      links ← hcursor.get[Option[Links]](FieldNames.`links`)
      data ← hcursor.get[Option[Data]](FieldNames.`data`)
    } yield {
      Relationship(
        links = links,
        data = data
      )
    }
  }

  def writeRelationships(hcursor: HCursor): Result[Relationships] = {
    for {
      relationshipMap ← hcursor.as[Map[String, Json]]
      relationships ← relationshipMap.foldLeft[Result[Relationships]](Right(Map.empty)) {
        case (relationshipsOrError, (name, relationshipJson)) ⇒
          for {
            relationships ← relationshipsOrError
            relationship ← writeRelationship(relationshipJson)
          } yield {
            relationships + (name → relationship)
          }
      }
    } yield {
      relationships
    }
  }

  implicit val decodeRelationships = new Decoder[Relationships] {
    final def apply(c: HCursor): Result[Relationships] = writeRelationships(c)
  }

  def writeResourceObject(hcursor: HCursor): Result[ResourceObject] = {
    for {
      id ← hcursor.get[Option[String]](FieldNames.`id`)
      `type` ← hcursor.get[String](FieldNames.`type`)
      attributes ← hcursor.get[Option[Attributes]](FieldNames.`attributes`)
      relationships ← hcursor.get[Option[Relationships]](FieldNames.`relationships`)
      links ← hcursor.get[Option[Links]](FieldNames.`links`)
      meta ← hcursor.get[Option[Meta]](FieldNames.`meta`)
    } yield {
      ResourceObject(
        id = id,
        `type` = `type`,
        attributes = attributes,
        relationships = relationships,
        links = links,
        meta = meta
      )
    }
  }

  implicit val decodeResourceObject = new Decoder[ResourceObject] {
    final def apply(c: HCursor): Result[ResourceObject] = writeResourceObject(c)
  }

  def writeResourceObjects(json: Json): Result[ResourceObjects] = {
    for {
      resourceObjects ← json.as[List[ResourceObject]]
    } yield {
      ResourceObjects(resourceObjects)
    }
  }

  def writeData(hcursor: HCursor): Result[Data] = {
    for {
      json ← hcursor.as[Json]
      result ← json.arrayOrObject(
        Left(DecodingFailure("data must be an array or an object", hcursor.history)),
        _ ⇒ writeResourceObjects(json),
        _ ⇒ writeResourceObject(hcursor)
      )
    } yield {
      result
    }
  }

  def writeErrorSource(hcursor: HCursor): Result[ErrorSource] = {
    for {
      pointer ← hcursor.get[Option[String]](FieldNames.`pointer`)
      parameter ← hcursor.get[Option[String]](FieldNames.`parameter`)
    } yield {
      ErrorSource(
        pointer = pointer,
        parameter = parameter
      )
    }
  }

  implicit val decodeErrorSource = new Decoder[ErrorSource] {
    final def apply(c: HCursor): Result[ErrorSource] = writeErrorSource(c)
  }

  def writeError(hcursor: HCursor): Result[Error] = {
    for {
      id ← hcursor.get[Option[String]](FieldNames.`id`)
      status ← hcursor.get[Option[String]](FieldNames.`status`)
      code ← hcursor.get[Option[String]](FieldNames.`code`)
      title ← hcursor.get[Option[String]](FieldNames.`title`)
      detail ← hcursor.get[Option[String]](FieldNames.`detail`)
      links ← hcursor.get[Option[Links]](FieldNames.`links`)
      meta ← hcursor.get[Option[Meta]](FieldNames.`meta`)
      source ← hcursor.get[Option[ErrorSource]](FieldNames.`source`)
    } yield {
      Error(
        id = id,
        status = status,
        code = code,
        title = title,
        detail = detail,
        links = links,
        meta = meta,
        source = source
      )
    }
  }

  implicit val decodeError = new Decoder[Error] {
    final def apply(c: HCursor): Result[Error] = writeError(c)
  }

  def writeIncluded(hcursor: HCursor): Result[Included] = {
    for {
      json ← hcursor.as[Json]
      resourceObjects ← writeResourceObjects(json)
    } yield {
      Included(resourceObjects)
    }
  }

  implicit val decodeIncluded = new Decoder[Included] {
    final def apply(c: HCursor): Result[Included] = writeIncluded(c)
  }

  implicit val decodeJsonApi = new Decoder[JsonApi] {
    final def apply(c: HCursor): Result[JsonApi] = for {
      propertyMap ← c.as[Map[String, Json]]
    } yield {
      propertyMap.toList.map {
        case (name, value) ⇒
          JsonApiProperty(name, writeValue(value))
      }
    }
  }

  implicit val decodeRootObject = new Decoder[RootObject] {
    final def apply(hcursor: HCursor): Result[RootObject] = {
      for {
        data ← hcursor.downField(FieldNames.`data`).as[Option[Data]].right
        links ← hcursor.downField(FieldNames.`links`).as[Option[Links]].right
        errors ← hcursor.downField(FieldNames.`errors`).as[Option[Errors]].right
        meta ← hcursor.downField(FieldNames.`meta`).as[Option[Meta]].right
        included ← hcursor.downField(FieldNames.`included`).as[Option[Included]].right
        jsonapi ← hcursor.downField(FieldNames.`jsonapi`).as[Option[JsonApi]].right
      } yield {
        RootObject(
          data = data,
          links = links,
          errors = errors,
          meta = meta,
          included = included,
          jsonApi = jsonapi
        )
      }
    }
  }

  def toJsonapi(json: Json): RootObject = {
    json.as[RootObject] match {
      case Left(cause) ⇒
        throw cause
      case Right(rootObject) ⇒
        rootObject
    }
  }
}

case object CirceJsonapiRootObjectWriter extends CirceJsonapiRootObjectWriter
