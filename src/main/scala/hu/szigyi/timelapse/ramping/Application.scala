package hu.szigyi.timelapse.ramping

import java.io.File

import com.typesafe.scalalogging.LazyLogging
import hu.szigyi.timelapse.ramping.model.EXIF
import hu.szigyi.timelapse.ramping.xmp.Service

import cats.data._


class Application(xmpService: Service) extends LazyLogging {

  def readEXIFs(imageFiles: Seq[File]): Seq[EXIF] = imageFiles.map(imageFile => xmpService.getEXIF(imageFile))

  def validate(xmps: Seq[EXIF]): Validated[Seq[ValidatedNel[String, EXIF]], Seq[EXIF]] = {
    null
  }

  def rampExposure(xmps: Seq[EXIF]): Seq[EXIF] = {
    val rampedEVs = xmpService.rampExposure(xmps)

    val rampedXMPs = xmps.zip(rampedEVs).map {
      case (xmp: EXIF, rampedEV: BigDecimal) => updateExposure(xmp, rampedEV)
    }
//    rampedXMPs.foreach(xmp => logger.info(xmp.settings.exposure.toString))
    rampedXMPs
  }

  def rampWhiteBalance(xmps: Seq[EXIF]): Seq[EXIF] = {
    val rampedWBs = xmpService.rampWhiteBalance(xmps)

    val rampedXMPs = xmps.zip(rampedWBs).map{
      case (xmp: EXIF, rampedWB: Int) => updateWhiteBalance(xmp, rampedWB)
    }
    rampedXMPs.foreach(xmp => logger.info(xmp.settings.whiteBalance.toString))
    rampedXMPs
  }

  def exportXMPs(xmps: Seq[EXIF]): Unit = xmps.foreach(xmp => xmpService.flushXMP(xmp))

  private def updateExposure(xmp: EXIF, exposure: BigDecimal): EXIF = {
    val rampedSettings = xmp.settings.copy(exposure = exposure)
    xmp.copy(settings = rampedSettings)
  }

  private def updateWhiteBalance(xmp: EXIF, wb: Int): EXIF = {
    val rampedSettings = xmp.settings.copy(whiteBalance = wb)
    xmp.copy(settings = rampedSettings)
  }
}

object Application {
  def apply(xmpService: Service): Application = new Application(xmpService)
}