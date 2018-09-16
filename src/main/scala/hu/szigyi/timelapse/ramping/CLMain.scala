package hu.szigyi.timelapse.ramping

import java.io.File

import cats.data.Validated.{Invalid, Valid}
import cats.data.{NonEmptyList, Validated}
import cats.implicits._
import com.typesafe.scalalogging.LazyLogging
import hu.szigyi.timelapse.ramping.factory.ComponentFactory
import hu.szigyi.timelapse.ramping.model.EXIF
import hu.szigyi.timelapse.ramping.validator.EXIFValidator.EXIFValid
import hu.szigyi.timelapse.ramping.validator.Violations.Violation

import scala.util.{Failure, Success, Try}

object CLMain extends App with LazyLogging with ComponentFactory {
  logger.info("Timelapse Ramping application is running...")

  // TODO parsing program arguments - folder of the pictures
  private val dir = "/Users/szabolcs/jumping_sunset/"
  //  private val dir = "/Volumes/Marvin/Pictures/Canon70D/2018/2018.09.02 - London - Isle of Dogs - Long timelapse - Sunset and Big Carriage constellation"

  logger.info(s"1. Listing all the images from $dir ...")
  private val imageFilesTry: Try[Seq[File]] = reader.listFilesFromDirectory(dir, imagesConfig.supportedFileExtensions)

  imageFilesTry match {
    case Success(imageFiles) => processImages(imageFiles)
    case Failure(exception) => logger.error(s"Unable to proceed. ${exception.getMessage}")
  }

  def processImages(imageFiles: Seq[File]) = {
    logger.info(s"\tFound ${imageFiles.size} images.")
    logger.info(s"2. Reading EXIF metadata from the images ...")
    val exifs: Seq[EXIFValid[EXIF]] = application.readEXIFs(imageFiles)

    logger.info("3. Validating the EXIF data ...")
    val validatedExifs: Validated[NonEmptyList[Violation], Seq[EXIF]] = exifs.toList.sequence[EXIFValid, EXIF]
    validatedExifs match {
      case Valid(validExifs) => {
        val rampedEXIFs = rampingImages(validExifs)
        logger.info(s"6. Exporting the ramped EXIFs into the XMP (sidecar) files (${exifs.size}) ...")
        application.exportXMPs(rampedEXIFs)
      }
      case Invalid(violations) => reporter.report(violations)
    }
  }

  def rampingImages(exifs: Seq[EXIF]): Seq[EXIF] = {
    logger.info("\tEXIFs data are valid.")
    logger.info("4. Ramping exposure over the images ...")
    val rampedEVs: Seq[EXIF] = application.rampExposure(exifs)

    if (defaultConfig.rampWhiteBalance) {
      logger.info("5. Ramping temperature over the images ...")
      application.rampTemperature(rampedEVs)
    } else {
      logger.info("\tNot ramping temperature over the images.")
      rampedEVs
    }
  }
}
