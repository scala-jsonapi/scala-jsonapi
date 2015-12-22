package org.zalando.jsonapi.json.playjson

import org.zalando.jsonapi.json.FieldNames
import org.zalando.jsonapi.model.JsonApiObject.Value
import org.zalando.jsonapi.model._
import org.zalando.jsonapi.model.RootObject._
import play.api.libs.json._
import play.api.libs.functional.syntax._

import collection.immutable.{ Seq ⇒ ImmutableSeq }

trait PlayJsonJsonapiFormat {

  /**
   * Play-JSON format for serializing and deserializing Jsonapi [[RootObject]].
   */
  implicit lazy val rootObjectFormat: Format[RootObject] = (
    (JsPath \ FieldNames.`data`).formatNullable[Data] and
    (JsPath \ FieldNames.`links`).formatNullable[Links] and
    (JsPath \ FieldNames.`errors`).formatNullable[Errors] and
    (JsPath \ FieldNames.`meta`).formatNullable[Meta] and
    (JsPath \ FieldNames.`included`).formatNullable[Included] and
    (JsPath \ FieldNames.`jsonapi`).formatNullable[JsonApi]
  )(RootObject.apply, unlift(RootObject.unapply))

  /**
   * Play-JSON format for serializing and deserializing Jsonapi [[Data]].
   */
  implicit lazy val dataFormat: Format[Data] = new Format[Data] {
    override def writes(data: Data): JsValue = data match {
      case ro: ResourceObject ⇒ resourceObjectFormat writes ro
      case ResourceObjects(ros) ⇒
        val values: ImmutableSeq[JsValue] = ros map (resourceObjectFormat writes _)
        JsArray(values)
    }

    override def reads(json: JsValue): JsResult[Data] = ???
  }

  /**
   * Play-JSON format for serializing and deserializing Jsonapi [[ResourceObject]].
   */
  implicit lazy val resourceObjectFormat: Format[ResourceObject] = (
    (JsPath \ FieldNames.`type`).format[String] and
    (JsPath \ FieldNames.`id`).format[String] and
    (JsPath \ FieldNames.`attributes`).formatNullable[Attributes] and
    (JsPath \ FieldNames.`relationships`).formatNullable[Relationships] and
    (JsPath \ FieldNames.`links`).formatNullable[Links] and
    (JsPath \ FieldNames.`meta`).formatNullable[Meta]
  )(ResourceObject.apply, unlift(ResourceObject.unapply))

  /**
   * Play-JSON format for serializing and deserializing Jsonapi [[Attributes]].
   */
  implicit lazy val attributesFormat: Format[Attributes] = new Format[Attributes] {
    override def writes(attributes: Attributes): JsValue = {
      val fields = attributes.map(a ⇒ a.name -> Json.toJson(a.value))
      JsObject(fields)
    }

    override def reads(json: JsValue): JsResult[Attributes] = json match {
      case JsObject(fields) ⇒
        fields.foldLeft[JsResult[Attributes]](JsSuccess(Vector.empty)) {
          case (acc, (name, jsValue)) ⇒ (acc, jsValue.validate[JsonApiObject.Value]) match {
            case (JsSuccess(attrs, _), JsSuccess(value, _)) ⇒
              JsSuccess(attrs :+ Attribute(name, value))
            case (JsSuccess(_, _), JsError(errors)) ⇒
              JsError(Seq(JsPath \ name -> errors.flatMap(_._2)))
            case (e: JsError, s: JsSuccess[_]) ⇒
              e
            case (e: JsError, JsError(errors)) ⇒
              e ++ JsError(Seq(JsPath \ name -> errors.flatMap(_._2)))
          }
        }
      case _ ⇒ JsError("error.expected.jsobject")
    }
  }

  /**
   * Play-JSON format for serializing and deserializing Jsonapi [[Meta]].
   */
  implicit lazy val metaFormat: Format[Meta] = new Format[Meta] {
    override def writes(meta: Meta): JsValue = {
      val fields = meta.map(mp ⇒ (mp.name, Json.toJson(mp.value)))
      JsObject(fields)
    }

    override def reads(json: JsValue): JsResult[Meta] = ???
  }

  /**
   * Play-JSON format for serializing and deserializing Jsonapi [[JsonApi]].
   */
  implicit lazy val jsonApiFormat: Format[JsonApi] = new Format[JsonApi] {
    override def writes(jsonApi: JsonApi): JsValue = {
      val fields = jsonApi.map(jap ⇒ (jap.name, Json.toJson(jap.value)))
      JsObject(fields)
    }

    override def reads(json: JsValue): JsResult[JsonApi] = ???
  }

