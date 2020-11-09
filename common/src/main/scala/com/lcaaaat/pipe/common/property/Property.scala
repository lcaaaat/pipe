package com.lcaaaat.pipe.common.property

import org.apache.commons.lang3.StringUtils

case class Property[T](key: String, default: Option[T], func: String => T)

object Property {
  val DEFAULT_LIST_SEPARATOR = ","

  val toIntFunc: String => Int = { string: String => string.toInt }
  val toLongFunc: String => Long = { string: String => string.toLong }
  val toDoubleFunc: String => Double = { string: String => string.toDouble }
  val toBooleanFunc: String => Boolean = { string: String => string.toBoolean }
  val toStringFunc: String => String = { string: String => string }
  val toListFunc: String => List[String] = {
    string: String =>
      if (StringUtils.isBlank(string)) {
        List[String]()
      } else {
        string.split(DEFAULT_LIST_SEPARATOR).map(_.trim).toList
      }
  }
}