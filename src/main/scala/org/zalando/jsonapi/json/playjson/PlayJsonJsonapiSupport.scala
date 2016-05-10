package org.zalando.jsonapi.json.playjson

import spray.httpx.PlayJsonSupport
import spray.httpx.marshalling.Marshaller
import spray.httpx.unmarshalling.Unmarshaller
import org.zalando.jsonapi.model.RootObject
import play.api.libs.json.{ JsValue, Json }
import spray.http.MediaTypes.`application/vnd.api+json`

trait PlayJsonJsonapiSupport extends PlayJsonJsonapiFormat with PlayJsonSupport {

  implicit val playJsonJsonapiMarshaller =
    Marshaller.delegate[RootObject, JsValue](`application/vnd.api+json`)(Json.toJson(_))

  implicit val playJsonJsonapiUnmarshaller =
    Unmarshaller.delegate[JsValue, RootObject](`application/vnd.api+json`)(Json.fromJson[RootObject](_).asOpt.get)
}

object PlayJsonJsonapiSupport extends PlayJsonJsonapiSupport
