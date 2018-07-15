package hu.szigyi.timelapse.ramping

import java.io.File

import com.typesafe.scalalogging.LazyLogging
import hu.szigyi.timelapse.ramping.factory.ComponentFactory

object Main extends App with LazyLogging with ComponentFactory {
  logger.info("Timelapse Ramping application is running...")

  // TODO parsing program arguments - folder of the pictures
  private val dir = "/Users/szabolcs/jumping_sunset/"

  logger.info(s"Listing all the images ...")
  private val files: List[File] = reader.listFilesFromDirectory(dir, imagesConfig.supportedFileExtensions)
  logger.info(s"Found ${files.size} images")
  private val shiftingWindow = 1
  private val shiftedFiles = files.drop(shiftingWindow)

  private val filesTuple = files.zip(shiftedFiles)
  filesTuple.foreach(tuple => stepByStep.run(tuple._1, tuple._2))
}
