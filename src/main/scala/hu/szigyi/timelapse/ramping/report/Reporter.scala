package hu.szigyi.timelapse.ramping.report

import cats.data.NonEmptyList
import com.typesafe.scalalogging.LazyLogging
import hu.szigyi.timelapse.ramping.math.BigDecimalDecorator._
import hu.szigyi.timelapse.ramping.model._
import hu.szigyi.timelapse.ramping.validate.Violations.{ApertureParsingViolation, ApertureRangeViolation, Violation}
import hu.szigyi.timelapse.ramping.csv.Encoders._
import hu.szigyi.timelapse.ramping.math.BigDecimalConverter._

class Reporter extends LazyLogging {
  var lineNumber = -1
  val headers = Seq(
    "number",
    "file_name",
    "key_frame",
    "iso",
    "shutter_speed",
    "aperture",
    "exposure",
    "temperature",
    "ramped_exposure",
    "ramped_temperature",
    "iso_scaled",
    "shutter_speed_scaled",
    "aperture_scaled",
    "exposure_scaled",
    "temperature_scaled",
    "ramped_exposure_scaled",
    "ramped_temperature_scaled"
  )

  def reportResult(processed: Seq[Processed]): String = {
    val isos = processed.map(_.metadata.settings.iso)
    val sss = processed.map(_.metadata.settings.shutterSpeed)
    val apertures = processed.map(_.metadata.settings.aperture)
    val exposures = processed.map(_.metadata.settings.exposure)
    val temperatures = processed.map(_.metadata.settings.temperature)
    val rampedExposures = processed.map(_.processedSettings.exposure.getOrElse(ZERO))
    val rampedTemperatures = processed.map(_.processedSettings.temperature.getOrElse(0))
    implicit val isoScale = ZeroOneScale[ISO](isos.min, isos.max)
    implicit val sssScale = ZeroOneScale[ShutterSpeed](sss.min, sss.max)
    implicit val apertureScale = ZeroOneScale[Aperture](apertures.min, apertures.max)
    implicit val exposureScale = ZeroOneScale[Exposure](exposures.min, exposures.max)
    implicit val temperatureScale = ZeroOneScale[Temperature](temperatures.min, temperatures.max)
    implicit val rampedExposureScale = ZeroOneScale[RampedExposure](rampedExposures.min, rampedExposures.max)
    implicit val rampedTemperatureScale = ZeroOneScale[RampedTemperature](rampedTemperatures.min, rampedTemperatures.max)

    val csv = processed.map(exif => {
      val scaled = Scaled(
        exif,
        ScaledSettings(Scale.zeroOneScale[ISO](exif.metadata.settings.iso),
          Scale.zeroOneScale[ShutterSpeed](exif.metadata.settings.shutterSpeed),
          Scale.zeroOneScale[Aperture](exif.metadata.settings.aperture),
          Scale.zeroOneScale[Exposure](exif.metadata.settings.exposure),
          Scale.zeroOneScale[Temperature](exif.metadata.settings.temperature),
          Scale.zeroOneScale[RampedExposure](exif.processedSettings.exposure.getOrElse(ZERO)),
          Scale.zeroOneScale[RampedTemperature](convertFromInt(exif.processedSettings.temperature.getOrElse(0)))
        )
      )
      val exifCsv: String = scaled.toCsv
      lineNumber += 1
      lineNumber.toString + "," + exifCsv
    }).mkString("\n")
    headers.mkString(",") + "\n" + csv
  }

  def reportError(genericViolations: NonEmptyList[Violation]): Unit = {
    logger.error("Unable to proceed. Issues have found in the images metadata.")

    val (violations, fatalViolation) = genericViolations.toList.partition(v => {
      v.isInstanceOf[ApertureParsingViolation] || v.isInstanceOf[ApertureRangeViolation]
    })

    if (!violations.isEmpty) {
      logger.error("Please provide default value for the Aperture! Metadata is missing (seems you used manual lens) but you can provide a default one like:")
      logger.error("$ export DEFAULT_APERTURE=11")
      violations.foreach(ex => logger.error("{} - {}", ex.imagePath, ex.message))
    }
    if (!fatalViolation.isEmpty) {
      logger.error("Please fix the missing metadata in the following images!")
      violations.foreach(ex => logger.error("{} - {}", ex.imagePath, ex.message))
    }
  }
}

object Reporter {
  def apply(): Reporter = new Reporter()
}