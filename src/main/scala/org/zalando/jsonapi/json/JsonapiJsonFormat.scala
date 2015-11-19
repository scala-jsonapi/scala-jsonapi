package org.zalando.jsonapi.json

import org.zalando.jsonapi.model.Attribute._
import org.zalando.jsonapi.model.RootObject._
import org.zalando.jsonapi.model._
import spray.json._

trait JsonapiJsonFormat {
  self: DefaultJsonProtocol ⇒

  /**
   * Spray-JSON format for serializing [[org.zalando.jsonapi.model.RootObject]] to Jsonapi.
   */
  implicit val rootObjectWriter: RootJsonWriter[RootObject] = new RootJsonWriter[RootObject] {
    override def write(rootObject: RootObject): JsValue = {

      rootObject.links match {
        case Some(links) ⇒ JsObject(Map("data" -> rootObject.data.toJson, "links" -> links.toJson))
        case None        ⇒ JsObject(Map("data" -> rootObject.data.toJson))
      }

      // JsObject(Map("data" -> rootObject.data.toJson, "links" -> rootObject.links.toJson))
    }
  }

  implicit val dataWriter: RootJsonWriter[Data] = new RootJsonWriter[Data] {
    override def write(data: Data): JsValue = {
      data match {
        case ro: ResourceObject            ⇒ ro.toJson
        case rio: ResourceIdentifierObject ⇒ rio.toJson
        case ResourceObjects(resourceObjects) ⇒
          val objects = resourceObjects map (ro ⇒ ro.toJson)
          JsArray(objects.toVector)
        case ResourceIdentifierObjects(resourceIdentifierObjects) ⇒
          val objects = resourceIdentifierObjects map (rio ⇒ rio.toJson)
          JsArray(objects.toVector)
      }
    }
  }

  implicit val resourceObjectWriter: RootJsonWriter[ResourceObject] = new RootJsonWriter[ResourceObject] {
    override def write(resourceObject: ResourceObject): JsValue = {

      val attributes = resourceObject.attributes match {
        case Some(a) ⇒ Some("attributes" -> a.toJson)
        case None    ⇒ None
      }

      val links = resourceObject.links match {
        case Some(l) ⇒ Some("links" -> l.toJson)
        case None    ⇒ None
      }

      JsObject(
        (
          Seq(
            "type" -> resourceObject.`type`.toJson,
            "id" -> resourceObject.id.toJson
          ) ++ attributes
            ++ links
        ).toMap
      )
    }
  }

  implicit val resourceIdentifierObjectWriter: RootJsonWriter[ResourceIdentifierObject] = new RootJsonWriter[ResourceIdentifierObject] {
    override def write(resourceIdentifierObject: ResourceIdentifierObject): JsValue = {
      JsObject(Map(
        "type" -> resourceIdentifierObject.`type`.toJson,
        "id" -> resourceIdentifierObject.id.toJson
      ))
    }
  }

  /**
   * Spray-JSON format for serializing Jsonapi [[org.zalando.jsonapi.model.Attributes]].
   */
  implicit val attributesWriter: RootJsonWriter[Attributes] = new RootJsonWriter[Attributes] {
    override def write(attributes: Attributes): JsValue = {
      val fields = attributes map (p ⇒ p.name -> p.value.toJson)
      JsObject(fields: _*)
    }
  }

  /**
   * Spray-JSON format for serializing [[org.zalando.jsonapi.model.Attribute]] to Jsonapi.
   */
  implicit val attributeValueWriter: JsonFormat[Attribute.Value] = lazyFormat(new JsonFormat[Attribute.Value] {
    override def write(attributeValue: Attribute.Value): JsValue = {
      attributeValue match {
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

  /**
   * Spray-JSON format for serializing Jsonapi [[org.zalando.jsonapi.model.Links]].
   */
  implicit val linksWriter: RootJsonWriter[Links] = new RootJsonWriter[Links] {
    override def write(links: Links): JsValue = {
      val fields = links map (l ⇒
        l.linkOption match {
          case Link.Self(url)    ⇒ "self" -> url.toJson
          case Link.About(url)   ⇒ "about" -> url.toJson
          case Link.First(url)   ⇒ "first" -> url.toJson
          case Link.Last(url)    ⇒ "last" -> url.toJson
          case Link.Next(url)    ⇒ "next" -> url.toJson
          case Link.Prev(url)    ⇒ "prev" -> url.toJson
          case Link.Related(url) ⇒ "related" -> url.toJson
        }
      )
      JsObject(fields: _*)
    }
  }
}
