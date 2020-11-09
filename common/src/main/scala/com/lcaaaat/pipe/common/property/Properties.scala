package com.lcaaaat.pipe.common.property

import java.io.{File, FileInputStream, InputStream}
import java.util.{Properties => JProperties}

import collection.JavaConverters._
import scala.collection.mutable


class Properties(private val map: mutable.Map[String, String]) {
  def getProperty[T](property: Property[T]): T = {
    getPropertyOption(property).get
  }

  def getPropertyOption[T](property: Property[T]): Option[T] = {
    map.get(property.key).map(property.func).orElse(property.default)
  }

  def getByPrefix(prefix: String): Map[String, String] = {
    map.filter(_._1.startsWith(prefix)).map { pair =>
      (pair._1.substring(prefix.length), pair._2)
    }.toMap
  }
}


object Properties {
  def apply(inputStream: InputStream): Properties = {
    val properties = new JProperties()
    properties.load(inputStream)
    new Properties(properties.asScala)
  }

  def apply(file: String): Properties = {
    val url = this.getClass.getClassLoader.getResource(file)
    apply(url.openStream())
  }

  def apply(): Properties = new Properties(mutable.Map[String, String]())
}