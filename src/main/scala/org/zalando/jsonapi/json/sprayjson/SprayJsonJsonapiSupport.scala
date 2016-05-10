package org.zalando.jsonapi.json
package sprayjson

import org.zalando.jsonapi.model.RootObject
import spray.httpx.unmarshalling.Unmarshaller
import spray.json._
import spray.httpx.marshalling.Marshaller
import spray.http.MediaTypes.`application/vnd.api+json`

trait SprayJsonJsonapiSupport extends SprayJsonJsonapiFormat with DefaultJsonProtocol {

  implicit def sprayJsonJsonapiMarshaller(implicit printer: JsonPrinter = PrettyPrinter) =
    Marshaller.delegate[RootObject, String](`application/vnd.api+json`) { jsonapi ⇒
      printer(jsonapi.toJson)
    }

  implicit def sprayJsonJsonapiUnmarshaller =
    Unmarshaller.delegate[String, RootObject](`application/vnd.api+json`) { string ⇒
      string.parseJson.convertTo[RootObject]
    }
}

object SprayJsonJsonapiSupport extends SprayJsonJsonapiSupport
