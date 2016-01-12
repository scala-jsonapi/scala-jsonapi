package org.zalando.jsonapi.json.sprayjson

import SprayJsonReadSupport._
import org.zalando.jsonapi.json._
import org.zalando.jsonapi.model.RootObject.{ Data, ResourceObject, ResourceObjects }
import org.zalando.jsonapi.model._
import spray.json._
import scala.language.postfixOps

trait SprayJsonJsonapiFormat {
  self: DefaultJsonProtocol ⇒

  /**
   * Spray-JSON format for serializing and deserializing Jsonapi [[RootObject]].
   */
  implicit lazy val rootObjectFormat: RootJsonFormat[RootObject] = new RootJsonFormat[RootObject] {
    override def write(rootObject: RootObject): JsValue = {
      val data = rootObject.data map (FieldNames.`data` -> _.toJson)
      val links = rootObject.links map (FieldNames.`links` -> _.toJson)
      val meta = rootObject.meta map (FieldNames.`meta` -> _.toJson)
      val errors = rootObject.errors map (FieldNames.`errors` -> _.toJson)
      val included = rootObject.included map (FieldNames.`included` -> _.toJson)
      val jsonApi = rootObject.jsonApi map (FieldNames.`jsonapi` -> _.toJson)
      JsObject(collectSome(data, links, meta, errors, included, jsonApi) toMap)
    }

    override def read(json: JsValue): RootObject = {
      val obj = json.asJsObject
      val data = (obj \? FieldNames.`data`) map (_.convertTo[Data])
      val links = (obj \? FieldNames.`links`) map (_.convertTo[Links])
      val meta = (obj \? FieldNames.`meta`) map (_.convertTo[Meta])
      val errors = (obj \? FieldNames.`errors`) map (_.convertTo[Errors])
      val included = (obj \? FieldNames.`included`) map (_.convertTo[Included])
      val jsonapi = (obj \? FieldNames.`jsonapi`) map (_.convertTo[JsonApi])
      RootObject(data, links, errors, meta, included, jsonapi)
    }
  }

  /**
   * Spray-JSON format for serializing and deserializing Jsonapi [[Data]].
   */
  implicit lazy val dataFormat: RootJsonFormat[Data] = new RootJsonFormat[Data] {
    override def write(data: Data): JsValue = {
      data match {
        case ro: ResourceObject ⇒ ro.toJson
        case ResourceObjects(resourceObjects) ⇒
          val objects = resourceObjects map (ro ⇒ ro.toJson)
          JsArray(objects.toVector)
      }
    }

    override def read(json: JsValue): Data = {
      json match {
        case obj: JsObject ⇒ obj.convertTo[ResourceObject]
        case arr: JsArray  ⇒ ResourceObjects(arr.convertTo[List[ResourceObject]])
        case _             ⇒ throwDesEx(s"Unable to serialize Data type from json: $json")
      }
    }
  }

  /**
   * Spray-JSON format for serializing and deserializing Jsonapi [[ResourceObject]].
   */
  implicit lazy val resourceObjectFormat: RootJsonFormat[ResourceObject] = new RootJsonFormat[ResourceObject] {
    override def write(resourceObject: ResourceObject): JsValue = {
      val `type` = Some(FieldNames.`type` -> resourceObject.`type`.toJson)
      val id = resourceObject.id map (FieldNames.`id` -> _.toJson)
      val attributes = resourceObject.attributes map (FieldNames.`attributes` -> _.toJson)
      val links = resourceObject.links map (FieldNames.`links` -> _.toJson)
      val meta = resourceObject.meta map (FieldNames.`meta` -> _.toJson)
      val relationships = resourceObject.relationships map (FieldNames.`relationships` -> _.toJson)
      JsObject(collectSome(`type`, id, attributes, relationships, links, meta) toMap)
    }

    override def read(json: JsValue): ResourceObject = {
      val obj = json.asJsObject
      val `type` = (obj \ FieldNames.`type`).asString
      val id = (obj \? FieldNames.`id`) map (_.asString)
      val attributes = (obj \? FieldNames.`attributes`) map (_.convertTo[Attributes])
      val links = (obj \? FieldNames.`links`) map (_.convertTo[Links])
      val meta = (obj \? FieldNames.`meta`) map (_.convertTo[Meta])
      val relationships = (obj \? FieldNames.`relationships`) map (_.convertTo[Relationships])
      ResourceObject(`type`, id, attributes, relationships, links, meta)
    }
  }

