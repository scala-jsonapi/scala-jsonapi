package org.zalando.jsonapi.spray

import org.zalando.jsonapi.model.RootObject
import org.zalando.jsonapi.{Jsonapi, JsonapiRootObjectReader, JsonapiRootObjectWriter}
import spray.http.MediaTypes.`application/vnd.api+json`
import spray.httpx.marshalling.Marshaller
import spray.httpx.unmarshalling.Unmarshaller

trait SprayJsonapiSupport {
  def jsonapiSprayMarshaller[T: JsonapiRootObjectWriter](implicit m: Marshaller[RootObject]): Marshaller[T] =
    Marshaller.delegate[T, RootObject](`application/vnd.api+json`)(Jsonapi.asRootObject(_))

  def jsonapiSprayUnmarshaller[T: JsonapiRootObjectReader](implicit u: Unmarshaller[RootObject]): Unmarshaller[T] =
    Unmarshaller.delegate[RootObject, T](`application/vnd.api+json`)(Jsonapi.fromRootObject(_))
}

object SprayJsonapiSupport extends SprayJsonapiSupport {
  implicit def jsonapiSprayMarshallerImplicit[T: JsonapiRootObjectWriter](implicit m: Marshaller[RootObject])
    : Marshaller[T] = jsonapiSprayMarshaller

  implicit def jsonapiSprayUnmarshallerImplicit[T: JsonapiRootObjectReader](implicit u: Unmarshaller[RootObject])
    : Unmarshaller[T] = jsonapiSprayUnmarshaller
}
