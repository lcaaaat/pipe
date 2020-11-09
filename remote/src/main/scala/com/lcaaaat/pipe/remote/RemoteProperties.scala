package com.lcaaaat.pipe.remote

import com.lcaaaat.pipe.common.property.Property
import com.lcaaaat.pipe.common.property.Property._

object RemoteProperties {
  val SERVER_PORT: Property[Int] = Property("server.port", Option(8724), toIntFunc)
  val SERVER_WORKER_MIN_THREADS: Property[Int] = Property("server.worker.min.threads", Option(1), toIntFunc)
  val SERVER_WORKER_MAX_THREADS: Property[Int] = Property("server.worker.max.threads", Option(50), toIntFunc)
  val ROCKSDB_PATH: Property[String] = Property("rocksdb.path", Option("rocksdb"), toStringFunc)
  val ROCKSDB_OPTION_CREATE_IF_MISSING: Property[Boolean] =
    Property("rocksdb.option.create.if.missing", Option(true), toBooleanFunc)
  val ROCKSDB_OPTION_CREATE_MISSING_CF: Property[Boolean] =
    Property("rocksdb.option.create.missing.cf", Option(true), toBooleanFunc)
  val PROJECT_BASE_PATH: Property[String] = Property("project.base.path", Option("project"), toStringFunc)
}
