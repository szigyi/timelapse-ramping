package hu.szigyi.timelapse.ramping

import java.io.File

import com.typesafe.scalalogging.LazyLogging
import hu.szigyi.timelapse.ramping.model.XMP
import hu.szigyi.timelapse.ramping.xmp.XmpService


class Application(xmpService: XmpService) extends LazyLogging {

  def readXMPs(imageFiles: Seq[File]): Seq[XMP] = imageFiles.map(imageFile => xmpService.getXMP(imageFile))

  def interpolateExposure(xmps: Seq[XMP]): Seq[XMP] = {
    val windowSize = 20
    val step = 1
    val size = xmps.size
    val last = xmps(size - 1)
    // Add last element n-1 times to the end
    val extendedXMPs = xmps ++ Seq.fill(windowSize - 1)(last)

    extendedXMPs.sliding(windowSize, step).map(window => {
      val xmp = window(0)
      val baseEV = xmpService.getEV(xmp)
      val rampedEV = ramp(window: _*)
      val adjustedEV = baseEV - rampedEV
      logger.info(s"Base  : $baseEV")
      logger.info(s"Ramped: $adjustedEV")
      updateExposure(xmp, adjustedEV)
    }).toSeq
  }

  def exportXMPs(xmps: Seq[XMP]): Unit = xmps.foreach(xmp => xmpService.flushXMP(xmp))

  private def ramp(xmps: XMP*): BigDecimal = {
    logger.info(s"Using: ${xmps.map(xmp=>xmp.xmpFilePath.getName).mkString(", ")}")
    xmpService.rampExposure(xmps: _*)
  }

  private def updateExposure(xmp: XMP, exposure: BigDecimal): XMP = {
    val rampedSettings = xmp.settings.copy(exposure = exposure)
    xmp.copy(settings = rampedSettings)
  }
}

object Application {
  def apply(xmpService: XmpService): Application = new Application(xmpService)
}