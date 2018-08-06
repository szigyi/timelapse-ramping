package hu.szigyi.timelapse.ramping.algo.ramp

import hu.szigyi.timelapse.ramping.algo.ev.EVDifference
import hu.szigyi.timelapse.ramping.model.XMP

class MirrorAndSqueeze(EVdiff: EVDifference, rampHelper: RampHelper) {

  private val squeeze = 0.9

  def rampExposure(base: XMP, current: XMP): BigDecimal = {
    val shutterBias = EVdiff.fromShutterSpeeds(base.settings, current.settings)
    val apertureBias = EVdiff.fromApertures(base.settings, current.settings)
    val isoBias = EVdiff.fromISOs(base.settings, current.settings)

    // Copying forward the base's exposure
    val baseExposure = base.settings.exposure
    val rampedExposure = rampHelper.expoAdd(rampHelper.expoAdd(rampHelper.expoAdd(baseExposure, shutterBias), apertureBias), isoBias)
    rampedExposure * squeeze
  }
}
object MirrorAndSqueeze {
  def apply(EVdiff: EVDifference, rampHelper: RampHelper): MirrorAndSqueeze = new MirrorAndSqueeze(EVdiff, rampHelper)
}
