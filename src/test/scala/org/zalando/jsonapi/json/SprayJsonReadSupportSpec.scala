package org.zalando.jsonapi.json

import org.scalatest.{ MustMatchers, WordSpec }
import org.zalando.jsonapi.json.SprayJsonReadSupport._
import spray.json._

class SprayJsonReadSupportSpec extends WordSpec with MustMatchers {
  "SprayJsonReadSupport" must {
    "read existing JSON field" in {
      val json = JsObject("foo" -> JsString("bar"))
      (json \? "foo") mustEqual Some(JsString("bar"))
    }
    "read non-existing JSON field as None" in {
      val json = JsObject()
      (json \? "foo") mustEqual None
    }
    "read non-existing JSON field where expected" in {
      val json = JsObject()
      intercept[DeserializationException] {
        (json \ "foo")
      }
    }
    "read an existing JSON string field" in {
      val json = JsObject("foo" -> JsString("bar"))
      (json \ "foo").asString mustEqual "bar"
    }
    "read an existing JSON array of strings" in {
      val json = JsObject("foo" -> JsArray(JsString("bar"), JsString("baz")))
      (json \ "foo").asStringSeq mustEqual Seq("bar", "baz")
    }
    "fail if existing JSON field is read as string but not a string" in {
      val json = JsObject("foo" -> JsNumber(23))
      intercept[DeserializationException] {
        (json \ "foo").asString
      }
    }
    "fail if existing JSON field is read as array of strings, but has wrong types in it" in {
      val json = JsObject("foo" -> JsArray(JsString("bar"), JsNumber(5)))
      intercept[DeserializationException] {
        (json \ "foo").asStringSeq
      }
    }
    "fail if existing JSON field is not an array" in {
      val json = JsObject("foo" -> JsString("bar"))
      intercept[DeserializationException] {
        (json \ "foo").asStringSeq
      }
    }
  }
}
