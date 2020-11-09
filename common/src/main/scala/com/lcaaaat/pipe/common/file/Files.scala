package com.lcaaaat.pipe.common.file

import com.lcaaaat.pipe.common.thrift.{Directory, File}
import java.io.{FileInputStream, File => JFile}

import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.TrueFileFilter

import scala.collection.JavaConverters._

object Files {
  def scan(path: String): Directory = {
    def recursiveDirectory(directory: JFile, relative: List[String]): Directory = {
      val subs = directory.listFiles()
      val ret = new Directory()
      ret.setName(directory.getName)
      ret.setRelativePath(relative.mkString(JFile.separator))
      val subFiles = subs.filter(_.isFile).map { file =>
        val ret = new File()
        ret.setName(file.getName)
        ret.setRelativePath((relative ++ List(file.getName)).mkString(JFile.separator))
        val is = new FileInputStream(file)
        ret.setChecksum(DigestUtils.md5Hex(is))
        is.close()
        ret
      }.toList.asJava
      ret.setSubFiles(subFiles)
      val subDirectories = subs.filter(_.isDirectory).map { directory =>
        recursiveDirectory(directory, relative ++ List(directory.getName))
      }.toList.asJava
      ret.setSubDirectories(subDirectories)
      ret
    }
    val directory = FileUtils.getFile(path)
    if (!directory.exists() || !directory.isDirectory) {
      throw new Exception(s"$path does not exists or is not a directory")
    }
    recursiveDirectory(directory, List.empty)
  }

  def combine(parent: String, file: String): String = {
    if (parent.endsWith(JFile.separator)) {
      parent + file
    } else {
      parent + JFile.separator + file
    }
  }

  def main(args: Array[String]): Unit = {
    val path = "/Users/lijiye/Applications/pipe"
    scan(path)
  }
}
