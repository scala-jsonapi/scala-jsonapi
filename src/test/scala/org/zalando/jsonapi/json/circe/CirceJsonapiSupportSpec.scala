package org.zalando.jsonapi.json.circe

import org.zalando.jsonapi.json.JsonapiSupportSpec
import org.zalando.jsonapi.model.RootObject
import spray.httpx.marshalling.Marshaller
import spray.httpx.unmarshalling.Unmarshaller

class CirceJsonapiSupportSpec extends JsonapiSupportSpec {
  override def jsonapiSupportClassName: String = "CirceJsonapiSupport"

  override implicit def jsonapiRootObjectUnmarshaller: Unmarshaller[RootObject] = CirceJsonapiSupport.circeJsonapiUnmarshaller

  override implicit def jsonapiRootObjectMarshaller: Marshaller[RootObject] = CirceJsonapiSupport.circeJsonapiMarshaller
}
