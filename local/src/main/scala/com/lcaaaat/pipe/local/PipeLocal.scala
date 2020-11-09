package com.lcaaaat.pipe.local

import com.lcaaaat.pipe.common.property.Properties
import com.lcaaaat.pipe.local.scan.Scanner


object PipeLocal {
  lazy val properties: Properties = Properties("pipe.properties")
  def main(args: Array[String]): Unit = {
    Scanner.start()
  }
}
