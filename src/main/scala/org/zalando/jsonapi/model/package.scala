package org.zalando.jsonapi

import org.zalando.jsonapi.model.RootObject.ResourceObjects

import scala.collection.immutable.{ Seq ⇒ ImmutableSeq }

/**
 * The model package, containing partially covered Jsonapi specification.
 */
package object model {

  /**
   * A root, top-level object.
   */
  case class RootObject(data: Option[RootObject.Data], links: Option[Links] = None, errors: Option[Errors] = None, meta: Option[Meta] = None, included: Option[Included] = None, jsonApi: Option[JsonApi] = None)

  object RootObject {
    sealed trait Data

    case class ResourceObject(`type`: String, id: String, attributes: Option[Attributes] = None, relationships: Option[Relationships] = None, links: Option[Links] = None, meta: Option[Meta] = None) extends Data
    case class ResourceIdentifierObject(`type`: String, id: String) extends Data
    case class ResourceObjects(array: ImmutableSeq[ResourceObject]) extends Data
    case class ResourceIdentifierObjects(array: ImmutableSeq[ResourceIdentifierObject]) extends Data
  }

  /**
   * A collection of [[Link]] objects.
   */
  type Links = ImmutableSeq[Link]

  /**
   * An object representing a Link
   * @param linkOption the kind of link to be addded with its URL
   */
  case class Link(linkOption: Link.LinkOption)

  object Link {
    sealed trait LinkOption

    /**
     * A Link of the "self" type.
     * @param url The url to link to.
     */
    case class Self(url: String) extends LinkOption

    /**
     * A Link of the "related" type.
     * @param url The url to link to.
     */
    case class Related(url: String) extends LinkOption

    /**
     * A Link of the "first" type.
     * @param url The url to link to.
     */
    case class First(url: String) extends LinkOption

    /**
     * A Link of the "last" type.
     * @param url The url to link to.
     */
    case class Last(url: String) extends LinkOption

    /**
     * A Link of the "next" type.
     * @param url The url to link to.
     */
    case class Next(url: String) extends LinkOption

    /**
     * A Link of the "prev" type.
     * @param url The url to link to.
     */
    case class Prev(url: String) extends LinkOption

    /**
     * A Link of the "about" type.
     * @param url The url to link to.
     */
    case class About(url: String) extends LinkOption
  }

  type Attributes = ImmutableSeq[Attribute]

  /**
   * The representation of an attribute of the root object.
   * @param name the name of the attribute
   * @param value the value of the attribute
   */
  case class Attribute(name: String, value: JsonApiObject.Value)

  /**
   * A collection of [[Error]] objects.
   */
  type Errors = ImmutableSeq[Error]

  type Meta = ImmutableSeq[MetaProperty]

  case class MetaProperty(name: String, value: JsonApiObject.Value)

  case class Error(id: Option[String] = None, links: Option[Links] = None, status: Option[String] = None, code: Option[String] = None, title: Option[String] = None, detail: Option[String] = None, source: Option[ErrorSource] = None, meta: Option[Meta] = None)

  case class ErrorSource(pointer: Option[String] = None, parameter: Option[String] = None)

  case class Included(resourceObjects: ResourceObjects)

  case class JsonApi(name: String, value: JsonApiObject.Value)

  object JsonApiObject {

    sealed trait Value;

    /**
     * An attribute value that is string-typed.
     * @param value the string value
     */
    case class StringValue(value: String) extends Value

    /**
     * An attribute value that is number-typed.
     * @param value the number value
     */
    case class NumberValue(value: BigDecimal) extends Value

    /**
     * An attribute value that is boolean-typed.
     * @param value the boolean value
     */
    case class BooleanValue(value: Boolean) extends Value

    /**
     * An attribute value that is list(key, value)-typed.
     * @param value the list of key-value pairs
     */
    case class JsObjectValue(value: Attributes) extends Value

    /**
     * An attribute value that is array-typed.
     * @param value the array value
     */
    case class JsArrayValue(value: Seq[Value]) extends Value

    /**
     * An attribute value that is null.
     */
    case object NullValue extends Value

  }

  type Relationships = ImmutableSeq[Relationship]

  case class Relationship(name: String, links: Option[Links], data: Option[ImmutableSeq[RootObject.ResourceIdentifierObject]])

}
