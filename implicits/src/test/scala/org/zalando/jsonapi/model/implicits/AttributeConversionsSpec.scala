package org.zalando.jsonapi.model.implicits

import org.scalatest.{Matchers, WordSpec}
import org.zalando.jsonapi.model.Attribute
import org.zalando.jsonapi.model.JsonApiObject._
import org.zalando.jsonapi.model.implicits.AttributeConversions._

class AttributeConversionsSpec extends WordSpec with Matchers {
  "scala tuples" should {
    "be converted to string attributes" in {
      convertPairToAttribute("name" → "string") should be(Attribute("name", StringValue("string")))
    }
    "be converted to number attributes" in {
      convertPairToAttribute("name" → 42) should be(Attribute("name", NumberValue(42)))
      convertPairToAttribute("name" → 42l) should be(Attribute("name", NumberValue(42)))
      convertPairToAttribute("name" → 42f) should be(Attribute("name", NumberValue(42)))
      convertPairToAttribute("name" → 42d) should be(Attribute("name", NumberValue(42)))
    }
    "be converted to boolean attributes" in {
      convertPairToAttribute("name" → true) should be(Attribute("name", TrueValue))
      convertPairToAttribute("name" → false) should be(Attribute("name", FalseValue))
    }
    "be converted to null attributes" in {
      convertPairToAttribute(("name" → null)) should be(Attribute("name", NullValue))
    }
    "be converted to js object attributes" in {
      convertPairToAttribute(("name" → Map("null" → null))) should be(Attribute("name", JsObjectValue(List(Attribute("null", NullValue)))))
    }
    "be converted to js array attributes" in {
      convertPairToAttribute(("name" → List(null))) should be(Attribute("name", JsArrayValue(List(NullValue))))
    }

    "be converted to optional string attributes" in {
      convertPairToOptionalAttribute("name" → Option("string")) should be(Option(Attribute("name", StringValue("string"))))
    }
    "be converted to optional number attributes" in {
      convertPairToOptionalAttribute("name" → Option(42)) should be(Option(Attribute("name", NumberValue(42))))
      convertPairToOptionalAttribute("name" → Option(42l)) should be(Option(Attribute("name", NumberValue(42))))
      convertPairToOptionalAttribute("name" → Option(42f)) should be(Option(Attribute("name", NumberValue(42))))
      convertPairToOptionalAttribute("name" → Option(42d)) should be(Option(Attribute("name", NumberValue(42))))
    }
    "be converted to optional boolean attributes" in {
      convertPairToOptionalAttribute("name" → Option(true)) should be(Option(Attribute("name", BooleanValue(true))))
      convertPairToOptionalAttribute("name" → Option(false)) should be(Option(Attribute("name", BooleanValue(false))))
    }
    "be converted to optional null attributes" in {
      convertPairToOptionalAttribute(("name" → Option(null))) should be(None)
    }
    "be converted to optional js object attributes" in {
      convertPairToOptionalAttribute(("name" → Option(Map("null" → null)))) should be(Option(Attribute("name", JsObjectValue(List(Attribute("null", NullValue))))))
    }
    "be converted to optional js array attributes" in {
      convertPairToOptionalAttribute(("name" → Option(List(null)))) should be(Option(Attribute("name", JsArrayValue(List(NullValue)))))
    }
  }
}
