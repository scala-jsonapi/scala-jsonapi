package org.zalando.jsonapi.json.circe

import cats.data.Xor
import io.circe._
import org.zalando.jsonapi.json.FieldNames
import org.zalando.jsonapi.model.JsonApiObject._
import org.zalando.jsonapi.model.RootObject._
import org.zalando.jsonapi.model.{Errors, Error, _}

trait CirceJsonapiDecoders {
  def jsonToValue(json: Json): Value = json.fold[Value](
      NullValue,
      BooleanValue.apply,
      value ⇒ NumberValue(value.toBigDecimal.get),
      StringValue.apply,
      values ⇒ JsArrayValue(values.map(jsonToValue)),
      values ⇒
        JsObjectValue(values.toMap.map {
          case (key, value) ⇒ Attribute(key, jsonToValue(value))
        }.toList)
  )

  implicit val valueDecoder = Decoder.instance[Value](_.as[Json].map(jsonToValue))

  implicit val attributesDecoder = Decoder.instance[Attributes](hcursor ⇒ {
    hcursor.as[Value].flatMap {
      case JsObjectValue(value) ⇒
        Xor.Right(value)
      case _ ⇒
        Xor.Left(DecodingFailure("only an object can be decoded to Attributes", hcursor.history))
    }
  })

  implicit val attributeDecoder = Decoder.instance[Attribute](_.as[Attributes].map(_.head))

  implicit val linksDecoder = Decoder.instance[Links](hcursor ⇒ {
    hcursor.as[Value].flatMap {
      case JsObjectValue(attributes) ⇒
        Xor.Right(attributes.map {
          case Attribute(FieldNames.`self`, StringValue(url)) ⇒ Links.Self(url)
          case Attribute(FieldNames.`about`, StringValue(url)) ⇒
            Links.About(url)
          case Attribute(FieldNames.`first`, StringValue(url)) ⇒
            Links.First(url)
          case Attribute(FieldNames.`last`, StringValue(url)) ⇒ Links.Last(url)
          case Attribute(FieldNames.`next`, StringValue(url)) ⇒ Links.Next(url)
          case Attribute(FieldNames.`prev`, StringValue(url)) ⇒ Links.Prev(url)
          case Attribute(FieldNames.`related`, StringValue(url)) ⇒
            Links.Related(url)
        })
      case _ ⇒
        Xor.Left(DecodingFailure("only an object can be decoded to Links", hcursor.history))
    }
  })

  def jsonToData(json: Json): Xor[DecodingFailure, Data] = json match {
    case json: Json if json.isArray ⇒
      json.as[ResourceObjects]
    case json: Json if json.isObject ⇒
      json.as[ResourceObject]
  }

  implicit val relationshipDecoder = Decoder.instance[Relationship](hcursor ⇒ {
    for {
      links ← hcursor.downField(FieldNames.`links`).as[Option[Links]]
      // TODO: there's prolly a cleaner way here. there's a circular dependency Data -> ResourceObject(s) -> Relationship(s) -> Data that's giving circe problems
      data ← hcursor.downField(FieldNames.`data`).as[Option[Json]].map(_.flatMap(jsonToData(_).toOption))
    } yield
      Relationship(
          links = links,
          data = data
      )
  })

  implicit val relationshipsDecoder = Decoder.instance[Relationships](_.as[Map[String, Relationship]])

  implicit val jsonApiDecoder = Decoder.instance[JsonApi](hcursor ⇒ {
    hcursor.as[Value].flatMap {
      case JsObjectValue(attributes) ⇒
        Xor.Right(attributes.map {
          case Attribute(name, value) ⇒ JsonApiProperty(name, value)
        })
      case _ ⇒
        Xor.Left(DecodingFailure("only an object can be decoded to JsonApi", hcursor.history))
    }
  })

  implicit val metaDecoder = Decoder.instance[Meta](hcursor ⇒ {
    hcursor.as[Value].flatMap {
      case JsObjectValue(attributes) ⇒
        Xor.Right(attributes.map {
          case Attribute(name, value) ⇒ name -> value
        }.toMap)
      case _ ⇒
        Xor.Left(DecodingFailure("only an object can be decoded to Meta", hcursor.history))
    }
  })

  implicit val errorSourceDecoder = Decoder.instance[ErrorSource](hcursor ⇒ {
    for {
      pointer ← hcursor.downField(FieldNames.`pointer`).as[Option[String]]
      parameter ← hcursor.downField(FieldNames.`parameter`).as[Option[String]]
    } yield
      ErrorSource(
          pointer = pointer,
          parameter = parameter
      )
  })

  implicit val errorDecoder = Decoder.instance[Error](hcursor ⇒ {
    for {
      id ← hcursor.downField(FieldNames.`id`).as[Option[String]]
      status ← hcursor.downField(FieldNames.`status`).as[Option[String]]
      code ← hcursor.downField(FieldNames.`code`).as[Option[String]]
      title ← hcursor.downField(FieldNames.`title`).as[Option[String]]
      detail ← hcursor.downField(FieldNames.`detail`).as[Option[String]]
      links ← hcursor.downField(FieldNames.`links`).as[Option[Links]]
      meta ← hcursor.downField(FieldNames.`meta`).as[Option[Meta]]
      source ← hcursor.downField(FieldNames.`source`).as[Option[ErrorSource]]
    } yield
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
  })

  implicit val resourceObjectDecoder = Decoder.instance[ResourceObject](hcursor ⇒ {
    for {
      id ← hcursor.downField(FieldNames.`id`).as[Option[String]]
      `type` ← hcursor.downField(FieldNames.`type`).as[String]
      attributes ← hcursor.downField(FieldNames.`attributes`).as[Option[Attributes]]
      relationships ← hcursor.downField(FieldNames.`relationships`).as[Option[Relationships]]
      links ← hcursor.downField(FieldNames.`links`).as[Option[Links]]
      meta ← hcursor.downField(FieldNames.`meta`).as[Option[Meta]]
    } yield
      ResourceObject(
          id = id,
          `type` = `type`,
          attributes = attributes,
          relationships = relationships,
          links = links,
          meta = meta
      )
  })

  implicit val resourceObjectsDecoder =
    Decoder.instance[ResourceObjects](_.as[List[ResourceObject]].map(ResourceObjects))

  implicit val dataDecoder = Decoder.instance[Data](_.as[Json].flatMap(jsonToData))

  implicit val includedDecoder = Decoder.instance[Included](_.as[ResourceObjects].map(Included.apply))

  implicit val rootObjectDecoder = Decoder.instance[RootObject](hcursor ⇒ {
    for {
      data ← hcursor.downField(FieldNames.`data`).as[Option[Data]]
      links ← hcursor.downField(FieldNames.`links`).as[Option[Links]]
      errors ← hcursor.downField(FieldNames.`errors`).as[Option[Errors]]
      meta ← hcursor.downField(FieldNames.`meta`).as[Option[Meta]]
      included ← hcursor.downField(FieldNames.`included`).as[Option[Included]]
      jsonapi ← hcursor.downField(FieldNames.`jsonapi`).as[Option[JsonApi]]
    } yield
      RootObject(
          data = data,
          links = links,
          errors = errors,
          meta = meta,
          included = included,
          jsonApi = jsonapi
      )
  })
}

object CirceJsonapiDecoders extends CirceJsonapiDecoders
