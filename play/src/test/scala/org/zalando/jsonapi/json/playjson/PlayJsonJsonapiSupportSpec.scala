package org.zalando.jsonapi.json
package playjson

import org.zalando.jsonapi.model.RootObject
import spray.httpx.marshalling.Marshaller
import spray.httpx.unmarshalling.Unmarshaller

class PlayJsonJsonapiSupportSpec {
  implicit def jsonapiRootObjectMarshaller: Marshaller[RootObject] = PlayJsonJsonapiSupport.playJsonJsonapiMarshaller

  implicit def jsonapiRootObjectUnmarshaller: Unmarshaller[RootObject] = PlayJsonJsonapiSupport.playJsonJsonapiUnmarshaller
}
