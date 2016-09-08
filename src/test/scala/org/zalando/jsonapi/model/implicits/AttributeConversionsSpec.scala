package org.zalando.jsonapi.model.implicits

import org.scalatest.{Matchers, WordSpec}
import org.zalando.jsonapi.model.Attribute
import org.zalando.jsonapi.model.JsonApiObject._
import org.zalando.jsonapi.model.implicits.AttributeConversions._

class AttributeConversionsSpec extends WordSpec with Matchers {
  "scala tuples" should {
    "be converted to string attributes" in {
      convertToStringAttribute("name" -> "string") should be(Attribute("name", StringValue("string")))
    }
    "be converted to number attributes" in {
      convertToIntAttribute("name" -> 42) should be(Attribute("name", NumberValue(42)))
      convertToLongAttribute("name" -> 42l) should be(Attribute("name", NumberValue(42)))
      convertToFloatAttribute("name" -> 42f) should be(Attribute("name", NumberValue(42)))
      convertToDoubleAttribute("name" -> 42d) should be(Attribute("name", NumberValue(42)))
    }
    "be converted to boolean attributes" in {
      convertToBooleanAttribute("name" -> true) should be(Attribute("name", BooleanValue(true)))
      convertToBooleanAttribute("name" -> false) should be(Attribute("name", BooleanValue(false)))
    }

    "be converted to optional string attributes" in {
      convertToOptionalStringAttribute("name" -> Option("string")) should be(Option(Attribute("name", StringValue("string"))))
    }
    "be converted to optional number attributes" in {
      convertToOptionalIntAttribute("name" -> Option(42)) should be(Option(Attribute("name", NumberValue(42))))
      convertToOptionalLongAttribute("name" -> Option(42l)) should be(Option(Attribute("name", NumberValue(42))))
      convertToOptionalFloatAttribute("name" -> Option(42f)) should be(Option(Attribute("name", NumberValue(42))))
      convertToOptionalDoubleAttribute("name" -> Option(42d)) should be(Option(Attribute("name", NumberValue(42))))
    }
    "be converted to optional boolean attributes" in {
      convertToOptionalBooleanAttribute("name" -> Option(true)) should be(Option(Attribute("name", BooleanValue(true))))
      convertToOptionalBooleanAttribute("name" -> Option(false)) should be(Option(Attribute("name", BooleanValue(false))))
    }
  }
}
