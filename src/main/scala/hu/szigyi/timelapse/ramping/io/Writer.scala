package hu.szigyi.timelapse.ramping.io

import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Files

class Writer {

  def write(file: File, content: String): Unit = {
    Files.write(file.toPath, content.getBytes(StandardCharsets.UTF_8))
  }
}

object Writer {
  def apply(): Writer = new Writer()
}