
package org.zalando.jsonapi.model.implicits

import org.scalatest.{Matchers, WordSpec}
import org.zalando.jsonapi.model.JsonApiObject._
import org.zalando.jsonapi.model.implicits.JsonApiObjectValueConversions._

class JsonApiObjectValueConversionsSpec extends WordSpec with Matchers {
  "scala values" should {
    "be converted to string values" in {
      convertStringToStringValue("string") should be(StringValue("string"))
    }
    "be converted to number values" in {
      convertIntToNumberValue(42) should be(NumberValue(42))
      convertLongToNumberValue(42l) should be(NumberValue(42))
      convertFloatToNumberValue(42f) should be(NumberValue(42))
      convertDoubleToNumberValue(42d) should be(NumberValue(42))
    }
    "be converted to boolean values" in {
      convertBooleanToBooleanValue(true) should be(BooleanValue(true))
      convertBooleanToBooleanValue(false) should be(BooleanValue(false))
    }
  }
}