  /**
   * Play-JSON format for serializing and deserializing Jsonapi [[Errors]].
   */
  implicit lazy val errorsFormat: Format[Errors] = new Format[Errors] {
    override def writes(errors: Errors): JsValue = {
      val fields = errors.map(Json.toJson(_))
      JsArray(fields)
    }

    override def reads(json: JsValue): JsResult[Errors] = ???
  }

  /**
   * Play-JSON format for serializing and deserializing Jsonapi [[Error]].
   */
  implicit lazy val errorFormat: Format[Error] = (
    (JsPath \ FieldNames.`id`).formatNullable[String] and
    (JsPath \ FieldNames.`links`).formatNullable[Links] and
    (JsPath \ FieldNames.`status`).formatNullable[String] and
    (JsPath \ FieldNames.`code`).formatNullable[String] and
    (JsPath \ FieldNames.`title`).formatNullable[String] and
    (JsPath \ FieldNames.`detail`).formatNullable[String] and
    (JsPath \ FieldNames.`source`).formatNullable[ErrorSource] and
    (JsPath \ FieldNames.`meta`).formatNullable[Meta]
  )(Error.apply, unlift(Error.unapply))

  /**
   * Play-JSON format for serializing and deserializing Jsonapi [[ErrorSource]].
   */
  implicit lazy val errorSourceFormat: Format[ErrorSource] = (
    (JsPath \ FieldNames.`pointer`).formatNullable[String] and
    (JsPath \ FieldNames.`parameter`).formatNullable[String]
  )(ErrorSource.apply, unlift(ErrorSource.unapply))

  /**
   * Play-JSON format for serializing and deserializing Jsonapi [[Relationship]].
   */
  implicit lazy val relationshipFormat: Format[Relationship] = (
    (JsPath \ FieldNames.`links`).formatNullable[Links] and
    (JsPath \ FieldNames.`data`).formatNullable[Data]
  )(Relationship.apply, unlift(Relationship.unapply))

  /**
   * Play-JSON format for serializing and deserializing Jsonapi [[JsonApiObject]].
   */
  implicit lazy val jsonApiObjectValueFormat: Format[JsonApiObject.Value] = new Format[JsonApiObject.Value] {
    override def writes(value: JsonApiObject.Value): JsValue = value match {
      case JsonApiObject.StringValue(s)   ⇒ JsString(s)
      case JsonApiObject.NumberValue(n)   ⇒ JsNumber(n)
      case JsonApiObject.BooleanValue(b)  ⇒ JsBoolean(b)
      case JsonApiObject.JsObjectValue(o) ⇒ JsObject(o.map (a ⇒ a.name -> Json.toJson[JsonApiObject.Value](a.value)))
      case JsonApiObject.JsArrayValue(a)  ⇒ JsArray(a.map(v ⇒ Json.toJson[JsonApiObject.Value](v)))
      case JsonApiObject.NullValue        ⇒ JsNull
    }

    override def reads(json: JsValue): JsResult[Value] = ???
  }

  /**
   * Play-JSON format for serializing and deserializing Jsonapi [[Links]].
   */
  implicit lazy val linksFormat: Format[Links] = new Format[Links] {
    override def writes(links: Links): JsValue = {
      val fields = links.map {
        _ match {
          case Links.About(u)   ⇒ (FieldNames.`about`, JsString(u))
          case Links.First(u)   ⇒ (FieldNames.`first`, JsString(u))
          case Links.Last(u)    ⇒ (FieldNames.`last`, JsString(u))
          case Links.Next(u)    ⇒ (FieldNames.`next`, JsString(u))
          case Links.Prev(u)    ⇒ (FieldNames.`prev`, JsString(u))
          case Links.Related(u) ⇒ (FieldNames.`related`, JsString(u))
          case Links.Self(u)    ⇒ (FieldNames.`self`, JsString(u))
        }
      }
      JsObject(fields)
    }

    override def reads(json: JsValue): JsResult[Links] = ???
  }

  /**
   * Play-JSON format for serializing and deserializing Jsonapi [[Included]].
   */
  implicit lazy val includedFormat: Format[Included] = new Format[Included] {
    override def writes(included: Included): JsValue = {
      val objects = included.resourceObjects.array.map(resourceObjectFormat writes _)
      JsArray(objects)
    }

    override def reads(json: JsValue): JsResult[Included] = ???
  }
}
