package hu.szigyi.timelapse.ramping.algo

import hu.szigyi.timelapse.ramping.model.{XMP, XMPSettings}

/**
  * Equations are based on the wikipedia page of Exposure Value https://en.wikipedia.org/wiki/Exposure_value<br/>
  * <br/>
  * EV equation<br/>
  * EV=log2(N2/t)<br/>
  * N = relative aperture<br/>
  * t = shutter speed<br/>
  */
class Equations {

  import scala.math._
  import math.BigDecimal._
  import ch.obermuhlner.math.big.BigDecimalMath._
  import hu.szigyi.timelapse.ramping.math.BigDecimalConverter._
  import hu.szigyi.timelapse.ramping.math.BigDecimalDecorator._

  /**
    * EVdiff=log2(ss1/ss2)<br/>
    * @param ss1 base shutter speed
    * @param ss2 current shutter speed
    * @return EVdiff
    */
  def shutterSpeeds(ss1: BigDecimal, ss2: BigDecimal): BigDecimal = {
    val div = ss1 / ss2
    log2(div, defaultMathContext)
  }

  /**
    * EVdiff=log2(a1^2^/a2^2^)<br/>
    * @param a1 base aperture
    * @param a2 current aperture
    * @return EVdiff
    */
  def apertures(a1: BigDecimal, a2: BigDecimal): BigDecimal = {
    val div = a1.`^2` / a2.`^2`
    log2(div, defaultMathContext)
  }

  /**
    * EVdiff=log2(i1/i2)<br/>
    * @param i1 base ISO
    * @param i2 current ISO
    * @return EVdiff
    */
  def ISOs(i1: Int, i2: Int): BigDecimal = {
    val div = BigDecimal(i1) / BigDecimal(i2)
    log2(div, defaultMathContext)
  }
}

object Equations {
  def apply(): Equations = new Equations()
}

class ExposureAlgorithm(equations: Equations) {

  import hu.szigyi.timelapse.ramping.math.BigDecimalDecorator._

  def shutterSpeeds(standard: XMPSettings, image: XMPSettings): Option[BigDecimal] = {
    if (standard.shutterSpeed === image.shutterSpeed) {
      None
    } else if (standard.shutterSpeed > image.shutterSpeed) {
      Some((equations.shutterSpeeds(standard.shutterSpeed, image.shutterSpeed)).neg)
    } else {
      Some(equations.shutterSpeeds(image.shutterSpeed, standard.shutterSpeed))
    }
  }

  def apertures(standard: XMPSettings, image: XMPSettings): Option[BigDecimal] = {
    if (standard.aperture === image.aperture) None
    else Some(equations.apertures(standard.aperture, image.aperture))
  }

  def ISOs(standard: XMPSettings, image: XMPSettings): Option[BigDecimal] = {
    if (standard.iso.equals(image.iso)) {
      None
    } else if (standard.iso > image.iso) {
      Some((equations.ISOs(standard.iso, image.iso)).neg)
    } else {
      Some(equations.ISOs(image.iso, standard.iso))
    }
  }
}

object ExposureAlgorithm {
  def apply(equations: Equations): ExposureAlgorithm = new ExposureAlgorithm(equations)
}

class MirrorPrevious(exposureAlgo: ExposureAlgorithm) {
  def rampExposure(standard: XMP, image: XMP): BigDecimal = {
    val shutterBias = exposureAlgo.shutterSpeeds(standard.settings, image.settings)
    val apertureBias = exposureAlgo.apertures(standard.settings, image.settings)
    val isoBias = exposureAlgo.ISOs(standard.settings, image.settings)

    // Copying forward the standard's exposure
    val standardExposure = standard.settings.exposure
    val rampedExposure = expoAdd(expoAdd(expoAdd(standardExposure, shutterBias), apertureBias), isoBias)
    rampedExposure
//    val rampedSettings = image.settings.copy(exposure = rampedExposure)
//    image.copy(settings = rampedSettings)
  }

  private def expoAdd(expo: BigDecimal, addition: Option[BigDecimal]): BigDecimal = addition match {
    case None => expo
    case Some(bd) => expo + bd
  }
}

object MirrorPrevious {
  def apply(exposureBias: ExposureAlgorithm): MirrorPrevious = new MirrorPrevious(exposureBias)
}

class AverageWindow(avgCount: Int) {

  def ramp(images: List[XMP]): Unit = {

  }
}