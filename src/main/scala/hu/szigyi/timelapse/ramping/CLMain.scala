package hu.szigyi.timelapse.ramping

import java.io.File

import cats.data.Validated.{Invalid, Valid}
import cats.data.{NonEmptyList, Validated}
import cats.implicits._
import com.typesafe.scalalogging.LazyLogging
import hu.szigyi.timelapse.ramping.factory.ComponentFactory
import hu.szigyi.timelapse.ramping.model.{Metadata, Processed, ProcessedSettings}
import hu.szigyi.timelapse.ramping.validate.EXIFValidator.EXIFValid
import hu.szigyi.timelapse.ramping.validate.Violations.Violation

import scala.util.{Failure, Success, Try}

object CLMain extends App with LazyLogging with ComponentFactory {
  logger.info("Timelapse Ramping application is running...")

  // TODO parsing program arguments - folder of the pictures
//  private val dir = "/Users/szabolcs/jumping_sunset/"
//    private val dir = "E:\\Pictures\\Canon70D\\2018\\2018.07.08 - London - Isle of Dogs - Sunset with jumping exposure\\original"
//    private val dir = "G:\\Pictures\\Canon70D\\2018\\2018.08.06 - London - Isle of Dogs - Sunset ramped - Beautiful sunset with clouds and coloures"
//    private val dir = "G:\\Pictures\\Canon70D\\2018\\2018.10.21 - London - Isle of Dogs - Sunset timelapse\\original"
//    private val dir = "G:\\Pictures\\Canon70D\\2018\\2018.06.03 - London - Isle of Dogs - Sunset through filters\\original\\transition"
//    private val dir = "G:\\Pictures\\Canon70D\\2018\\2018.09.02 - London - Isle of Dogs - Long timelapse - Sunset and Big Carriage constellation"
    private val dir = "G:\\Pictures\\Canon70D\\2019\\2019.04.21 - Scotland - Isle of Skye\\Sunrise - Fog storm\\The Quiraing"
  private val reportFile = new File(dir + "/csv_report.csv")
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
    val exifs: Seq[EXIFValid[Metadata]] = application.readEXIFs(imageFiles)

    logger.info("3. Validating the EXIF data ...")
    val validatedExifs: Validated[NonEmptyList[Violation], Seq[Metadata]] = exifs.toList.sequence[EXIFValid, Metadata]
    validatedExifs match {
      case Valid(validExifs) => {
        logger.info("\tEXIFs data are valid.")
        logger.info("4. Ramping exposure and/or temperature over the images ...")
        val ramped = application.ramp(validExifs, defaultConfig.rampWhiteBalance)

        if (!modesConfig.reportOnly) {
          logger.info(s"6. Exporting the ramped EXIFs into the XMP (sidecar) files (${exifs.size}) ...")
          application.exportXMPs(ramped)
        }
        report(ramped)
      }
      case Invalid(violations) => reporter.reportError(violations)
    }
  }

  def report(ramped: Seq[Processed]) = {
    logger.info(s"7. Generating Report ...")
    val report = reporter.reportResult(ramped)
    logger.info(s"8. Exporting the report to ${reportFile.getAbsolutePath} ...")
    application.exportReport(reportFile, report)

  }
}
