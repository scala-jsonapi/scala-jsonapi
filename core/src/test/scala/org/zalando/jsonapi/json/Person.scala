package org.zalando.jsonapi.json

import org.zalando.jsonapi.JsonapiRootObjectWriter
import org.zalando.jsonapi.model.JsonApiObject.StringValue
import org.zalando.jsonapi.model.{ Attribute, RootObject }
import org.zalando.jsonapi.model.RootObject.ResourceObject

/**
 * Class Person is used during testing
 * @param id
 * @param name
 */
case class Person(id: Int, name: String)

object Person {
  implicit val personJsonapiRootObjectWriter: JsonapiRootObjectWriter[Person] = new JsonapiRootObjectWriter[Person] {
    override def toJsonapi(person: Person) = {
      RootObject(data = Some(ResourceObject(
        `type` = "person",
        id = Some(person.id.toString),
        attributes = Some(List(
          Attribute("name", StringValue(person.name))
        )), links = None)))
    }
  }
}