package hu.szigyi.timelapse.ramping

import java.io.File

import com.typesafe.scalalogging.LazyLogging
import hu.szigyi.timelapse.ramping.model.XMP
import hu.szigyi.timelapse.ramping.xmp.XmpService

class Application(xmpService: XmpService) extends LazyLogging {

  def readXMPs(imageFiles: Seq[File]): Seq[XMP] = imageFiles.map(imageFile => xmpService.getXMP(imageFile))

  def interpolateExposure(xmps: Seq[XMP]): Seq[XMP] = {
    var base: XMP = xmps.head
    xmps.drop(1).map(xmp => {
      val rampedExposure = ramping(base, xmp)
      // Updating the base with the current XMP because the current updated will be the base for the next iteration
      // TODO it is not a functional way of doing it, it smells
      logger.info(s"Base    : $base")
      base = updateExposure(xmp, rampedExposure)
      logger.info(s"Original: $xmp")
      logger.info(s"Ramped  : $base\n\n")
      base
    })
  }

  def exportXMPs(xmps: Seq[XMP]): Unit = xmps.foreach(xmp => xmpService.flushXMP(xmp))

  private def ramping(baseXmp: XMP, xmp: XMP): BigDecimal = {
    logger.info(s"Using '${baseXmp.xmpFilePath.getName}' to ramp '${xmp.xmpFilePath.getName}'")
    xmpService.rampExposure(baseXmp, xmp)
  }

  private def updateExposure(xmp: XMP, exposure: BigDecimal): XMP = {
    val rampedSettings = xmp.settings.copy(exposure = exposure)
    xmp.copy(settings = rampedSettings)
  }
}

object Application {
  def apply(xmpService: XmpService): Application = new Application(xmpService)
}