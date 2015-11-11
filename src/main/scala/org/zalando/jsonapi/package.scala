package org.zalando

import org.zalando.jsonapi.model.RootObject

package object jsonapi {
  trait JsonapiRootObjectWriter[A] {
    def toJsonapi(a: A): RootObject
  }

  implicit class ToJsonapiRootObjectWriterOps[A](a: A) {
    def rootObject(implicit writer: JsonapiRootObjectWriter[A]): RootObject = {
      Jsonapi.asRootObject(a)
    }
  }

  object Jsonapi {
    def asRootObject[A](a: A)(implicit writer: JsonapiRootObjectWriter[A]): RootObject =
      writer toJsonapi a
  }
}
