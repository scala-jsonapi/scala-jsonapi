package org.zalando.jsonapi.model.implicits

import org.zalando.jsonapi.model.Attribute
import org.zalando.jsonapi.model.JsonApiObject._

import scala.language.implicitConversions

object JsonApiObjectValueConversions {
  implicit def convertAnyToValue(any: Any): Value = {
    any match {
      case(string: String) ⇒
        StringValue(string)
      case(int: Int) ⇒
        NumberValue(int)
      case(long: Long) ⇒
        NumberValue(long)
      case(double: Double) ⇒
        NumberValue(double)
      case(float: Float) ⇒
        NumberValue(float)
      case true ⇒
        TrueValue
      case false ⇒
        FalseValue
      case null ⇒
        NullValue
      case value: Value ⇒
        value
      case(map: Map[_,_]) ⇒
        JsObjectValue(
          map.map {
            case(name: String, value) ⇒
              Attribute(name, convertAnyToValue(value))
            case _ ⇒
              throw UnconvertibleTypeError("Maps must have string keys to be converted to JsonApiObject Values")
          }.toList
        )
      case(seq: Seq[_]) ⇒
        JsArrayValue(seq.map(convertAnyToValue))
      case _ ⇒
        throw UnconvertibleTypeError(any)
   }
  }
}
