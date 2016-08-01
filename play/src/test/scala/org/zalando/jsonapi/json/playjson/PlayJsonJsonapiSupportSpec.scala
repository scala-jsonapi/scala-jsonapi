package org.zalando.jsonapi.json
package playjson

import org.zalando.jsonapi.model.RootObject
import spray.httpx.marshalling.Marshaller
import spray.httpx.unmarshalling.Unmarshaller

class PlayJsonJsonapiSupportSpec extends JsonapiSupportSpec {
  override def jsonapiSupportClassName: String = "PlayJsonJsonapiSupport"

  override implicit def jsonapiRootObjectMarshaller: Marshaller[RootObject] = PlayJsonJsonapiSupport.playJsonJsonapiMarshaller

  override implicit def jsonapiRootObjectUnmarshaller: Unmarshaller[RootObject] = PlayJsonJsonapiSupport.playJsonJsonapiUnmarshaller
}
