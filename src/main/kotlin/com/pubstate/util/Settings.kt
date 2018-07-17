package com.pubstate.util

import java.io.File
import java.io.FileInputStream
import java.util.*


object Settings {
  val props = {
    val path = System.getProperty("user.home") + "/pubcn-settings.properties"
    val properties = Properties()
    if (File(path).exists()) {
      properties.load(FileInputStream(path))
    }
    properties
  }()
}