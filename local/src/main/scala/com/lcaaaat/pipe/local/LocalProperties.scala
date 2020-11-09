package com.lcaaaat.pipe.local

import com.lcaaaat.pipe.common.property.Property
import com.lcaaaat.pipe.common.property.Property._

object LocalProperties {
  val PROJECT_DIRECTORY: Property[String] = Property("project.directory", Option("project"), toStringFunc)
  val REMOTE_HOST: Property[String] = Property("remote.host", Option("127.0.0.1"), toStringFunc)
  val REMOTE_PORT: Property[Int] = Property("remote.port", Option(8724), toIntFunc)
  val REMOTE_TIMEOUT: Property[Int] = Property("remote.port", Option(30000), toIntFunc)
  val SCANNER_WORKER_NUMBER: Property[Int] = Property("scanner.worker.number", Option(10), toIntFunc)
  val SCANNER_WORKER_RATE: Property[Long] = Property("scanner.worker.number", Option(5L), toLongFunc)
}
