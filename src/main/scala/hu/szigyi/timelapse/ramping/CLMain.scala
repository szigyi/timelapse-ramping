package hu.szigyi.timelapse.ramping

import java.io.File

import cats.data.Validated.Valid
import cats.data.{NonEmptyList, Validated, ValidatedNel}
import com.typesafe.scalalogging.LazyLogging
import hu.szigyi.timelapse.ramping.factory.ComponentFactory
import hu.szigyi.timelapse.ramping.model.XMP

object CLMain extends App with LazyLogging with ComponentFactory {
  logger.info("Timelapse Ramping application is running...")

  // TODO parsing program arguments - folder of the pictures
//  private val dir = "/Users/szabolcs/jumping_sunset/"
  private val dir = "/Volumes/Marvin/Pictures/Canon70D/2018/2018.09.02 - London - Isle of Dogs - Long timelapse - Sunset and Big Carriage constellation"

  logger.info(s"Listing all the images ...")
  private val imageFiles: Seq[File] = reader.listFilesFromDirectory(dir, imagesConfig.supportedFileExtensions)
  logger.info(s"Found ${imageFiles.size} images")

  private val xmps: Seq[XMP] = application.readXMPs(imageFiles)
  // TODO accumulate the validation into one Valid object to continue the process based on it
//  private val validatedXMPs: Validated[ValidatedNel[String, XMP]] = application.validate(xmps)
//  validatedXMPs match {
//    case Valid(xmps: XMP) =>
//  }
  private val rampedEVs: Seq[XMP] = application.rampExposure(xmps)
  private val rampedWBs: Seq[XMP] = if (defaultConfig.rampWhiteBalance) application.rampWhiteBalance(rampedEVs) else rampedEVs
  application.exportXMPs(rampedWBs)
}
