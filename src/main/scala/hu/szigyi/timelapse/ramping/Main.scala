package hu.szigyi.timelapse.ramping

import java.io.File

import com.typesafe.scalalogging.LazyLogging
import hu.szigyi.timelapse.ramping.factory.ComponentFactory
import hu.szigyi.timelapse.ramping.model.XMP

object Main extends App with LazyLogging with ComponentFactory {
  logger.info("Timelapse Ramping application is running...")

  // TODO parsing program arguments - folder of the pictures
  private val dir = "/Users/szabolcs/jumping_sunset/"

  logger.info(s"Listing all the images ...")
  private val imageFiles: Seq[File] = reader.listFilesFromDirectory(dir, imagesConfig.supportedFileExtensions)
  logger.info(s"Found ${imageFiles.size} images")

  private val xmps: Seq[XMP] = application.readXMPs(imageFiles)
  private val rampedXMPs: Seq[XMP] = application.rampExposure(xmps)
  application.exportXMPs(rampedXMPs)
}
