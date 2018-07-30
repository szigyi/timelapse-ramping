package hu.szigyi.timelapse.ramping.algo

import hu.szigyi.timelapse.ramping.model.XMPSettings

class EVDifference(equations: EV) {

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
object EVDifference {
  def apply(equations: EV): EVDifference = new EVDifference(equations)
}
