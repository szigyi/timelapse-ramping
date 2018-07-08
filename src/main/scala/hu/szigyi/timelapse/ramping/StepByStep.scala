package hu.szigyi.timelapse.ramping

import java.io.File

import com.typesafe.scalalogging.LazyLogging
import hu.szigyi.timelapse.ramping.service.XmpService

class StepByStep(xmpService: XmpService) extends LazyLogging {

  def run(standard: File, image: File): Unit = {
    logger.info(s"Getting XMP files for Standard (${standard.getName}) and next (${image.getName}) images...")
    val standardXmp = xmpService.getXMP(standard)
    val xmp = xmpService.getXMP(image)
    logger.info(s"Standard: $standardXmp")


    // TODO Applying the Ramping on the second 'file' from the one
    // TODO Save the Ramped xmp file
  }
}

object StepByStep {
  def apply(xmpService: XmpService): StepByStep = new StepByStep(xmpService)
}