package hu.szigyi.timelapse.ramping.algo

import hu.szigyi.timelapse.ramping.model.{XMP, XMPSettings}


abstract class Ramp {

  protected def expoAdd(expo: BigDecimal, addition: Option[BigDecimal]): BigDecimal = addition match {
    case None => expo
    case Some(bd) => expo + bd
  }
}

abstract class RampByPairs extends Ramp {
  def rampExposure(base: XMP, current: XMP): BigDecimal
}

abstract class RampBySeq(ev: EV) extends Ramp {
  private def xmpToEV(xmp: XMP): BigDecimal = ev.EV(xmp.settings.aperture, xmp.settings.shutterSpeed, xmp.settings.iso)
  def rampExposure(xmps: XMP*): BigDecimal
  def EV(xmp: XMP): BigDecimal = xmpToEV(xmp)
}

class MirrorPrevious(EVdiff: EVDifference) extends RampByPairs {
  override def rampExposure(base: XMP, current: XMP): BigDecimal = {
    val shutterBias = EVdiff.fromShutterSpeeds(base.settings, current.settings)
    val apertureBias = EVdiff.fromApertures(base.settings, current.settings)
    val isoBias = EVdiff.fromISOs(base.settings, current.settings)

    // Copying forward the standard's exposure
    val baseExposure = base.settings.exposure
    val rampedExposure = expoAdd(expoAdd(expoAdd(baseExposure, shutterBias), apertureBias), isoBias)
    rampedExposure
  }
}
object MirrorPrevious {
  def apply(EVdiff: EVDifference): MirrorPrevious = new MirrorPrevious(EVdiff)
}

class MirrorAndSqueeze(EVdiff: EVDifference) extends RampByPairs {
  private val squeeze = 0.9
  override def rampExposure(base: XMP, current: XMP): BigDecimal = {
    val shutterBias = EVdiff.fromShutterSpeeds(base.settings, current.settings)
    val apertureBias = EVdiff.fromApertures(base.settings, current.settings)
    val isoBias = EVdiff.fromISOs(base.settings, current.settings)

    // Copying forward the base's exposure
    val baseExposure = base.settings.exposure
    val rampedExposure = expoAdd(expoAdd(expoAdd(baseExposure, shutterBias), apertureBias), isoBias)
    rampedExposure * squeeze
  }
}
object MirrorAndSqueeze {
  def apply(EVdiff: EVDifference): MirrorAndSqueeze = new MirrorAndSqueeze(EVdiff)
}

class AverageWindow(ev: EV) extends RampBySeq(ev) {

  override def rampExposure(xmps: XMP*): BigDecimal = {
    val size = xmps.size
    val EVs = xmps.map(xmp => ev.EV(xmp.settings.aperture, xmp.settings.shutterSpeed, xmp.settings.iso))
    EVs.sum / size
  }
}
object AverageWindow {
  def apply(ev: EV): AverageWindow = new AverageWindow(ev)
}

class Interpolation(ev: EV) extends RampBySeq(ev) {
  override def rampExposure(xmps: XMP*): BigDecimal = ???
}
object Interpolation {
  def apply(ev: EV): Interpolation = new Interpolation(ev)
}