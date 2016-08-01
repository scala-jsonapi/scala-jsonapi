package org.zalando.jsonapi

package object json {
  private[json] def collectSome[A](opts: Option[A]*): List[A] =
    (opts collect { case Some(field) ⇒ field }).toList
}
