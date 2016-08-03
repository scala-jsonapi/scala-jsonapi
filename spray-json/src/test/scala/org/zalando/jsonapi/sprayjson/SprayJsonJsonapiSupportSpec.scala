package org.zalando.jsonapi.sprayjson

import org.zalando.jsonapi.json.JsonapiSupportSpec
import org.zalando.jsonapi.spray.SprayJsonJsonapiSupport
import spray.json.CompactPrinter

class SprayJsonJsonapiSupportSpec extends JsonapiSupportSpec {
  implicit val printer = CompactPrinter
  override implicit def jsonapiRootObjectMarshaller = SprayJsonJsonapiSupport.sprayJsonJsonapiMarshaller
  override implicit def jsonapiRootObjectUnmarshaller = SprayJsonJsonapiSupport.sprayJsonJsonapiUnmarshaller
  override def jsonapiSupportClassName = "SprayJsonJsonapiSupport"
}
