package hu.szigyi.timelapse.ramping

import java.io.File

import com.typesafe.scalalogging.LazyLogging
import hu.szigyi.timelapse.ramping.service.XmpService

class StepByStep(xmpService: XmpService) extends LazyLogging {

  def run(standard: File, image: File): Unit = {
    logger.info(s"Using '${standard.getName}' to ramp '${image.getName}'")
    val standardXmp = xmpService.getXMP(standard)
    val xmp = xmpService.getXMP(image)

    // TODO Applying the Ramping on the second 'file' from the one
    val rampedXMP = xmpService.ramp(standardXmp, xmp)
    logger.info(s"Original: $xmp")
    logger.info(s"Ramped  : $rampedXMP")
    // TODO Save the Ramped xmp file

  }
}

object StepByStep {
  def apply(xmpService: XmpService): StepByStep = new StepByStep(xmpService)
}