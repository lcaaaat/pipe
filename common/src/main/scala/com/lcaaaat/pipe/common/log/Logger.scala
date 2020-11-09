package com.lcaaaat.pipe.common.log

import org.slf4j.{LoggerFactory, Logger => SlfLogger}

import scala.util.{Failure, Success, Try}

trait Logger {
  protected val LOG: SlfLogger = LoggerFactory.getLogger(this.getClass.getName.stripSuffix("$"))

  protected def withLogExceptionWrapper[T](f: => T): T = {
    Try(f) match {
      case Success(value) =>
        value
      case Failure(exception) =>
        LOG.error("", exception)
        throw exception
    }
  }
}
