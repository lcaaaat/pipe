package com.lcaaaat.pipe.remote

import java.io.{File => JFile}
import java.nio.ByteBuffer

import com.lcaaaat.pipe.common.file.Files
import com.lcaaaat.pipe.common.log.Logger
import com.lcaaaat.pipe.common.thrift.{Directory, File, Pipe, PipeRemoteException, Project}
import com.lcaaaat.pipe.remote.metadata.{Metadata, RocksDBMetadata}
import com.lcaaaat.pipe.remote.PipeRemote.properties
import com.lcaaaat.pipe.remote.RemoteProperties._
import org.apache.commons.io.FileUtils

import scala.collection.mutable
import scala.util.{Failure, Success, Try}

class PipeIFaceImpl extends Pipe.Iface with Logger{

  private val metadata: Metadata = RocksDBMetadata

  private val directories = mutable.Map[Project, Directory]()

  private def withWrapper[T](f: => T): T = withLogExceptionWrapper {
    Try(f) match {
      case Success(value) =>
        value
      case Failure(exception) =>
        throw new PipeRemoteException(exception.getMessage)
    }
  }

  override def containsProject(project: Project): Boolean = withWrapper {
    LOG.info(s"Check if project ${project.name} exists.")
    metadata.selectProject(project.name).isDefined
  }

  override def registerProject(project: Project): Unit = withWrapper {
    LOG.info(s"Register project ${project.name}.")
    val baseDirectory = properties.getProperty(PROJECT_BASE_PATH)
    project.setRemotePath(Files.combine(baseDirectory, project.name))
    metadata.insertProject(project)
    FileUtils.forceMkdir(new JFile(project.getRemotePath))
  }

  override def listDirectory(project: Project): Directory = withWrapper {
    LOG.info(s"List directory for project ${project.name}.")
    val p = metadata.selectProject(project.name)
    if (p.isEmpty) {
      throw new Exception(s"Project ${project.name} does not exists.")
    }
    val path = p.get.remotePath
    val directoryOption = directories.get(project)
    if (directoryOption.isEmpty) {
      val directory = Files.scan(path)
      directories(project) = directory
      directory
    } else {
      directoryOption.get
    }
  }

  override def createDirectory(project: Project, directory: Directory): Unit = withWrapper {
    LOG.info(s"Create directory ${directory.relativePath} in project ${project.name}.")
    directories.remove(project)
    metadata.selectProject(project.name)
    FileUtils.forceMkdir(new JFile(metadata.selectProject(project.name).get.getRemotePath, directory.getRelativePath))
  }

  override def removeDirectory(project: Project, directory: Directory): Unit = withWrapper {
    LOG.info(s"Remove directory ${directory.relativePath} in project ${project.name}.")
    directories.remove(project)
    FileUtils.deleteDirectory(new JFile(metadata.selectProject(project.name).get.remotePath, directory.relativePath))
  }

  override def uploadFile(project: Project, file: File, data: ByteBuffer): Unit = withWrapper {
    LOG.info(s"Upload File ${file.relativePath} in project ${project.name}.")
    directories.remove(project)
    val f = new JFile(metadata.selectProject(project.name).get.remotePath, file.relativePath)
    FileUtils.touch(f)
    FileUtils.writeByteArrayToFile(f, data.array())
  }

  override def removeFile(project: Project, file: File): Unit = withWrapper {
    LOG.info(s"Remove File ${file.relativePath} in project ${project.name}.")
    directories.remove(project)
    FileUtils.deleteQuietly(new JFile(metadata.selectProject(project.name).get.remotePath, file.relativePath))
  }
}
