package org.zalando

import org.zalando.jsonapi.model.RootObject

package object jsonapi {

  /**
   * Type class whose instances are able to be written as Jsonapi root objects.
   * @tparam A the type that is an instance of this type class
   */
  trait JsonapiRootObjectWriter[A] {
    /**
     * Returns a Jsonapi [[model.RootObject]] representation of the given object.
     * @param a the object to be written as a Jsonapi [[model.RootObject]]
     */
    def toJsonapi(a: A): RootObject
  }

  /**
   * Implicit class that provides operations on types that are instances of the [[JsonapiRootObjectWriter]] type class.
   */
  implicit class ToJsonapiRootObjectWriterOps[A](a: A) {
    /**
     * Returns a Jsonapi [[model.RootObject]] from the wrapped object.
     */
    def rootObject(implicit writer: JsonapiRootObjectWriter[A]): RootObject = {
      Jsonapi.asRootObject(a)
    }
  }

  /**
   * Entry point for interacting with the Jsonapi library.
   */
  object Jsonapi {
    /**
     * Returns a Jsonapi [[model.RootObject]] from the provided value.
     */
    def asRootObject[A](a: A)(implicit writer: JsonapiRootObjectWriter[A]): RootObject =
      writer toJsonapi a
  }
}
