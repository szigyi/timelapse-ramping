package hu.szigyi.timelapse.ramping

import java.io.File

import com.typesafe.scalalogging.LazyLogging
import hu.szigyi.timelapse.ramping.bchm.Perf
import hu.szigyi.timelapse.ramping.factory.ComponentFactory

object Main extends App with LazyLogging with ComponentFactory {
  logger.info("Timelapse Ramping application is running...")

  // TODO parsing program arguments - folder of the pictures
  private val dir = "/Users/szabolcs/jumping_sunset/"

  logger.info(s"Reading all the files...")
  private val files: List[File] = reader.readFilesFromDirectory(dir)
  logger.info(s"Found ${files.size} images")

  for ((standard, image) <- files.zip(files.drop(1))) {
    val step = StepByStep(xmpService)
    step.run(standard, image)
  }
}
