package com.lcaaaat.pipe.local.scan

import java.io.{File => JFile}
import java.nio.ByteBuffer

import com.lcaaaat.pipe.common.file.Files
import com.lcaaaat.pipe.common.log.Logger
import com.lcaaaat.pipe.common.thrift.{Directory, File, Pipe, Project}
import com.lcaaaat.pipe.local.ThriftClient
import org.apache.commons.io.FileUtils

import scala.collection.JavaConverters._
import scala.util.Try


class ProjectScanner(val project: Project, val scanPath: String) extends Runnable with Logger {


  def compareFiles(local: List[File], remote: List[File], client: Pipe.Client): Unit = {
    val localNames = local.map(_.name).toSet
    val remoteNames = remote.map(_.name).toSet
    val localMap = local.map { file =>
      (file.name, file)
    }.toMap
    val remoteMap = remote.map { file =>
      (file.name, file)
    }.toMap

    val needRemove = remoteNames -- localNames
    val needCreate = localNames -- remoteNames
    val needUpdate = {
      val intersect = localNames & remoteNames
      intersect.filter { name: String =>
        localMap(name).checksum != remoteMap(name).checksum
      }
    }

    (needRemove ++ needUpdate).foreach { name =>
      LOG.info(s"Remove file ${remoteMap(name).relativePath} in project ${project.name}.")
      client.removeFile(project, remoteMap(name))
    }

    (needCreate ++ needUpdate).foreach { name =>
      LOG.info(s"Upload file ${localMap(name).relativePath} in project ${project.name}.")
      val file = localMap(name)
      val jFile = new JFile(scanPath, file.relativePath)
      val bytes = ByteBuffer.wrap(FileUtils.readFileToByteArray(jFile))
      client.uploadFile(project, file, bytes)
    }
  }

  def upload(directory: Directory, client: Pipe.Client): Unit = {
    def recursive(directory: Directory): Unit = {
      directory.subDirectories.forEach { subDir =>
        client.createDirectory(project, subDir)
        recursive(subDir)
      }
      directory.subFiles.forEach { subFile =>
        val jFile = new JFile(scanPath, subFile.relativePath)
        val bytes = ByteBuffer.wrap(FileUtils.readFileToByteArray(jFile))
        client.uploadFile(project, subFile, bytes)
      }
    }
    recursive(directory)
  }

  def compareDirectories(local: List[Directory], remote: List[Directory], client: Pipe.Client): Unit = {
    val localNames = local.map(_.name).toSet
    val remoteNames = remote.map(_.name).toSet
    val localMap = local.map { file =>
      (file.name, file)
    }.toMap
    val remoteMap = remote.map { file =>
      (file.name, file)
    }.toMap

    val needRemove = remoteNames -- localNames
    val needAdd = localNames -- remoteNames
    val needKeep = localNames & remoteNames
    needRemove.foreach { name =>
      LOG.info(s"Remove directory ${remoteMap(name).relativePath} in project ${project.name}.")
      client.removeDirectory(project, remoteMap(name))
    }
    needAdd.foreach { name =>
      LOG.info(s"Create directory ${localMap(name).relativePath} in project ${project.name}.")
      client.createDirectory(project, localMap(name))
      upload(localMap(name), client)
    }
    (needAdd ++ needKeep).foreach { name =>
      compare(localMap(name), remoteMap(name), client)
    }
  }

  def compare(local: Directory, remote: Directory, client: Pipe.Client): Unit = {
    compareFiles(local.subFiles.asScala.toList, remote.subFiles.asScala.toList, client)
    compareDirectories(local.subDirectories.asScala.toList, remote.subDirectories.asScala.toList, client)
  }

  override def run(): Unit = {
    val thrift = ThriftClient()
    try {
      LOG.info(s"Scan project ${project.name}.")
      val client = thrift.client
      val remote = client.listDirectory(project)
      val local = Files.scan(scanPath)
      compare(local, remote, client)
      LOG.info(s"Sync project ${project.name} succeed.")
    } catch {
      case exception: Exception =>
        LOG.error("Scan project failed.", exception)
    } finally {
      Try(thrift.close())
    }
  }
}
