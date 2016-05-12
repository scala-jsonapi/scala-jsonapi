package org.zalando.jsonapi.json.sprayjson

import akka.http.scaladsl.marshalling.{ ToEntityMarshaller, Marshaller }
import org.zalando.jsonapi.model.RootObject
import spray.json._
import akka.http.scaladsl.model.MediaTypes.`application/vnd.api+json`
import akka.http.scaladsl.unmarshalling.{ FromEntityUnmarshaller, Unmarshaller }

trait AkkaHttpJsonapiSupport extends SprayJsonJsonapiFormat with DefaultJsonProtocol {
  implicit def akkaHttpJsonJsonapiMarshaller(implicit printer: JsonPrinter = PrettyPrinter): ToEntityMarshaller[RootObject] = {
    Marshaller.StringMarshaller.wrap(`application/vnd.api+json`)(printer).compose(_.toJson)
  }

  implicit def akkaHttpJsonJsonapiUnmarshaller: FromEntityUnmarshaller[RootObject] = {
    Unmarshaller
      .byteStringUnmarshaller
      .forContentTypes(`application/vnd.api+json`)
      .mapWithCharset((data, charset) ⇒ data.decodeString(charset.nioCharset.name).parseJson.convertTo[RootObject])
  }
}

object AkkaHttpJsonapiSupport extends AkkaHttpJsonapiSupport