  /**
   * Spray-JSON format for serializing and deserializing Jsonapi [[Attributes]].
   */
  implicit lazy val attributesFormat: RootJsonFormat[Attributes] = new RootJsonFormat[Attributes] {
    override def write(attributes: Attributes): JsValue = {
      val fields = attributes map (p ⇒ p.name -> p.value.toJson)
      JsObject(fields: _*)
    }

    override def read(json: JsValue): Attributes = {
      json.asJsObject.fields map { case (name, value) ⇒ Attribute(name, value.convertTo[JsonApiObject.Value]) } toList
    }
  }

  /**
   * Spray-JSON format for serializing and deserializing Jsonapi [[Meta]].
   */
  implicit lazy val metaFormat: RootJsonFormat[Meta] = new RootJsonFormat[Meta] {
    override def write(meta: Meta): JsValue = {
      val fields = meta map (m ⇒ m.name -> m.value.toJson)
      JsObject(fields: _*)
    }

    override def read(json: JsValue): Meta = {
      json.asJsObject.fields map { case (name, value) ⇒ MetaProperty(name, value.convertTo[JsonApiObject.Value]) } toList
    }
  }

  /**
   * Spray-JSON format for serializing and deserializing Jsonapi [[JsonApi]].
   */
  implicit lazy val jsonApiFormat: RootJsonFormat[JsonApi] = new RootJsonFormat[JsonApi] {
    override def write(jsonApi: JsonApi): JsValue = {
      val fields = jsonApi map (jap ⇒ jap.name -> jap.value.toJson)
      JsObject(fields: _*)
    }

    override def read(json: JsValue): JsonApi = {
      json.asJsObject.fields map (jap ⇒ JsonApiProperty(jap._1, jap._2.convertTo[JsonApiObject.Value])) toList
    }
  }

  /**
   * Spray-JSON format for serializing and deserializing Jsonapi [[Errors]].
   */
  implicit lazy val errorsFormat: RootJsonFormat[Errors] = new RootJsonFormat[Errors] {
    override def write(errors: Errors): JsValue = {
      val objects = errors map (e ⇒ e.toJson)
      JsArray(objects.toVector)
    }

    override def read(json: JsValue): Errors = {
      json.convertTo[List[Error]]
    }
  }

  /**
   * Spray-JSON format for serializing and deserializing Jsonapi [[Error]].
   */
  implicit lazy val errorFormat: RootJsonFormat[Error] = new RootJsonFormat[Error] {
    override def write(error: Error): JsObject = {
      val id = error.id map (FieldNames.`id` -> _.toJson)
      val status = error.status map (FieldNames.`status` -> _.toJson)
      val code = error.code map (FieldNames.`code` -> _.toJson)
      val title = error.title map (FieldNames.`title` -> _.toJson)
      val detail = error.detail map (FieldNames.`detail` -> _.toJson)
      val links = error.links map (FieldNames.`links` -> _.toJson)
      val meta = error.meta map (FieldNames.`meta` -> _.toJson)
      val source = error.source map (FieldNames.`source` -> _.toJson)
      JsObject(collectSome(id, status, code, title, detail, links, meta, source) toMap)
    }

    override def read(json: JsValue): Error = {
      val obj = json.asJsObject
      val id = (obj \? FieldNames.`id`) map (_.asString)
      val status = (obj \? FieldNames.`status`) map (_.asString)
      val code = (obj \? FieldNames.`code`) map (_.asString)
      val title = (obj \? FieldNames.`title`) map (_.asString)
      val detail = (obj \? FieldNames.`detail`) map (_.asString)
      val links = (obj \? FieldNames.`links`) map (_.convertTo[Links])
      val meta = (obj \? FieldNames.`meta`) map (_.convertTo[Meta])
      val source = (obj \? FieldNames.`source`) map (_.convertTo[ErrorSource])
      Error(id, links, status, code, title, detail, source, meta)
    }
  }

  /**
   * Spray-JSON format for serializing and deserializing Jsonapi [[ErrorSource]].
   */
  implicit lazy val errorSourceFormat: RootJsonFormat[ErrorSource] = new RootJsonFormat[ErrorSource] {
    override def write(errorSource: ErrorSource): JsValue = {
      val pointer = errorSource.pointer map (FieldNames.`pointer` -> _.toJson)
      val parameter = errorSource.parameter map (FieldNames.`parameter` -> _.toJson)
      JsObject(collectSome(pointer, parameter) toMap)
    }

    override def read(json: JsValue): ErrorSource = {
      val obj = json.asJsObject
      val pointer = (obj \? FieldNames.`pointer`) map (_.asString)
      val parameter = (obj \? FieldNames.`parameter`) map (_.asString)
      ErrorSource(pointer, parameter)
    }
  }

