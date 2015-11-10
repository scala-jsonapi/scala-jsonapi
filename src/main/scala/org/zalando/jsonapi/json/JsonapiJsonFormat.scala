package org.zalando.jsonapi.json

import org.zalando.jsonapi.model.DataProperty.Value
import org.zalando.jsonapi.model._
import spray.json._

trait JsonapiJsonFormat {
  self: DefaultJsonProtocol ⇒

  implicit val rootObjectWriter: RootJsonWriter[RootObject] = new RootJsonWriter[RootObject] {
    override def write(rootObject: RootObject): JsValue = {
      rootObject.data match {
        case Some(data) ⇒ JsObject(Map("data" -> data.toJson))
        case None       ⇒ JsObject()
      }
    }
  }

  implicit val dataPropertiesWriter: RootJsonWriter[DataProperties] = new RootJsonWriter[DataProperties] {
    override def write(dataProperties: DataProperties): JsValue = {
      val fields = dataProperties map (p ⇒ p.name -> p.value.toJson)
      JsObject(fields: _*)
    }
  }

  implicit val dataPropertyValueWriter: JsonFormat[DataProperty.Value] = lazyFormat(new JsonFormat[DataProperty.Value] {
    override def write(dataPropertyValue: DataProperty.Value): JsValue = {
      dataPropertyValue match {
        case DataProperty.StringValue(s)   ⇒ s.toJson
        case DataProperty.NumberValue(n)   ⇒ n.toJson
        case DataProperty.BooleanValue(b)  ⇒ b.toJson
        case DataProperty.JsObjectValue(o) ⇒ o.toMap.toJson
        case DataProperty.JsArrayValue(a)  ⇒ a.toJson
        case DataProperty.NullValue        ⇒ JsNull
      }
    }

    // We don't need it for now
    override def read(json: JsValue): Value = ???
  })
}
