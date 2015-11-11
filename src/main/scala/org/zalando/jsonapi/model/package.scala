package org.zalando.jsonapi

import collection.immutable.{ Seq ⇒ ImmutableSeq }

package object model {
  case class RootObject(`type`: String, id: String, data: Option[DataProperties])

  type DataProperties = ImmutableSeq[DataProperty]

  case class DataProperty(name: String, value: DataProperty.Value)

  object DataProperty {
    sealed trait Value
    case class StringValue(value: String) extends Value
    case class NumberValue(value: BigDecimal) extends Value
    case class BooleanValue(value: Boolean) extends Value
    case class JsObjectValue(value: DataProperties) extends Value
    case class JsArrayValue(value: Seq[Value]) extends Value
    case object NullValue extends Value
  }
}
