package org.zalando.jsonapi.model.implicits

import scala.language.implicitConversions

import org.zalando.jsonapi.model.Attribute
import org.zalando.jsonapi.model.implicits.JsonApiObjectValueConversions._

object AttributeConversions {
  implicit def convertToStringAttribute(nameString: (String, String)) = Attribute(nameString._1, nameString._2)
  implicit def convertToIntAttribute(nameInt: (String, Int)) = Attribute(nameInt._1, nameInt._2)
  implicit def convertToLongAttribute(nameLong: (String, Long)) = Attribute(nameLong._1, nameLong._2)
  implicit def convertToDoubleAttribute(nameDouble: (String, Double)) = Attribute(nameDouble._1, nameDouble._2)
  implicit def convertToFloatAttribute(nameFloat: (String, Float)) = Attribute(nameFloat._1, nameFloat._2)
  implicit def convertToBooleanAttribute(nameInt: (String, Boolean)) = Attribute(nameInt._1, nameInt._2)

  implicit def convertToOptionalStringAttribute(nameString: (String, Option[String])) = nameString._2.map(nameString._1 -> _:Attribute)
  implicit def convertToOptionalIntAttribute(nameInt: (String, Option[Int])) = nameInt._2.map(nameInt._1 -> _:Attribute)
  implicit def convertToOptionalLongAttribute(nameLong: (String, Option[Long])) = nameLong._2.map(nameLong._1 -> _:Attribute)
  implicit def convertToOptionalDoubleAttribute(nameDouble: (String, Option[Double])) = nameDouble._2.map(nameDouble._1 -> _:Attribute)
  implicit def convertToOptionalFloatAttribute(nameFloat: (String, Option[Float])) = nameFloat._2.map(nameFloat._1 -> _:Attribute)
  implicit def convertToOptionalBooleanAttribute(nameInt: (String, Option[Boolean])) = nameInt._2.map(nameInt._1 -> _:Attribute)
}
