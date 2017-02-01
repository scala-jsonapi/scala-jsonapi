package org.zalando.jsonapi.akka.http

import akka.http.scaladsl.marshalling._
import akka.http.scaladsl.model.MediaTypes._
import akka.http.scaladsl.unmarshalling._
import org.zalando.jsonapi.model._
import org.zalando.jsonapi.sprayjson.SprayJsonJsonapiProtocol
import org.zalando.jsonapi.{JsonapiRootObjectReader, JsonapiRootObjectWriter, _}
import _root_.spray.json._

trait AkkaHttpJsonapiSupport extends SprayJsonJsonapiProtocol {
  def akkaHttpJsonapiMarshaller[T: JsonapiRootObjectWriter]: ToEntityMarshaller[T] =
    Marshaller.StringMarshaller.wrap(`application/vnd.api+json`)(_.rootObject.toJson.compactPrint)

  def akkaHttpJsonapiUnmarshaller[T: JsonapiRootObjectReader]: FromEntityUnmarshaller[T] =
    Unmarshaller.stringUnmarshaller
      .forContentTypes(`application/vnd.api+json`)
      .map(_.parseJson.convertTo[RootObject].jsonapi[T])
}

object AkkaHttpJsonapiSupport extends AkkaHttpJsonapiSupport {
  implicit def akkaHttpJsonapiMarshallerImplicit[T: JsonapiRootObjectWriter]: ToEntityMarshaller[T] =
    akkaHttpJsonapiMarshaller
  implicit def akkaHttpJsonapiUnmarshallerImplicit[T: JsonapiRootObjectReader]: FromEntityUnmarshaller[T] =
    akkaHttpJsonapiUnmarshaller
}
