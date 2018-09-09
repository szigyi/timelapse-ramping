package hu.szigyi.timelapse.ramping.algo.ramp

import hu.szigyi.timelapse.ramping.algo.ev.EV
import hu.szigyi.timelapse.ramping.model.EXIF

class AverageWindow(ev: EV) {

  def rampExposure(xmps: Seq[EXIF]): BigDecimal = {
    val size = xmps.size
    val EVs = xmps.map(xmp => ev.EV(xmp.settings.aperture, xmp.settings.shutterSpeed, xmp.settings.iso))
    EVs.sum / size
  }
}
object AverageWindow {
  def apply(ev: EV): AverageWindow = new AverageWindow(ev)
}
