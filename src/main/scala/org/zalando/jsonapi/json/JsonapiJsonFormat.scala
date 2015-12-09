package org.zalando.jsonapi.json

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

      val data = rootObject.data match {
        case Some(d) ⇒ Some("data" -> d.toJson)
        case None    ⇒ None
      }

      val links = rootObject.links match {
        case Some(l) ⇒ Some("links" -> l.toJson)
        case None    ⇒ None
      }

      val meta = rootObject.meta match {
        case Some(m) ⇒ Some("meta" -> m.toJson)
        case None    ⇒ None
      }

      val errors = rootObject.errors match {
        case Some(e) ⇒ Some("errors" -> e.toJson)
        case None    ⇒ None
      }

      val included = rootObject.included match {
        case Some(i) ⇒ Some("included" -> i.toJson)
        case None    ⇒ None
      }

      val jsonApi = rootObject.jsonApi match {
        case Some(j) ⇒ Some("jsonapi" -> j.toJson)
        case None    ⇒ None
      }

      JsObject(
        Map()
          ++ data
          ++ links
          ++ meta
          ++ errors
          ++ included
          ++ jsonApi
      )
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

      val attributes = resourceObject.attributes.map(a ⇒ "attributes" -> a.toJson)

      val links = resourceObject.links.map(l ⇒ "links" -> l.toJson)

      val meta = resourceObject.meta.map(m ⇒ "meta" -> m.toJson)

      val relationships = resourceObject.relationships.map(r ⇒ "relationships" -> r.toJson)

      JsObject(
        (
          Seq(
            "type" -> resourceObject.`type`.toJson,
            "id" -> resourceObject.id.toJson
          ) ++ attributes
            ++ links
            ++ meta
            ++ relationships
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
   * Spray-JSON format for serializing Jsonapi [[org.zalando.jsonapi.model.Meta]].
   */
  implicit val metaWriter: RootJsonWriter[Meta] = new RootJsonWriter[Meta] {
    override def write(meta: Meta): JsValue = {
      val fields = meta map (m ⇒ m.name -> m.value.toJson)
      JsObject(fields: _*)
    }
  }

  implicit val jsonApiWriter: RootJsonWriter[JsonApi] = new RootJsonWriter[JsonApi] {
    override def write(jsonApi: JsonApi): JsValue = {
      val fields = jsonApi map (jap ⇒ jap.name -> jap.value.toJson)
      JsObject(fields: _*)
    }
  }

  implicit val errorsWriter: RootJsonWriter[Errors] = new RootJsonWriter[Errors] {
    override def write(errors: Errors): JsValue = {
      val objects = errors map (e ⇒ e.toJson)
      JsArray(objects.toVector)
    }
  }

  /**
   * Spray-JSON format for serializing Jsonapi [[org.zalando.jsonapi.model.Meta]].
   */
  implicit val errorWriter: RootJsonWriter[Error] = new RootJsonWriter[Error] {
    override def write(error: Error): JsObject = {

      val links = error.links.map(l ⇒ "links" -> l.toJson)

      val meta = error.meta.map(m ⇒ "meta" -> m.toJson)

      val source = error.source.map(s ⇒ "source" -> s.toJson)

      JsObject(Map(
        "id" -> error.id.getOrElse("").toJson,
        "status" -> error.status.getOrElse("").toJson,
        "code" -> error.code.getOrElse("").toJson,
        "title" -> error.title.getOrElse("").toJson,
        "detail" -> error.detail.getOrElse("").toJson)
        ++ source
        ++ links
        ++ meta
      )
    }
  }

  implicit val errorSourceWriter: RootJsonWriter[ErrorSource] = new RootJsonWriter[ErrorSource] {
    override def write(errorSource: ErrorSource): JsValue = {
      JsObject(Map(
        "pointer" -> errorSource.pointer.toJson,
        "parameter" -> errorSource.parameter.toJson
      ))
    }
  }

  implicit val relationshipsWriter: RootJsonWriter[Relationships] = new RootJsonWriter[Relationships] {
    override def write(relationships: Relationships): JsValue = {
      JsObject(relationships map { relationship ⇒
        relationship._1 -> relationship._2.toJson
      })
    }
  }

  implicit val relationshipWriter: RootJsonWriter[Relationship] = new RootJsonWriter[Relationship] {
    override def write(relationship: Relationship): JsValue = {

      val links = relationship.links.map(l ⇒ "links" -> l.toJson)

      val data = relationship.data.map(d ⇒ "data" -> d.toJson)

      JsObject(
        Map()
          ++ links
          ++ data
      )
    }
  }

  /**
   * Spray-JSON format for serializing [[org.zalando.jsonapi.model.JsonApiObject]] to Jsonapi.
   */
  implicit val jsonApiObjectValueWriter: JsonFormat[JsonApiObject.Value] = lazyFormat(new JsonFormat[JsonApiObject.Value] {
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

    // We don't need it for now
    override def read(json: JsValue): JsonApiObject.Value = ???
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

  implicit val includedWriter: RootJsonWriter[Included] = new RootJsonWriter[Included] {
    override def write(included: Included): JsValue = {
      val objects = included.resourceObjects.array map (i ⇒ i.toJson)
      JsArray(objects.toVector)
    }
  }
}
