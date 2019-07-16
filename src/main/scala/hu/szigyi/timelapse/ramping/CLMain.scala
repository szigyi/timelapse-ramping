package hu.szigyi.timelapse.ramping

import java.io.File

import cats.data.Validated.{Invalid, Valid}
import cats.data.{NonEmptyList, Validated}
import cats.implicits._
import com.typesafe.scalalogging.LazyLogging
import hu.szigyi.timelapse.ramping.cli.CLIConfig
import hu.szigyi.timelapse.ramping.factory.ComponentFactory
import hu.szigyi.timelapse.ramping.model.{Metadata, Processed}
import hu.szigyi.timelapse.ramping.validate.EXIFValidator.EXIFValid
import hu.szigyi.timelapse.ramping.validate.Violations.Violation

import scala.util.{Failure, Success, Try}

object CLMain extends App with LazyLogging with ComponentFactory {
  logger.info("Timelapse Ramping application is running...")

  val parser = new scopt.OptionParser[CLIConfig]("Timelapse Ramping") {
    head("Timelapse Ramping", "0.0.1")

    opt[String]('p', "pathToPictures")
      .required()
      .valueName("<p>")
      .action((p, c) => c.copy(pathToPictures = p))
      .text("pathToPictures is required")
  }
  parser.parse(args, CLIConfig()) match {
    case None => logger.error("error during parsing arguments")
    case Some(config) => {
      val dir = config.pathToPictures
      val reportFile = new File(dir + "/csv_report.csv")
      logger.info(s"1. Listing all the images from $dir ...")
      val imageFilesTry: Try[Seq[File]] = reader.listFilesFromDirectory(dir, imagesConfig.supportedFileExtensions)

      imageFilesTry match {
        case Success(imageFiles) => processImages(imageFiles, reportFile)
        case Failure(exception) => logger.error(s"Unable to proceed. ${exception.getMessage}")
      }
    }
  }

  def processImages(imageFiles: Seq[File], reportFile: File) = {
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
        report(ramped, reportFile)
      }
      case Invalid(violations) => reporter.reportError(violations)
    }
  }

  def report(ramped: Seq[Processed], reportFile: File) = {
    logger.info(s"7. Generating Report ...")
    val report = reporter.reportResult(ramped)
    logger.info(s"8. Exporting the report to ${reportFile.getAbsolutePath} ...")
    application.exportReport(reportFile, report)

  }
}
