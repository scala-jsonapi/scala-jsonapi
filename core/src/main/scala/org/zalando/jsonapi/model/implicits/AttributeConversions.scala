package org.zalando.jsonapi.model.implicits

import scala.language.implicitConversions

import org.zalando.jsonapi.model.Attribute
import org.zalando.jsonapi.model.implicits.JsonApiObjectValueConversions._

object AttributeConversions {
  implicit def convertPairToOptionalAttribute(pair: (String, Option[_])): Option[Attribute] = {
    pair._2.map(Attribute(pair._1, _))
  }

  implicit def convertPairToAttribute(pair: (String, _)): Attribute = {
    Attribute(pair._1, pair._2)
  }
}
