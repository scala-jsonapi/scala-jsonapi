package org.zalando.jsonapi.json
package playjson

import org.zalando.jsonapi.model.RootObject
import play.api.libs.json._
import spray.http.MediaTypes._
import spray.http._
import spray.httpx.marshalling.Marshaller
import spray.httpx.unmarshalling._

import scala.util.control.NonFatal

trait PlayJsonapiSupport {
  implicit def playJsonUnmarshaller[T: Reads] =
    delegate[String, T](`application/json`, `application/vnd.api+json`)(string ⇒
      try {
        implicitly[Reads[T]].reads(Json.parse(string)).asEither.left.map(e ⇒ MalformedContent(s"Received JSON is not valid.\n${Json.prettyPrint(JsError.toFlatJson(e))}"))
      } catch {
        case NonFatal(exc) ⇒ Left(MalformedContent(exc.getMessage, exc))
      })(UTF8StringUnmarshaller)

  implicit def playJsonMarshaller[T: Writes](implicit printer: JsValue ⇒ String = Json.stringify) =
    Marshaller.delegate[T, String](ContentTypes.`application/json`, ContentType(MediaTypes.`application/vnd.api+json`, HttpCharsets.`UTF-8`)) { value ⇒
      printer(implicitly[Writes[T]].writes(value))
    }

  //
  private val UTF8StringUnmarshaller = new Unmarshaller[String] {
    def apply(entity: HttpEntity) = Right(entity.asString(defaultCharset = HttpCharsets.`UTF-8`))
  }

  // Unmarshaller.delegate is used as a kind of map operation; play-json JsResult can contain either validation errors or the JsValue
  // representing a JSON object. We need a delegate method that works as a flatMap and let the provided A ⇒ Deserialized[B] function
  // to deal with any possible error, including exceptions.
  //
  private def delegate[A, B](unmarshalFrom: ContentTypeRange*)(f: A ⇒ Deserialized[B])(implicit ma: Unmarshaller[A]): Unmarshaller[B] =
    new SimpleUnmarshaller[B] {
      val canUnmarshalFrom = unmarshalFrom
      def unmarshal(entity: HttpEntity) = ma(entity).right.flatMap(a ⇒ f(a))
    }
}

trait PlayJsonJsonapiSupport extends PlayJsonJsonapiFormat with PlayJsonapiSupport {

  implicit val playJsonJsonapiMarshaller =
    Marshaller.delegate[RootObject, JsValue](`application/vnd.api+json`)(Json.toJson(_))

  implicit val playJsonJsonapiUnmarshaller =
    Unmarshaller.delegate[JsValue, RootObject](`application/vnd.api+json`)(Json.fromJson[RootObject](_).asOpt.get)
}

object PlayJsonJsonapiSupport extends PlayJsonJsonapiSupport
