package org.zalando.jsonapi.json.circe

import io.circe.Json
import io.circe.generic.auto._
import io.circe.syntax._
import org.zalando.jsonapi.json.circe.CirceJsonapiEncoders._
import org.zalando.jsonapi.json.circe.CirceJsonapiDecoders._
import org.zalando.jsonapi.{JsonapiRootObjectWriter, JsonapiRootObjectReader}
import org.zalando.jsonapi.model.RootObject

trait CirceJsonapiRootObjectWriter extends JsonapiRootObjectWriter[Json] {
  def toJsonapi(json: Json): RootObject = json.as[RootObject].right.get
}

case object CirceJsonapiRootObjectWriter extends CirceJsonapiRootObjectWriter

trait CirceJsonapiRootObjectReader extends JsonapiRootObjectReader[Json] {
  def fromJsonapi(rootObject: RootObject): Json = rootObject.asJson
}

case object CirceJsonapiRootObjectReader extends CirceJsonapiRootObjectReader

trait CirceJsonapiRootObjectFormat extends CirceJsonapiRootObjectWriter with CirceJsonapiRootObjectReader

object CirceJsonapiRootObjectFormat extends CirceJsonapiRootObjectFormat