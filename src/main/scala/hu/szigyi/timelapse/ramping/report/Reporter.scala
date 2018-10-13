package hu.szigyi.timelapse.ramping.report

import cats.data.NonEmptyList
import com.typesafe.scalalogging.LazyLogging
import hu.szigyi.timelapse.ramping.validator.Violations.{ApertureParsingViolation, ApertureRangeViolation, Violation}

class Reporter extends LazyLogging {

  def report(genericViolations: NonEmptyList[Violation]): Unit = {
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