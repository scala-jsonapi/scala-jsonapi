package org.zalando.jsonapi.json
package sprayjson

import spray.json.CompactPrinter

class SprayJsonJsonapiSupportSpec extends JsonapiSupportSpec {
  implicit val printer = CompactPrinter
  override implicit def jsonapiRootObjectMarshaller = SprayJsonJsonapiSupport.sprayJsonJsonapiMarshaller
  override implicit def jsonapiRootObjectUnmarshaller = SprayJsonJsonapiSupport.sprayJsonJsonapiUnmarshaller
  override def jsonapiSupportClassName = "SprayJsonJsonapiSupport"
}
