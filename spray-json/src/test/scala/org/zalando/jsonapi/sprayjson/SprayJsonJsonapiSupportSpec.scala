package org.zalando.jsonapi.sprayjson

import spray.json.CompactPrinter

class SprayJsonJsonapiSupportSpec {
  implicit val printer = CompactPrinter
  implicit def jsonapiRootObjectMarshaller = SprayJsonJsonapiSupport.sprayJsonJsonapiMarshaller
  implicit def jsonapiRootObjectUnmarshaller = SprayJsonJsonapiSupport.sprayJsonJsonapiUnmarshaller
}
