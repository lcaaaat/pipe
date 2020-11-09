package com.lcaaaat.pipe.common.serialization

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}

import org.apache.thrift.{TBase, TFieldIdEnum}
import org.apache.thrift.protocol.TBinaryProtocol
import org.apache.thrift.transport.TIOStreamTransport


object ThriftSerialization {
  def serialize[T <: TBase[_, _], F <: TFieldIdEnum](obj: TBase[T, F]): Array[Byte] = {
    val out = new ByteArrayOutputStream()
    val transport = new TIOStreamTransport(out)
    val protocol = new TBinaryProtocol(transport)
    obj.write(protocol)
    out.toByteArray
  }

  def deserialize[T <: TBase[_, _], F <: TFieldIdEnum](obj: TBase[T, F], array: Array[Byte]): Unit = {
    val in = new ByteArrayInputStream(array)
    val transport = new TIOStreamTransport(in)
    val protocol = new TBinaryProtocol(transport)
    obj.read(protocol)
  }
}
