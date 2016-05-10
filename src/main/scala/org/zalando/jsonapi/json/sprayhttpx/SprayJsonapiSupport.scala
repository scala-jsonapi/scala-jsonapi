package org.zalando.jsonapi.json.sprayhttpx

import org.zalando.jsonapi.model.RootObject
import org.zalando.jsonapi.{ Jsonapi, JsonapiRootObjectReader, JsonapiRootObjectWriter }
import spray.http.MediaTypes.`application/vnd.api+json`
import spray.httpx.marshalling.Marshaller
import spray.httpx.unmarshalling.Unmarshaller

trait SprayJsonapiSupport {

  implicit def jsonapiJsonConvertableMarshaller[T: JsonapiRootObjectWriter](implicit m: Marshaller[RootObject]): Marshaller[T] =
    Marshaller.delegate[T, RootObject](`application/vnd.api+json`)(Jsonapi.asRootObject(_))

  implicit def jsonapiJsonConvertableUnmarshaller[T: JsonapiRootObjectReader](implicit u: Unmarshaller[RootObject]): Unmarshaller[T] =
    Unmarshaller.delegate[RootObject, T](`application/vnd.api+json`)(Jsonapi.fromRootObject(_))

}
