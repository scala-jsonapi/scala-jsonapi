package org.zalando.jsonapi.json
package sprayjson

import org.zalando.jsonapi.model.RootObject
import spray.json._
import spray.httpx.marshalling.Marshaller

trait SprayJsonJsonapiSupport extends SprayJsonJsonapiFormat with DefaultJsonProtocol {
  implicit def sprayJsonJsonapiMarshaller(implicit printer: JsonPrinter = PrettyPrinter) =
    Marshaller.delegate[RootObject, String](`application/vnd.api+json`) { jsonapi ⇒
      printer(jsonapi.toJson)
    }
}

object SprayJsonJsonapiSupport extends SprayJsonJsonapiSupport
