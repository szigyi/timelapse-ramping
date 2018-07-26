package hu.szigyi.timelapse.ramping.algo

import hu.szigyi.timelapse.ramping.model.{XMP, XMPSettings}

/**
  * Equations are based on the wikipedia page of Exposure Value https://en.wikipedia.org/wiki/Exposure_value<br/>
  * <br/>
  * EV equation<br/>
  * EV=log2(N^2^/t)<br/>
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
    * EVdiff=log2(base/current)<br/>
    * @param base base shutter speed
    * @param current current shutter speed
    * @return EVdiff which is positive if current should be brighter or negative if current should be darker
    */
  def shutterSpeeds(base: BigDecimal, current: BigDecimal): BigDecimal = {
    val div = base / current
    log2(div, defaultMathContext)
  }

  /**
    * EVdiff=log2(current^2^/base^2^)<br/>
    * @param base base aperture
    * @param current current aperture
    * @return EVdiff which is positive if current should be brighter or negative if current should be darker
    */
  def apertures(base: BigDecimal, current: BigDecimal): BigDecimal = {
    val div = current.`^2` / base.`^2`
    log2(div, defaultMathContext)
  }

  /**
    * EVdiff=log2(base/current)<br/>
    * @param base base ISO
    * @param current current ISO
    * @return EVdiff which is positive if current should be brighter or negative if current should be darker
    */
  def ISOs(base: Int, current: Int): BigDecimal = {
    val div = BigDecimal(base) / current
    log2(div, defaultMathContext)
  }
}

object Equations {
  def apply(): Equations = new Equations()
}

class EVdifference(equations: Equations) {

  import hu.szigyi.timelapse.ramping.math.BigDecimalDecorator._

  /**
    * If shutter speeds are equal then returns None
    * If base's shutter speed is bigger then
    * @param base
    * @param current
    * @return
    */
  def fromShutterSpeeds(base: XMPSettings, current: XMPSettings): Option[BigDecimal] =
    if (base.shutterSpeed === current.shutterSpeed) None
    else Some(equations.shutterSpeeds(base.shutterSpeed, current.shutterSpeed))

  def fromApertures(base: XMPSettings, current: XMPSettings): Option[BigDecimal] =
    if (base.aperture === current.aperture) None
    else Some(equations.apertures(base.aperture, current.aperture))

  def fromISOs(base: XMPSettings, current: XMPSettings): Option[BigDecimal] =
    if (base.iso.equals(current.iso)) None
    else Some(equations.ISOs(base.iso, current.iso))
}

object EVdifference {
  def apply(equations: Equations): EVdifference = new EVdifference(equations)
}

class MirrorPrevious(exposureAlgo: EVdifference) {
  def rampExposure(base: XMP, current: XMP): BigDecimal = {
    val shutterBias = exposureAlgo.fromShutterSpeeds(base.settings, current.settings)
    val apertureBias = exposureAlgo.fromApertures(base.settings, current.settings)
    val isoBias = exposureAlgo.fromISOs(base.settings, current.settings)

    // Copying forward the standard's exposure
    val baseExposure = base.settings.exposure
    val rampedExposure = expoAdd(expoAdd(expoAdd(baseExposure, shutterBias), apertureBias), isoBias)
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
  def apply(exposureBias: EVdifference): MirrorPrevious = new MirrorPrevious(exposureBias)
}

class AverageWindow(avgCount: Int) {

  def ramp(images: List[XMP]): Unit = {

  }
}