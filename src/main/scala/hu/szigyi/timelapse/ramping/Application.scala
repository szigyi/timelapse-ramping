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

      base = updateExposure(xmp, rampedExposure)

      base
    })
  }

  def exportXMPs(xmps: Seq[XMP]): Unit = xmps.foreach(xmp => xmpService.flushXMP(xmp))

  private def ramping(baseXmp: XMP, xmp: XMP): BigDecimal = {
    logger.info(s"Using '${baseXmp.xmpFilePath.getName}' to ramp '${xmp.xmpFilePath.getName}'")

    // TODO Applying the Ramping on the second 'file' from the one
    val rampedExposure = xmpService.rampExposure(baseXmp, xmp)
    logger.info(s"Base: $baseXmp")
    logger.info(s"Original: $xmp")
    logger.info(s"Ramped  : $rampedExposure")
    // TODO Save the Ramped xmp file
    //    if (!xmp.equals(rampedXMP)) {
    //      xmpService.flushRampedXMP(rampedXMP)
    //    }
    rampedExposure
  }

  private def updateExposure(xmp: XMP, exposure: BigDecimal): XMP = {
    val rampedSettings = xmp.settings.copy(exposure = exposure)
    xmp.copy(settings = rampedSettings)
  }
}

object Application {
  def apply(xmpService: XmpService): Application = new Application(xmpService)
}