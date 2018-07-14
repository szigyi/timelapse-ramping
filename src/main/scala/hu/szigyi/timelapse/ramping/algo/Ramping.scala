package hu.szigyi.timelapse.ramping.algo

import hu.szigyi.timelapse.ramping.model.{XMP, XMPSettings}

class Equations {

  import scala.math._
  import ch.obermuhlner.math.big.BigDecimalMath
  import hu.szigyi.timelapse.ramping.math.BigDecimalContext._
  import hu.szigyi.timelapse.ramping.math.BigDecimalConverter._

  def shutterSpeeds(ss1: BigDecimal, ss2: BigDecimal): BigDecimal = {
    val div = ss1 / ss2
    BigDecimalMath.log2(div, mathContext)
  }

  def apertures(a1: BigDecimal, a2: BigDecimal): BigDecimal = {
    val div = a1 / a2
    BigDecimalMath.log(div, mathContext) / log(sqrt(2))
  }

  def ISOs(i1: Int, i2: Int): BigDecimal = {
    val div = i1 / i2
    BigDecimalMath.log2(div, mathContext)
  }
}

object Equations {
  def apply(): Equations = new Equations()
}

class ExposureBias(equations: Equations) {

  import hu.szigyi.timelapse.ramping.math.BigDecimalEquals._

  private def neg(bigDecimal: BigDecimal): BigDecimal = bigDecimal * -1

  def shutterSpeeds(standard: XMPSettings, image: XMPSettings): Option[BigDecimal] = {
    if (bdEquals(standard.shutterSpeed, image.shutterSpeed)) {
      None
    } else if (standard.shutterSpeed > image.shutterSpeed) {
      Some(neg(equations.shutterSpeeds(standard.shutterSpeed, image.shutterSpeed)))
    } else {
      Some(equations.shutterSpeeds(image.shutterSpeed, standard.shutterSpeed))
    }
  }

  def apertures(standard: XMPSettings, image: XMPSettings): Option[BigDecimal] = {
    if (bdEquals(standard.aperture, image.aperture)) None
    else Some(equations.apertures(standard.aperture, image.aperture))
  }

  def ISOs(standard: XMPSettings, image: XMPSettings): Option[BigDecimal] = {
    if (standard.iso.equals(image.iso)) {
      None
    } else if (standard.iso > image.iso) {
      Some(neg(equations.ISOs(standard.iso, image.iso)))
    } else {
      Some(equations.ISOs(image.iso, standard.iso))
    }
  }
}

object ExposureBias {
  def apply(equations: Equations): ExposureBias = new ExposureBias(equations)
}

class RampMirrorPrevious(exposureBias: ExposureBias) {
  def ramp(standard: XMP, image: XMP): XMP = {
    val shutterBias = exposureBias.shutterSpeeds(standard.settings, image.settings)
    val apertureBias = exposureBias.apertures(standard.settings, image.settings)
    val isoBias = exposureBias.ISOs(standard.settings, image.settings)

    val exposure = standard.settings.exposureBias
    val rampedExposure = expoAdd(expoAdd(expoAdd(exposure, shutterBias), apertureBias), isoBias)

    val rampedSettings = image.settings.copy(exposureBias = rampedExposure)
    image.copy(settings = rampedSettings)
  }

  private def expoAdd(expo: BigDecimal, addition: Option[BigDecimal]): BigDecimal = addition match {
    case None => expo
    case Some(bd) => expo + bd
  }
}

object RampMirrorPrevious {
  def apply(exposureBias: ExposureBias): RampMirrorPrevious = new RampMirrorPrevious(exposureBias)
}

class RampAverageWindow(avgCount: Int) {

  def ramp(images: List[XMP]): Unit = {

  }
}