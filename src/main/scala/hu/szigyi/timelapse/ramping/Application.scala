package hu.szigyi.timelapse.ramping

import java.io.File

import com.typesafe.scalalogging.LazyLogging
import hu.szigyi.timelapse.ramping.model.EXIF
import hu.szigyi.timelapse.ramping.xmp.Service

import cats.data._


class Application(service: Service) extends LazyLogging {

  def readEXIFs(imageFiles: Seq[File]): Seq[EXIF] = imageFiles.map(imageFile => service.getEXIF(imageFile))

  def validate(exifs: Seq[EXIF]): Validated[Seq[ValidatedNel[String, EXIF]], Seq[EXIF]] = {
    null
  }

  def rampExposure(exifs: Seq[EXIF]): Seq[EXIF] = {
    val rampedEVs = service.rampExposure(exifs)

    val rampedEXIFs = exifs.zip(rampedEVs).map {
      case (exif: EXIF, rampedEV: BigDecimal) => updateExposure(exif, rampedEV)
    }
//    rampedXMPs.foreach(xmp => logger.info(xmp.settings.exposure.toString))
    rampedEXIFs
  }

  def rampTemperature(exifs: Seq[EXIF]): Seq[EXIF] = {
    val rampedTemps = service.rampTemperature(exifs)

    val rampedEXIFs = exifs.zip(rampedTemps).map{
      case (exif: EXIF, rampedWB: Int) => updateTemperature(exif, rampedWB)
    }
    rampedEXIFs.foreach(exif => logger.info(exif.settings.temperature.toString))
    rampedEXIFs
  }

  def exportXMPs(exifs: Seq[EXIF]): Unit = exifs.foreach(exif => service.flushXMP(exif))

  private def updateExposure(exif: EXIF, exposure: BigDecimal): EXIF = {
    val rampedSettings = exif.settings.copy(exposure = exposure)
    exif.copy(settings = rampedSettings)
  }

  private def updateTemperature(exif: EXIF, temperature: Int): EXIF = {
    val rampedSettings = exif.settings.copy(temperature = temperature)
    exif.copy(settings = rampedSettings)
  }
}

object Application {
  def apply(service: Service): Application = new Application(service)
}