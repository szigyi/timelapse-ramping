package hu.szigyi.timelapse.ramping.io

import java.io.{File, FilenameFilter}

import com.typesafe.scalalogging.LazyLogging

class IOUtil extends LazyLogging {

  private val DOT: String = "."

  def replaceExtension(file: File, ext: String): File = {
    val newPath = file.getAbsolutePath.split("\\" + DOT).dropRight(1).mkString(DOT) + DOT + ext
    logger.debug(s"Changed Extension to $newPath")
    new File(newPath)
  }

  def filterByExtensions(supportedExtensions: List[String]): FilenameFilter = {
    (_: File, name: String) => {
      supportedExtensions.collectFirst {
        case ext if name.toLowerCase.endsWith(s".${ext.toLowerCase}") => ext
      }.isDefined
    }
  }
}

object IOUtil {
  def apply(): IOUtil = new IOUtil()
}
