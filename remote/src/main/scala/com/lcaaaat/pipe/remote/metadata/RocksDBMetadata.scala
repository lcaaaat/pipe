package com.lcaaaat.pipe.remote.metadata

import com.lcaaaat.pipe.common.log.Logger
import com.lcaaaat.pipe.common.serialization.ThriftSerialization
import com.lcaaaat.pipe.common.thrift.Project
import com.lcaaaat.pipe.remote.PipeRemote.properties
import com.lcaaaat.pipe.remote.RemoteProperties._
import org.rocksdb.{Options, RocksDB}

object RocksDBMetadata extends Metadata with Logger {

  private lazy val rocksDB = {
    RocksDB.loadLibrary()
    val options = new Options()
    options.setCreateIfMissing(properties.getProperty(ROCKSDB_OPTION_CREATE_IF_MISSING))
    options.setCreateMissingColumnFamilies(properties.getProperty(ROCKSDB_OPTION_CREATE_MISSING_CF))
    RocksDB.open(options, properties.getProperty(ROCKSDB_PATH))
  }

  override def selectProject(name: String): Option[Project] = withLogExceptionWrapper {
    val bytes = rocksDB.get(name.getBytes())
    Option(bytes).map { bytes =>
      val ret = new Project()
      ThriftSerialization.deserialize(ret, bytes)
      ret
    }
  }

  override def insertProject(project: Project): Unit = withLogExceptionWrapper {
    if (selectProject(project.name).isDefined) {
      throw new Exception(s"Project ${project.name} already exists.")
    }
    rocksDB.put(project.getName.getBytes(), ThriftSerialization.serialize(project))
  }

  override def close(): Unit = {
    rocksDB.close()
  }
}
