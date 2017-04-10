package org.zalando.jsonapi.json.circe

import io.circe.Json
import io.circe.parser.parse
import org.zalando.jsonapi.json.JsonBaseSpec
import org.zalando.jsonapi.model._

class CirceJsonapiFormatSpec extends JsonBaseSpec[Json] with CirceJsonapiRootObjectFormat {
  override protected def parseJson(jsonString: String): Json = parse(jsonString).right.get
  implicit val writer = CirceJsonapiRootObjectWriter
  implicit val reader = CirceJsonapiRootObjectReader
}
