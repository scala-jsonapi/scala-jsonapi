package org.zalando.jsonapi.json.playjson

import org.zalando.jsonapi.json.FieldNames
import org.zalando.jsonapi.model.JsonApiObject.Value
import org.zalando.jsonapi.model._
import org.zalando.jsonapi.model.RootObject._
import play.api.libs.json._
import play.api.libs.functional.syntax._

import collection.immutable.{Seq ⇒ ImmutableSeq}

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

    override def reads(json: JsValue): JsResult[Data] = {
      json.asOpt[ResourceObject] match {
        case Some(ro) ⇒ JsSuccess(ro)
        case None ⇒
          JsSuccess(ResourceObjects(json.as[ImmutableSeq[ResourceObject]]))
      }
    }
  }

  /**
    * Play-JSON format for serializing and deserializing Jsonapi [[ResourceObject]].
    */
  implicit lazy val resourceObjectFormat: Format[ResourceObject] = (
      (JsPath \ FieldNames.`type`).format[String] and
        (JsPath \ FieldNames.`id`).formatNullable[String] and
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
          case (acc, (name, jsValue)) ⇒
            (acc, jsValue.validate[JsonApiObject.Value]) match {
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
    * Play-JSON format for serializing and deserializing Jsonapi [[JsonApi]].
    */
  implicit lazy val jsonApiFormat: Format[JsonApi] = new Format[JsonApi] {
    override def writes(jsonApi: JsonApi): JsValue = {
      val fields = jsonApi.map(jap ⇒ (jap.name, Json.toJson(jap.value)))
      JsObject(fields)
    }

    override def reads(json: JsValue): JsResult[JsonApi] = json match {
      case JsObject(fields) ⇒
        fields.foldLeft[JsResult[JsonApi]](JsSuccess(Vector.empty)) {
          case (acc, (name, jsValue)) ⇒
            (acc, jsValue.validate[JsonApiObject.Value]) match {
              case (JsSuccess(jsonApiProps, _), JsSuccess(value, _)) ⇒
                JsSuccess(jsonApiProps :+ JsonApiProperty(name, value))
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
    * Play-JSON format for serializing and deserializing Jsonapi [[Errors]].
    */
  implicit lazy val errorsFormat: Format[Errors] = new Format[Errors] {
    override def writes(errors: Errors): JsValue = {
      val fields = errors.map(Json.toJson(_))
      JsArray(fields)
    }

    override def reads(json: JsValue): JsResult[Errors] = json match {
      case JsArray(a) ⇒
        a.foldLeft[JsResult[Errors]](JsSuccess(Vector.empty)) {
          case (acc, jsValue) ⇒
            (acc, jsValue.validate[Error]) match {
              case (JsSuccess(errs, _), JsSuccess(value, _)) ⇒
                JsSuccess(errs :+ value)
              case (JsSuccess(_, _), JsError(errors)) ⇒
                JsError(errors)
              case (e: JsError, s: JsSuccess[_]) ⇒
                e
              case (e: JsError, JsError(errors)) ⇒
                e ++ JsError(errors)
            }
        }
      case _ ⇒ JsError("error.expected.jsarray")
    }
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
      case JsonApiObject.StringValue(s) ⇒ JsString(s)
      case JsonApiObject.NumberValue(n) ⇒ JsNumber(n)
      case JsonApiObject.BooleanValue(b) ⇒ JsBoolean(b)
      case JsonApiObject.JsObjectValue(o) ⇒
        JsObject(o.map(a ⇒ a.name -> Json.toJson(a.value)))
      case JsonApiObject.JsArrayValue(a) ⇒
        JsArray(a.map(v ⇒ Json.toJson[JsonApiObject.Value](v)))
      case JsonApiObject.NullValue ⇒ JsNull
    }

    override def reads(json: JsValue): JsResult[Value] = json match {
      case JsString(s) ⇒ JsSuccess(JsonApiObject.StringValue(s))
      case JsNumber(n) ⇒ JsSuccess(JsonApiObject.NumberValue(n))
      case JsBoolean(b) ⇒ JsSuccess(JsonApiObject.BooleanValue(b))
      case JsObject(o) ⇒ {
        val attrs = o
          .map(keyValue ⇒ {
            val (key, jsValue) = keyValue
            val read = reads(jsValue).getOrElse(JsonApiObject.NullValue)
            Attribute(key, read)
          })
          .toList
        JsSuccess(JsonApiObject.JsObjectValue(attrs))
      }
      case JsArray(a) ⇒ {
        val arrayValues = a
          .map(jsValue ⇒ {
            reads(jsValue).getOrElse(JsonApiObject.NullValue)
          })
          .toList
        JsSuccess(JsonApiObject.JsArrayValue(arrayValues))
      }
      case JsNull ⇒ JsSuccess(JsonApiObject.NullValue)
      case _ ⇒ JsError("error.expected.jsonapivalue")
    }
  }

  /**
    * Play-JSON format for serializing and deserializing Jsonapi [[Links]].
    */
  implicit lazy val linksFormat: Format[Links] = new Format[Links] {
    override def writes(links: Links): JsValue = {
      val fields = links.map {
        _ match {
          case Links.About(u, None) ⇒ (FieldNames.`about`, JsString(u))
          case Links.About(u, Some(meta)) ⇒ linkValuesToJson(FieldNames.`about`, u, meta)

          case Links.First(u, None) ⇒ (FieldNames.`first`, JsString(u))
          case Links.First(u, Some(meta)) ⇒ linkValuesToJson(FieldNames.`first`, u, meta)

          case Links.Last(u, None) ⇒ (FieldNames.`last`, JsString(u))
          case Links.Last(u, Some(meta)) ⇒ linkValuesToJson(FieldNames.`last`, u, meta)

          case Links.Next(u, None) ⇒ (FieldNames.`next`, JsString(u))
          case Links.Next(u, Some(meta)) ⇒ linkValuesToJson(FieldNames.`next`, u, meta)

          case Links.Prev(u, None) ⇒ (FieldNames.`prev`, JsString(u))
          case Links.Prev(u, Some(meta)) ⇒ linkValuesToJson(FieldNames.`prev`, u, meta)

          case Links.Related(u, None) ⇒ (FieldNames.`related`, JsString(u))
          case Links.Related(u, Some(meta)) ⇒ linkValuesToJson(FieldNames.`related`, u, meta)

          case Links.Self(u, None) ⇒ (FieldNames.`self`, JsString(u))
          case Links.Self(u, Some(meta)) ⇒ linkValuesToJson(FieldNames.`self`, u, meta)
        }
      }
      JsObject(fields)
    }

    def linkValuesToJson(name: String, href: String, meta: Meta): (String, JsValue) = {
      (name, JsObject(
        Seq(
          ("href", JsString(href)),
          ("meta", Json.toJson(meta))
        )
      ))
    }

    def jsonToLinkValues(linkObjectJson: Seq[(String, JsValue)]): (String, Option[Meta]) = {
      (linkObjectJson.find(_._1 == "href"), linkObjectJson.find(_._1 == "meta")) match {
        case(Some(hrefJson), Some(metaJson)) =>
          val href = hrefJson match {
            case ("href", JsString(hrefStr)) => hrefStr
          }
          val meta: Map[String, JsonApiObject.Value] = metaJson match {
            case ("meta", JsObject(metaObjectJson)) =>
              metaObjectJson.map {
                case (name, value) =>
                  (name, value.as[JsonApiObject.Value])

              }.toMap
          }
          (href, Some(meta))
      }
    }

    override def reads(json: JsValue): JsResult[Links] = json match {
      case JsObject(o) ⇒
        JsSuccess(o.map { keyValue ⇒
          keyValue match {
            case (FieldNames.`about`, JsString(u)) ⇒ Links.About(u, None)
            case (FieldNames.`about`, JsObject(linkObjectJson)) =>
              val linkValues = jsonToLinkValues(linkObjectJson)
              Links.About(linkValues._1, linkValues._2)

            case (FieldNames.`first`, JsString(u)) ⇒ Links.First(u, None)
            case (FieldNames.`first`, JsObject(linkObjectJson)) =>
              val linkValues = jsonToLinkValues(linkObjectJson)
              Links.First(linkValues._1, linkValues._2)

            case (FieldNames.`last`, JsString(u)) ⇒ Links.Last(u, None)
            case (FieldNames.`last`, JsObject(linkObjectJson)) =>
              val linkValues = jsonToLinkValues(linkObjectJson)
              Links.Last(linkValues._1, linkValues._2)

            case (FieldNames.`next`, JsString(u)) ⇒ Links.Next(u, None)
            case (FieldNames.`next`, JsObject(linkObjectJson)) =>
              val linkValues = jsonToLinkValues(linkObjectJson)
              Links.Next(linkValues._1, linkValues._2)

            case (FieldNames.`prev`, JsString(u)) ⇒ Links.Prev(u, None)
            case (FieldNames.`prev`, JsObject(linkObjectJson)) =>
              val linkValues = jsonToLinkValues(linkObjectJson)
              Links.Prev(linkValues._1, linkValues._2)

            case (FieldNames.`related`, JsString(u)) ⇒ Links.Related(u, None)
            case (FieldNames.`related`, JsObject(linkObjectJson)) =>
              val linkValues = jsonToLinkValues(linkObjectJson)
              Links.Related(linkValues._1, linkValues._2)

            case (FieldNames.`self`, JsString(u)) ⇒ Links.Self(u, None)
            case (FieldNames.`self`, JsObject(linkObjectJson)) =>
              val linkValues = jsonToLinkValues(linkObjectJson)
              Links.Self(linkValues._1, linkValues._2)
          }
        }.toVector)
      case _ ⇒ JsError("error.expected.links")
    }
  }

  /**
    * Play-JSON format for serializing and deserializing Jsonapi [[Included]].
    */
  implicit lazy val includedFormat: Format[Included] = new Format[Included] {
    override def writes(included: Included): JsValue = {
      val objects = included.resourceObjects.array.map(resourceObjectFormat writes _)
      JsArray(objects)
    }

    override def reads(json: JsValue): JsResult[Included] =
      JsSuccess(Included(ResourceObjects(json.as[ImmutableSeq[ResourceObject]])))
  }
}
