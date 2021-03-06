package hu.szigyi.timelapse.ramping.algo.ramp

import hu.szigyi.timelapse.ramping.algo.ev.EVDifference
import hu.szigyi.timelapse.ramping.model.XMP

class MirrorPrevious(EVdiff: EVDifference, rampHelper: RampHelper) {

  def rampExposure(base: XMP, current: XMP): BigDecimal = {
    val shutterBias = EVdiff.fromShutterSpeeds(base.settings, current.settings)
    val apertureBias = EVdiff.fromApertures(base.settings, current.settings)
    val isoBias = EVdiff.fromISOs(base.settings, current.settings)

    // Copying forward the standard's exposure
    val baseExposure = base.settings.exposure
    val rampedExposure = rampHelper.expoAdd(rampHelper.expoAdd(rampHelper.expoAdd(baseExposure, shutterBias), apertureBias), isoBias)
    rampedExposure
  }
}
object MirrorPrevious {
  def apply(EVdiff: EVDifference, rampHelper: RampHelper): MirrorPrevious = new MirrorPrevious(EVdiff, rampHelper)
}
