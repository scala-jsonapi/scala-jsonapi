package org.zalando.jsonapi.model.implicits

import scala.language.implicitConversions

import org.zalando.jsonapi.model.JsonApiObject._

object JsonApiObjectValueConversions {
  implicit def convertStringToStringValue(string: String) = StringValue(string)
  implicit def convertIntToNumberValue(int: Int) = NumberValue(int)
  implicit def convertLongToNumberValue(long: Long) = NumberValue(long)
  implicit def convertDoubleToNumberValue(double: Double) = NumberValue(double)
  implicit def convertFloatToNumberValue(float: Float) = NumberValue(float)
  implicit def convertBooleanToBooleanValue(boolean: Boolean) = BooleanValue(boolean)
}
