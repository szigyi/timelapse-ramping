package hu.szigyi.timelapse.ramping.algo.ramp

import hu.szigyi.timelapse.ramping.algo.ev.EV
import hu.szigyi.timelapse.ramping.model.XMP

import scala.math.BigDecimal

class RampHelper(ev: EV) {

  private val slidingWindow = 2

  // TODO refactor, it is not functional, it smells!!
  def expoAdd(expo: BigDecimal, addition: Option[BigDecimal]): BigDecimal = addition match {
    case None => expo
    case Some(bd) => expo + bd
  }

  def residuals(EVs: Seq[BigDecimal]): Seq[BigDecimal] = {
    val extendedEVs = EVs.head +: EVs
    extendedEVs.sliding(slidingWindow).map((window: Seq[BigDecimal]) => window.head - window(1)).toSeq
  }

  def toEV(xmp: XMP): BigDecimal = ev.EV(xmp.settings.aperture, xmp.settings.shutterSpeed, xmp.settings.iso)

//  implicit class RichXMP(val xmp: XMP) extends AnyVal{
//    def toEV: BigDecimal = EV().EV(xmp.settings.aperture, xmp.settings.shutterSpeed, xmp.settings.iso)
//  }
}

object RampHelper {
  def apply(ev: EV): RampHelper = new RampHelper(ev)
}
