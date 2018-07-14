package hu.szigyi.timelapse.ramping.io

import java.io.File

import com.typesafe.scalalogging.LazyLogging
import hu.szigyi.timelapse.ramping.conf.ImagesConfig

import scala.io.Source

class Reader(imagesConfig: ImagesConfig, fsUtil: IOUtil) extends LazyLogging {

  def listFilesFromDirectory(dir: String, supportedExtensions: List[String]): List[File] = {
    val d = new File(dir)
    if (!d.exists) {
      throw new NoSuchElementException(s"Folder does not exist: $dir")

    } else if (!d.isDirectory) {
      throw new NoSuchElementException(s"It is not a folder: $dir")

    } else {
      d.listFiles(fsUtil.filterByExtensions(supportedExtensions))
        .filter(_.isFile)
        .sortWith((f1, f2) => f1.getName < f2.getName)
        .toList
    }
  }

  def readFile(file: File): String = Source.fromFile(file, "UTF-8").getLines.mkString

  def isExists(file: File): Boolean = file.exists()
}

object Reader {
  def apply(imagesConfig: ImagesConfig, fsUtil: IOUtil): Reader = new Reader(imagesConfig, fsUtil)
}