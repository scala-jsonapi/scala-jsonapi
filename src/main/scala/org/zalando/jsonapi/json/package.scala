package org.zalando.jsonapi

import org.zalando.jsonapi.model.RootObject
import spray.http.{ MediaType, MediaTypes }
import spray.httpx.marshalling.Marshaller

package object json {
  private[json] def collectSome[A](opts: Option[A]*): List[A] =
    (opts collect { case Some(field) ⇒ field }).toList

  /**
   * The Jsonapi JSON media type.
   */
  val jsonapiMediaType = "application/vnd.api+json"

  /**
   * The Jsonapi JSON media type as a [[spray.http.MediaType]].
   */
  val `application/vnd.api+json` = MediaTypes.register(MediaType.custom(jsonapiMediaType))

  implicit def jsonapiJsonConvertableMarshaller[T: JsonapiRootObjectWriter](implicit m: Marshaller[RootObject]): Marshaller[T] =
    Marshaller.delegate[T, RootObject](`application/vnd.api+json`)(Jsonapi.asRootObject(_))
}
