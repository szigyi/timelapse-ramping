package hu.szigyi.timelapse.ramping.io

import java.io.File

import com.typesafe.scalalogging.LazyLogging

import scala.io.Source
import scala.util.{Failure, Success, Try}

class Reader(ioUtil: IOUtil) extends LazyLogging {

  def listFilesFromDirectory(dir: String, supportedExtensions: List[String]): Try[List[File]] = {
    val d = new File(dir)
    if (!d.exists) {
      Failure(new NoSuchElementException(s"Folder does not exist: $dir"))

    } else if (!d.isDirectory) {
      Failure(new IllegalArgumentException(s"It is not a folder: $dir"))

    } else {
      val files = d.listFiles(ioUtil.filterByExtensions(supportedExtensions))
        .filter(_.isFile)
        .sortWith((f1, f2) => f1.getName < f2.getName)
        .toList
      Success(files)
    }
  }

  def readFile(file: File): String = Source.fromFile(file, "UTF-8").getLines.mkString

  def isExists(file: File): Boolean = file.exists()
}

object Reader {
  def apply(ioUtil: IOUtil): Reader = new Reader(ioUtil)
}