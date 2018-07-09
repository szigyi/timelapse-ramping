package hu.szigyi.timelapse.ramping.fs

import java.io.{File, FilenameFilter}

import com.typesafe.scalalogging.LazyLogging
import hu.szigyi.timelapse.ramping.conf.ImagesConfig

class FsUtil extends LazyLogging {

  def replaceExtension(file: File, ext: String): File = {
    val newPath = file.getAbsolutePath.split("\\.").dropRight(1).mkString("") + ext
    logger.debug(s"Changed Extension to $newPath")
    new File(newPath)
  }

  def workingDirectoryOf(file: File): File = new File(file.getParent)

  def filterImages(imagesConfig: ImagesConfig): FilenameFilter = {
    (_: File, name: String) => {
      imagesConfig.supportedFileExtensions.collectFirst {
        case ext if name.toLowerCase.endsWith(s".${ext.toLowerCase}") => ext
      }.isDefined
    }
  }
}

object FsUtil {
  def apply(): FsUtil = new FsUtil()
}
