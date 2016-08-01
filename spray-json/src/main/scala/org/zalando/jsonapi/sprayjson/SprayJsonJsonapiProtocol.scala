package org.zalando.jsonapi.sprayjson

import spray.json.DefaultJsonProtocol

/**
  * Jsonapi JSON pootocol for serializing and deserializing Jsonapi representations.
  */
trait SprayJsonJsonapiProtocol extends DefaultJsonProtocol with SprayJsonJsonapiFormat

/**
  * Import this to get the Jsonapi Spray-JSON serialization into scope without mixing in the trait
  */
object SprayJsonJsonapiProtocol extends SprayJsonJsonapiProtocol
