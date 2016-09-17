package org.zalando.jsonapi.model.implicits

case class UnconvertibleTypeError(msg: String) extends Exception(msg)

object UnconvertibleTypeError {
  def apply(any: Any): UnconvertibleTypeError = {
    UnconvertibleTypeError(s"Can not convert ${any.getClass.getName} to JsonApiObject Value")
  }
}
