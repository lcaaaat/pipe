package com.lcaaaat.pipe.local

import com.lcaaaat.pipe.common.thrift.Pipe
import com.lcaaaat.pipe.local.LocalProperties.{REMOTE_HOST, REMOTE_PORT, REMOTE_TIMEOUT}
import com.lcaaaat.pipe.local.PipeLocal.properties
import org.apache.thrift.protocol.TBinaryProtocol
import org.apache.thrift.transport.TSocket

class ThriftClient(private val host: String, private val port: Int, private val timeout: Int) {
  private val socket = new TSocket(host, port, timeout)
  private val protocol = new TBinaryProtocol(socket)
  val client = new Pipe.Client(protocol)
  socket.open()

  def close(): Unit = {
    socket.close()
  }
}

object ThriftClient {
  def apply(): ThriftClient = {
    val host = properties.getProperty(REMOTE_HOST)
    val port = properties.getProperty(REMOTE_PORT)
    val timeout = properties.getProperty(REMOTE_TIMEOUT)
    new ThriftClient(host, port, timeout)
  }
}
