package com.lcaaaat.pipe.remote.metadata

import com.lcaaaat.pipe.common.thrift.{Directory, Project}

trait Metadata {
  def selectProject(name: String): Option[Project]

  def insertProject(project: Project)

  def close()
}
