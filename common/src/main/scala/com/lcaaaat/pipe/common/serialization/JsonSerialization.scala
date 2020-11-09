package com.lcaaaat.pipe.common.serialization

import java.nio.charset.StandardCharsets

import com.google.gson.Gson

object JsonSerialization {
  def serialize(obj: AnyRef): Array[Byte] = {
    val gson = new Gson()
    gson.toJson(obj).getBytes(StandardCharsets.UTF_8)
  }

  def deserialize[T](bytes: Array[Byte], clazz: Class[T]): T = {
    val json = new String(bytes, StandardCharsets.UTF_8)
    val gson = new Gson()
    gson.fromJson(json, clazz)
  }
}
