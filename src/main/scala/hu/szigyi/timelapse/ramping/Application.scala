package hu.szigyi.timelapse.ramping

import java.io.File

import com.typesafe.scalalogging.LazyLogging
import hu.szigyi.timelapse.ramping.model._
import hu.szigyi.timelapse.ramping.service.Service
import hu.szigyi.timelapse.ramping.validate.EXIFValidator.EXIFValid


class Application(service: Service) extends LazyLogging {

  def readEXIFs(imageFiles: Seq[File]): Seq[EXIFValid[Metadata]] = imageFiles.map(imageFile => service.getEXIF(imageFile))

  def ramp(exifs: Seq[Metadata], rampWB: Boolean): Seq[Processed] = {
    val rampedEVs = service.rampExposure(exifs)

    if (rampWB) {
      val rampedTemps: Seq[Int] = service.rampTemperature(exifs)

      exifs.zip(rampedEVs).zip(rampedTemps).map {
        case ((exif: Metadata, rampedEV: BigDecimal), rampedTemp: Int) => {
          Processed(exif, ProcessedSettings(Some(rampedEV), Some(rampedTemp)))
        }
      }
    } else {
      exifs.zip(rampedEVs).map {
        case (exif: Metadata, rampedEv: BigDecimal) => Processed(exif, ProcessedSettings(Some(rampedEv), None))
      }
    }
  }

  def exportXMPs(exifs: Seq[Processed]): Unit = exifs.foreach(exif => service.flushXMP(exif))

  def exportReport(reportFile: File, csv: String): Unit = service.flushReport(reportFile, csv)
}

object Application {
  def apply(service: Service): Application = new Application(service)
}