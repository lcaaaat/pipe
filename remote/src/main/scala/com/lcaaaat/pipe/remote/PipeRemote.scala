package com.lcaaaat.pipe.remote

import com.lcaaaat.pipe.common.log.Logger
import com.lcaaaat.pipe.common.property.Properties
import com.lcaaaat.pipe.common.thrift.Pipe
import com.lcaaaat.pipe.remote.RemoteProperties._
import org.apache.thrift.protocol.TBinaryProtocol
import org.apache.thrift.server.{TServer, TSimpleServer, TThreadPoolServer}
import org.apache.thrift.transport.{TFramedTransport, TServerSocket}


object PipeRemote extends Logger {

  lazy val properties: Properties = Properties("pipe.properties")

  def main(args: Array[String]): Unit = {
    val port = properties.getProperty(SERVER_PORT)
    val args = new TServer.Args(new TServerSocket(port))
      .processor(new Pipe.Processor[Pipe.Iface](new PipeIFaceImpl))
      .protocolFactory(new TBinaryProtocol.Factory())
    val server = new TSimpleServer(args)

    LOG.info(s"Start server on port $port")
    server.serve()
  }
}
