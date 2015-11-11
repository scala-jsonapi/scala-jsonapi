package org.zalando.jsonapi

import collection.immutable.{ Seq ⇒ ImmutableSeq }

package object model {
  case class RootObject(data: Data)

  case class Data(`type`: String, id: String, attributes: Option[Attributes])

  type Attributes = ImmutableSeq[Attribute]

  case class Attribute(name: String, value: Attribute.Value)

  object Attribute {
    sealed trait Value
    case class StringValue(value: String) extends Value
    case class NumberValue(value: BigDecimal) extends Value
    case class BooleanValue(value: Boolean) extends Value
    case class JsObjectValue(value: Attributes) extends Value
    case class JsArrayValue(value: Seq[Value]) extends Value
    case object NullValue extends Value
  }
}
