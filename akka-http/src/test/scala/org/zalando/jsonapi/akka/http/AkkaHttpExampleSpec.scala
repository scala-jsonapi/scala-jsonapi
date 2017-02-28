package org.zalando.jsonapi.akka.http

import akka.http.scaladsl.model.MediaTypes.`application/vnd.api+json`
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{FlatSpec, Matchers}
import org.zalando.jsonapi.akka.http.AkkaHttpJsonapiSupport._
import org.zalando.jsonapi.json.Person
import spray.json._

class AkkaHttpExampleSpec extends FlatSpec with Matchers with ScalatestRouteTest {

  private val route = path("") {
    complete {
      Person(42, "foobar")
    }
  }

  it should "marshall Person and set correct Content-Type" in {
    Get("/") ~> route ~> check {
      val jsonResponse = responseAs[String].parseJson

      val expectedJson =
        """
          {
            "data": {
              "id": "42",
              "type": "person",
              "attributes": {
                "name": "foobar"
              }
            }
          }
        """.stripMargin.parseJson

      jsonResponse shouldBe expectedJson

      response.entity.contentType.mediaType shouldBe `application/vnd.api+json`
    }
  }
}

