package hu.szigyi.timelapse.ramping.algo.ramp

import hu.szigyi.timelapse.ramping.algo.ev.EV
import hu.szigyi.timelapse.ramping.model.XMP

import scala.math.{BigDecimal}
import hu.szigyi.timelapse.ramping.math.BigDecimalDecorator._


class RampHelper(ev: EV) {

  private val slidingWindow = 2
  private val ZERO = BigDecimal("0.0")

  // TODO refactor, it is not functional, it smells!!
  def expoAdd(expo: BigDecimal, addition: Option[BigDecimal]): BigDecimal = addition match {
    case None => expo
    case Some(bd) => expo + bd
  }

  /**
    * Input Data
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
    * @param data
    * @return sequence of zeros and changes
    */
  def relativeChangesInData(data: Seq[BigDecimal]): Seq[BigDecimal] = {
    val extendedData = data.head +: data
    extendedData
      .sliding(slidingWindow)
      .map((window: Seq[BigDecimal]) => window.head - window(1))
      .toSeq
  }

  /**
    * Removing all the zeros from the sequence. Adding the original indices to the remaining items.
    *
    * Input sequence
    * |        *
    * |                    *
    * |*   *       *   *       *
    *  0.  1.  2.  3.  4.  5.  6.
    *
    * Output sequence
    * |   *
    * |      *
    * |*        *
    *  0. 2. 5. 6.
    *
    * @param changesInEV
    * @return squashed changes in EV with original indices (removed all zeros)
    */
  def removeNonBoundaryZeros(changesInEV: Seq[BigDecimal]): Seq[(Int, BigDecimal)] = {
    val firstElement = (0, changesInEV.head)
    val lastElement = (changesInEV.size - 1, changesInEV.last)

    val nonBoundarySeq: Seq[BigDecimal] = changesInEV.tail.dropRight(1)
    val zerolessSeq: Seq[(Int, BigDecimal)] = (1 to nonBoundarySeq.size).zip(nonBoundarySeq).filterNot(_._2 === ZERO)

    firstElement +: zerolessSeq :+ lastElement
  }

  /**
    * It makes a Cut-Off sequence from a residual (changes) sequence.
    * Adds a zero element after every change.
    * If there is at least one missing index (gap) between the consecutive changes
    *
    * Input sequence
    * |   *
    * |      *
    * |*        *
    *  0. 2. 5. 6.
    *
    * Output sequence
    * |    *
    * |            *
    * |*       *       *
    *  0.  2.  3.  5.  6.
    *
    * @param nonZeros
    * @return
    */
  def toCutOffSequence(nonZeros: Seq[(Int, BigDecimal)]): Seq[(Int, BigDecimal)] = {
    def nextIndexOf(i: (Int, BigDecimal)): Int = i._1 + 1
    def areNeighbours(i1: (Int, BigDecimal), i2: (Int, BigDecimal)): Boolean = i2._1 - i1._1 == 1
    def isNeighbourOfLastElement(i1: (Int, BigDecimal), last: (Int, BigDecimal)): Boolean = areNeighbours(i1, last)

    if (nonZeros.size < 4) return nonZeros

    val nonBoundary = nonZeros.tail.dropRight(1)
    val nonBoundaryLastIsDuplicated = nonBoundary :+ nonBoundary.last

    val cutOff: Seq[(Int, BigDecimal)] = nonBoundaryLastIsDuplicated.sliding(slidingWindow).map((e: Seq[(Int, BigDecimal)]) => {
      val first = e.head
      val second = e(1)
      if (isNeighbourOfLastElement(first, nonZeros.last) || areNeighbours(first, second)) {
        Seq((first._1, first._2))
      } else {
        Seq(
          (first._1, first._2),
          (nextIndexOf(first), ZERO)
        )
      }
    }).toSeq.flatten

    val firstElement = nonZeros.head
    val lastElement = nonZeros.last
    firstElement +: cutOff :+ lastElement
  }

  def shiftSequenceIndices(seq: Seq[(Int, BigDecimal)]): Seq[(Int, BigDecimal)] = {
    val lastIndex = seq.last._1
    val tail = seq.tail
    val shiftedIndices = tail.map((e: (Int, BigDecimal)) => (e._1 - 1, e._2))

    if (shiftedIndices.head._1.equals(0)) shiftedIndices :+ (lastIndex, ZERO)
    else seq.head +: shiftedIndices :+ (lastIndex, ZERO)
  }

  // TODO extract to an XMP related part, it does not belong to here
  def calculateEV(xmp: XMP): BigDecimal = ev.EV(xmp.settings.aperture, xmp.settings.shutterSpeed, xmp.settings.iso)

//  implicit class RichXMP(val xmp: XMP) extends AnyVal{
//    def toEV: BigDecimal = EV().EV(xmp.settings.aperture, xmp.settings.shutterSpeed, xmp.settings.iso)
//  }
}

object RampHelper {
  def apply(ev: EV): RampHelper = new RampHelper(ev)
}
