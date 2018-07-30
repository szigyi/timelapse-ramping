package hu.szigyi.timelapse.ramping.algo

import hu.szigyi.timelapse.ramping.model.{XMP, XMPSettings}


abstract class Ramping(EVdiffAlgo: EVDifference) {

  protected def expoAdd(expo: BigDecimal, addition: Option[BigDecimal]): BigDecimal = addition match {
    case None => expo
    case Some(bd) => expo + bd
  }
}

abstract class RampingByPairs(EVdiffAlgo: EVDifference) extends Ramping(EVdiffAlgo) {
  def rampExposure(base: XMP, current: XMP): BigDecimal
}

abstract class RampingBySeq(EVdiffAlgo: EVDifference) extends Ramping(EVdiffAlgo) {
  def rampExposure(xmps: XMP*): BigDecimal
}

class MirrorPrevious(EVdiffAlgo: EVDifference) extends RampingByPairs(EVdiffAlgo) {
  override def rampExposure(base: XMP, current: XMP): BigDecimal = {
    val shutterBias = EVdiffAlgo.fromShutterSpeeds(base.settings, current.settings)
    val apertureBias = EVdiffAlgo.fromApertures(base.settings, current.settings)
    val isoBias = EVdiffAlgo.fromISOs(base.settings, current.settings)

    // Copying forward the standard's exposure
    val baseExposure = base.settings.exposure
    val rampedExposure = expoAdd(expoAdd(expoAdd(baseExposure, shutterBias), apertureBias), isoBias)
    rampedExposure
  }
}
object MirrorPrevious {
  def apply(EVdiffAlgo: EVDifference): MirrorPrevious = new MirrorPrevious(EVdiffAlgo)
}

class MirrorAndSqueeze(EVdiffAlgo: EVDifference) extends RampingByPairs(EVdiffAlgo) {
  private val squeeze = 0.9
  override def rampExposure(base: XMP, current: XMP): BigDecimal = {
    val shutterBias = EVdiffAlgo.fromShutterSpeeds(base.settings, current.settings)
    val apertureBias = EVdiffAlgo.fromApertures(base.settings, current.settings)
    val isoBias = EVdiffAlgo.fromISOs(base.settings, current.settings)

    // Copying forward the base's exposure
    val baseExposure = base.settings.exposure
    val rampedExposure = expoAdd(expoAdd(expoAdd(baseExposure, shutterBias), apertureBias), isoBias)
    rampedExposure * squeeze
  }
}
object MirrorAndSqueeze {
  def apply(EVdiffAlgo: EVDifference): MirrorAndSqueeze = new MirrorAndSqueeze(EVdiffAlgo)
}

class AverageWindow(EVdiffAlgo: EVDifference, avgCount: Int) extends RampingBySeq(EVdiffAlgo) {

  override def rampExposure(xmps: XMP*): BigDecimal = {

    EVdiffAlgo.fromShutterSpeeds
  }
}

class Interpolation(EVdiffAlgo: EVDifference) extends RampingBySeq(EVdiffAlgo) {
  override def rampExposure(xmps: XMP*): BigDecimal = ???
}