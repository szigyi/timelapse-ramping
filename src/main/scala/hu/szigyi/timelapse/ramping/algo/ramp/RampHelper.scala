package hu.szigyi.timelapse.ramping.algo.ramp

import hu.szigyi.timelapse.ramping.algo.ev.EV
import hu.szigyi.timelapse.ramping.model.XMP

import scala.math.BigDecimal
import hu.szigyi.timelapse.ramping.math.BigDecimalDecorator._

class RampHelper(ev: EV) {

  private val slidingWindow = 2
  private val ZERO = BigDecimal("0.0")

  // TODO refactor, it is not functional, it smells!!
  def expoAdd(expo: BigDecimal, addition: Option[BigDecimal]): BigDecimal = addition match {
    case None => expo
    case Some(bd) => expo + bd
  }

  def indiciesOf[T](seq: Seq[T]): Seq[Int] = (0 to seq.size - 1)

  /**
    * Input EVs
    * |     ___/-----
    * |    /
    * ____/
    * 0.   1.   2.
    *
    * Output sequence
    * |
    * |    /\
    * ____/ \___/\____
    * 0.   1.   2.
    *
    * @param EVs
    * @return sequence of zeros and changes
    */
  def relativeChangesInEVs(EVs: Seq[BigDecimal]): Seq[BigDecimal] = {
    val extendedEVs = EVs.head +: EVs
    extendedEVs.sliding(slidingWindow).map((window: Seq[BigDecimal]) => window.head - window(1)).toSeq
  }

  /**
    * Input sequence
    * |
    * |        /\
    * ________/ \_______/\_______
    * 0.  1.   2.   3.  4.
    *
    * Output sequence
    * |
    * |  /\
    * __/ \_/\_
    * 0. 2. 4.
    *
    * @param changesInEV
    * @return squashed changes in EV with original indices (removed not boundary zeros)
    */
  def removeNotBoundaryZeros(changesInEV: Seq[BigDecimal]): Seq[(Int, BigDecimal)] = {
    val firstElement = (0, changesInEV.head)
    val lastElement = (changesInEV.size - 1, changesInEV.last)
    var notZeros = indiciesOf(changesInEV).zip(changesInEV).filterNot(p => p._2 === ZERO)
    firstElement +: notZeros :+ lastElement
//
    if (notZeros.isEmpty || !notZeros.head.equals(firstElement)) {
      notZeros = firstElement +: notZeros
    }
    if (notZeros.size.equals(1) || !notZeros.last.equals(lastElement)) {
      notZeros = notZeros :+ lastElement
    }
    notZeros
  }

  def toEV(xmp: XMP): BigDecimal = ev.EV(xmp.settings.aperture, xmp.settings.shutterSpeed, xmp.settings.iso)

//  implicit class RichXMP(val xmp: XMP) extends AnyVal{
//    def toEV: BigDecimal = EV().EV(xmp.settings.aperture, xmp.settings.shutterSpeed, xmp.settings.iso)
//  }
}

object RampHelper {
  def apply(ev: EV): RampHelper = new RampHelper(ev)
}
