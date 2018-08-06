package hu.szigyi.timelapse.ramping

import java.io.File

import com.typesafe.scalalogging.LazyLogging
import hu.szigyi.timelapse.ramping.model.XMP
import hu.szigyi.timelapse.ramping.xmp.XmpService


class Application(xmpService: XmpService) extends LazyLogging {

  def readXMPs(imageFiles: Seq[File]): Seq[XMP] = imageFiles.map(imageFile => xmpService.getXMP(imageFile))

  def rampExposure(xmps: Seq[XMP]): Seq[XMP] = {
    val rampedEVs = xmpService.rampExposure(xmps)

    val rampedXMPs = xmps.zip(rampedEVs).map {
      case (xmp: XMP, rampedEV: BigDecimal) => updateExposure(xmp, rampedEV)
    }
    rampedXMPs.foreach(xmp => logger.info(xmp.settings.exposure.toString))
    rampedXMPs
  }

  def exportXMPs(xmps: Seq[XMP]): Unit = xmps.foreach(xmp => xmpService.flushXMP(xmp))

  private def updateExposure(xmp: XMP, exposure: BigDecimal): XMP = {
    val rampedSettings = xmp.settings.copy(exposure = exposure)
    xmp.copy(settings = rampedSettings)
  }
}

object Application {
  def apply(xmpService: XmpService): Application = new Application(xmpService)
}