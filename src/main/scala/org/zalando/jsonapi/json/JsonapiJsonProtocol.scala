package org.zalando.jsonapi.json

import spray.json.DefaultJsonProtocol

/**
 * Jsonapi JSON pootocol for serializing and deserializing Jsonapi representations.
 */
trait JsonapiJsonProtocol extends DefaultJsonProtocol with JsonapiJsonFormat

/**
 * Import this to get the Jsonapi Spray-JSON serialization into scope without mixing in the trait
 */
object JsonapiJsonProtocol extends JsonapiJsonProtocol
