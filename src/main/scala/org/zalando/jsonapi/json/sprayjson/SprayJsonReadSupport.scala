package org.zalando.jsonapi.json.sprayjson

import spray.json._

private[json] object SprayJsonReadSupport {

  type Seq[+A] = scala.collection.immutable.Seq[A]
  val Seq = scala.collection.immutable.Seq

  implicit class RichJsObject(val obj: JsObject) extends AnyVal {
    def fieldOpt(fieldName: String): Option[JsValue] =
      obj.getFields(fieldName).toList match {
        case Seq(value) ⇒ Some(value)
        case _ ⇒ None
      }

    def field(fieldName: String): JsValue =
      obj.getFields(fieldName).toList match {
        case Seq(value) ⇒ value
        case x ⇒
          deserializationError(s"No value for fieldName $fieldName in $obj: $x")
      }

    def \(fieldName: String): JsValue = field(fieldName)

    def \?(fieldName: String): Option[JsValue] = fieldOpt(fieldName)
  }

  implicit class RichJsValue(val v: JsValue) extends AnyVal {
    def asString: String = v match {
      case JsString(s) ⇒ s
      case x ⇒ deserializationError(s"$x is not a JSON string")
    }

    def asStringSeq: Seq[String] = v match {
      case JsArray(elements) ⇒
        elements map {
          case JsString(s) ⇒ s
          case x ⇒ deserializationError(s"$x is not a JSON string")
        }
      case x ⇒
        deserializationError(s"$x is not a JSON array")
    }
  }

}
