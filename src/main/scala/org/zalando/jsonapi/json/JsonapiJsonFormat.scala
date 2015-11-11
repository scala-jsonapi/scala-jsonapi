package org.zalando.jsonapi.json

import org.zalando.jsonapi.model.Attribute._
import org.zalando.jsonapi.model._
import spray.json._

trait JsonapiJsonFormat {
  self: DefaultJsonProtocol ⇒

  implicit val rootObjectWriter: RootJsonWriter[RootObject] = new RootJsonWriter[RootObject] {
    override def write(rootObject: RootObject): JsValue = {
      JsObject(Map("data" -> rootObject.data.toJson))
    }
  }

  implicit val dataObjectWriter: RootJsonWriter[Data] = new RootJsonWriter[Data] {
    override def write(data: Data): JsValue = {
      data.attributes match {
        case Some(attrs) ⇒ JsObject("type" -> data.`type`.toJson, "id" -> data.id.toJson, "attributes" -> attrs.toJson)
        case None        ⇒ JsObject("type" -> data.`type`.toJson, "id" -> data.id.toJson)
      }
    }
  }

  implicit val dataPropertiesWriter: RootJsonWriter[Attributes] = new RootJsonWriter[Attributes] {
    override def write(attributes: Attributes): JsValue = {
      val fields = attributes map (p ⇒ p.name -> p.value.toJson)
      JsObject(fields: _*)
    }
  }

  implicit val dataPropertyValueWriter: JsonFormat[Attribute.Value] = lazyFormat(new JsonFormat[Attribute.Value] {
    override def write(dataPropertyValue: Attribute.Value): JsValue = {
      dataPropertyValue match {
        case Attribute.StringValue(s)   ⇒ s.toJson
        case Attribute.NumberValue(n)   ⇒ n.toJson
        case Attribute.BooleanValue(b)  ⇒ b.toJson
        case Attribute.JsObjectValue(o) ⇒ o.toJson
        case Attribute.JsArrayValue(a)  ⇒ a.toJson
        case Attribute.NullValue        ⇒ JsNull
      }
    }

    // We don't need it for now
    override def read(json: JsValue): Value = ???
  })
}