  /**
   * Spray-JSON format for serializing and deserializing Jsonapi [[Relationship]].
   */
  implicit lazy val relationshipFormat: RootJsonFormat[Relationship] = new RootJsonFormat[Relationship] {
    override def write(relationship: Relationship): JsValue = {
      val links = relationship.links map (FieldNames.`links` -> _.toJson)
      val data = relationship.data map (FieldNames.`data` -> _.toJson)
      JsObject(collectSome(links, data) toMap)
    }

    override def read(json: JsValue): Relationship = {
      val obj = json.asJsObject
      val links = (obj \? FieldNames.`links`) map (_.convertTo[Links])
      val data = (obj \? FieldNames.`data`) map (_.convertTo[Data])
      Relationship(links, data)
    }
  }

  /**
   * Spray-JSON format for serializing and deserializing Jsonapi [[JsonApiObject]].
   */
  implicit lazy val jsonApiObjectValueFormat: JsonFormat[JsonApiObject.Value] = lazyFormat(new JsonFormat[JsonApiObject.Value] {
    override def write(oValue: JsonApiObject.Value): JsValue = {
      oValue match {
        case JsonApiObject.StringValue(s)   ⇒ s.toJson
        case JsonApiObject.NumberValue(n)   ⇒ n.toJson
        case JsonApiObject.BooleanValue(b)  ⇒ b.toJson
        case JsonApiObject.JsObjectValue(o) ⇒ o.toJson
        case JsonApiObject.JsArrayValue(a)  ⇒ a.toJson
        case JsonApiObject.NullValue        ⇒ JsNull
      }
    }

    override def read(json: JsValue): JsonApiObject.Value = {
      json match {
        case JsString(s)  ⇒ JsonApiObject.StringValue(s)
        case JsNumber(n)  ⇒ JsonApiObject.NumberValue(n)
        case JsBoolean(b) ⇒ JsonApiObject.BooleanValue(b)
        case JsNull       ⇒ JsonApiObject.NullValue
        case JsArray(a)   ⇒ JsonApiObject.JsArrayValue(a map (jsValue ⇒ jsValue.convertTo[JsonApiObject.Value]) toList)
        case JsObject(o)  ⇒ JsonApiObject.JsObjectValue(o map { case (name, value) ⇒ Attribute(name, value.convertTo[JsonApiObject.Value]) } toList)
      }
    }
  })

  /**
   * Spray-JSON format for serializing and deserializing Jsonapi [[Links]].
   */
  implicit lazy val linksFormat: RootJsonFormat[Links] = new RootJsonFormat[Links] {
    override def write(links: Links): JsValue = {
      val fields = links map (l ⇒ l match {
        case Links.Self(url)    ⇒ "self" -> url.toJson
        case Links.About(url)   ⇒ "about" -> url.toJson
        case Links.First(url)   ⇒ "first" -> url.toJson
        case Links.Last(url)    ⇒ "last" -> url.toJson
        case Links.Next(url)    ⇒ "next" -> url.toJson
        case Links.Prev(url)    ⇒ "prev" -> url.toJson
        case Links.Related(url) ⇒ "related" -> url.toJson
      })
      JsObject(fields: _*)
    }

    override def read(json: JsValue): Links = {
      val obj = json.asJsObject
      val self = (obj \? FieldNames.`self`) map (url ⇒ Links.Self(url.asString))
      val about = (obj \? FieldNames.`about`) map (url ⇒ Links.About(url.asString))
      val first = (obj \? FieldNames.`first`) map (url ⇒ Links.First(url.asString))
      val last = (obj \? FieldNames.`last`) map (url ⇒ Links.Last(url.asString))
      val next = (obj \? FieldNames.`next`) map (url ⇒ Links.Next(url.asString))
      val prev = (obj \? FieldNames.`prev`) map (url ⇒ Links.Prev(url.asString))
      val related = (obj \? FieldNames.`related`) map (url ⇒ Links.Related(url.asString))
      collectSome(self, about, first, last, next, prev, related)
    }
  }

  /**
   * Spray-JSON format for serializing and deserializing Jsonapi [[Included]].
   */
  implicit lazy val includedFormat: RootJsonFormat[Included] = new RootJsonFormat[Included] {
    override def write(included: Included): JsValue = {
      val objects = included.resourceObjects.array map (_.toJson)
      JsArray(objects.toVector)
    }

    override def read(json: JsValue): Included = {
      Included(ResourceObjects(json.convertTo[List[ResourceObject]]))
    }
  }
}
