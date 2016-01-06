package org.zalando.jsonapi.json
package playjson

import org.zalando.jsonapi.model.RootObject
import play.api.libs.json.{ Json, JsValue }
import spray.httpx.PlayJsonSupport
import spray.httpx.marshalling.Marshaller

trait PlayJsonJsonapiSupport extends PlayJsonJsonapiFormat with PlayJsonSupport {
  implicit val playJsonJsonapiMarshaller =
    Marshaller.delegate[RootObject, JsValue](`application/vnd.api+json`)(Json.toJson(_))
}

object PlayJsonJsonapiSupport extends PlayJsonJsonapiSupport