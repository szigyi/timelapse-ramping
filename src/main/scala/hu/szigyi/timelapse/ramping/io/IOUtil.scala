package hu.szigyi.timelapse.ramping.io

import java.io.{File, FilenameFilter}

import com.typesafe.scalalogging.LazyLogging

class IOUtil extends LazyLogging {

  def replaceExtension(file: File, ext: String): File = {
    val newPath = file.getAbsolutePath.split("\\.").dropRight(1).mkString("") + ext
    logger.debug(s"Changed Extension to $newPath")
    new File(newPath)
  }

  def workingDirectoryOf(file: File): File = new File(file.getParent)

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
