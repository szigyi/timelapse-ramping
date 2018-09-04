package hu.szigyi.timelapse.ramping

import java.io.File

import com.typesafe.scalalogging.LazyLogging
import hu.szigyi.timelapse.ramping.model.XMP
import hu.szigyi.timelapse.ramping.xmp.XmpService

import cats.data._
import cats.data.Validated._
import cats.implicits._


class Application(xmpService: XmpService) extends LazyLogging {

  def readXMPs(imageFiles: Seq[File]): Seq[XMP] = imageFiles.map(imageFile => xmpService.getXMP(imageFile))

  def validate(xmps: Seq[XMP]): Validated[Seq[ValidatedNel[String, XMP]], Seq[XMP]] = {
    null
  }

  def rampExposure(xmps: Seq[XMP]): Seq[XMP] = {
    val rampedEVs = xmpService.rampExposure(xmps)

    val rampedXMPs = xmps.zip(rampedEVs).map {
      case (xmp: XMP, rampedEV: BigDecimal) => updateExposure(xmp, rampedEV)
    }
//    rampedXMPs.foreach(xmp => logger.info(xmp.settings.exposure.toString))
    rampedXMPs
  }

  def rampWhiteBalance(xmps: Seq[XMP]): Seq[XMP] = {
    val rampedWBs = xmpService.rampWhiteBalance(xmps)

    val rampedXMPs = xmps.zip(rampedWBs).map{
//      case (xmp: XMP, rampedWB: Int) => updateWhiteBalance(xmp, rampedWB)
      case (xmp: XMP, rampedWB: Int) => xmp
    }
    rampedXMPs.foreach(xmp => logger.info(xmp.settings.whiteBalance.toString))
    rampedXMPs
  }

  def exportXMPs(xmps: Seq[XMP]): Unit = xmps.foreach(xmp => xmpService.flushXMP(xmp))

  private def updateExposure(xmp: XMP, exposure: BigDecimal): XMP = {
    val rampedSettings = xmp.settings.copy(exposure = exposure)
    xmp.copy(settings = rampedSettings)
  }

  private def updateWhiteBalance(xmp: XMP, wb: Int): XMP = {
    val rampedSettings = xmp.settings.copy(whiteBalance = wb)
    xmp.copy(settings = rampedSettings)
  }
}

object Application {
  def apply(xmpService: XmpService): Application = new Application(xmpService)
}