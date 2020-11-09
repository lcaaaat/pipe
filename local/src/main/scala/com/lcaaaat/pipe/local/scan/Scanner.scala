package com.lcaaaat.pipe.local.scan

import java.io.{File, FileInputStream}
import java.util.concurrent.{Executors, TimeUnit}
import java.util.{Properties => JProperties}

import com.lcaaaat.pipe.common.log.Logger
import com.lcaaaat.pipe.common.thrift.Project
import com.lcaaaat.pipe.local.LocalProperties._
import com.lcaaaat.pipe.local.PipeLocal.properties
import com.lcaaaat.pipe.local.ThriftClient
import org.apache.commons.io.FileUtils
import org.apache.commons.lang3.StringUtils

import scala.collection.JavaConverters._
import scala.util.Try

object Scanner extends Logger {

  private def scan(): Map[Project, String] = {
    val projectDirectory = {
      val path = properties.getProperty(PROJECT_DIRECTORY)
      val dir = new File(path)
      if (!dir.exists() && !dir.isDirectory) {
        throw new Exception(s"$path does not exists or is not a directory")
      }
      dir
    }
    FileUtils.listFiles(projectDirectory, Array[String]("conf"), false)
      .asScala
      .filter(_.isFile)
      .map { file =>
        LOG.info(s"Load project by ${file.getAbsolutePath}.")
        val properties = new JProperties()
        val is = new FileInputStream(file)
        properties.load(is)
        is.close()

        val project = new Project()

        val name = properties.getProperty("name", "")
        if (StringUtils.isEmpty(name)) {
          LOG.error(s"`name` is empty in file ${file.getAbsolutePath}.")
          throw new Exception(s"Load ${file.getAbsolutePath} failed.")
        }
        project.setName(name)

        val description = properties.getProperty("description", "")
        if (StringUtils.isNotEmpty(description)) {
          project.setDescription(description)
        }

        val directoryPath = properties.getProperty("directory", "")
        if (StringUtils.isEmpty(directoryPath)) {
          LOG.error(s"`directory` is empty in file ${file.getAbsolutePath}.")
          throw new Exception(s"Load ${file.getAbsolutePath} failed.")
        }
        val directory = new File(directoryPath)
        if (!directory.exists() && !directory.isDirectory) {
          LOG.error("$directoryPath does not exists or is not a directory")
          throw new Exception(s"Load ${file.getAbsolutePath} failed.")
        }
        LOG.info(s"Load project $name succeed.")
        (project, directoryPath)
      }.toMap
  }


  private def registerIfMissing(project: Project): Unit = {
    val client = ThriftClient()
    val t = Try(client.client.containsProject(project))
    if (t.isFailure || !t.get) {
      LOG.info(s"List directory ${project.name} failed.")
      LOG.info(s"Register project ${project.toString}")
      client.client.registerProject(project)
    }
    client.close()
  }

  def start(): Unit = {
    val projects = scan()
    projects.keys.foreach(registerIfMissing)
    val workerThread = Executors.newScheduledThreadPool(properties.getProperty(SCANNER_WORKER_NUMBER))
    projects.foreach { pair =>
      val project = pair._1
      val path = pair._2
      workerThread.scheduleWithFixedDelay(new ProjectScanner(project, path),
        0, properties.getProperty(SCANNER_WORKER_RATE), TimeUnit.SECONDS)
    }
  }
}
