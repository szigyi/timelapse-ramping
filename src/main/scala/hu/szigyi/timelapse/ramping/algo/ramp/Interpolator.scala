package hu.szigyi.timelapse.ramping.algo.ramp

import breeze.linalg.DenseVector
import com.typesafe.scalalogging.LazyLogging

import breeze.interpolation._

class Interpolator(rampHelper: RampHelper) extends LazyLogging {

  private def prepareSequenceForInterpolation(data: Seq[BigDecimal]): Seq[(Int, BigDecimal)] = {
    val relativeChanges: Seq[BigDecimal] = rampHelper.relativeChangesInData(data)
    val squashedChanges: Seq[(Int, BigDecimal)] = rampHelper.removeNonBoundaryZeros(relativeChanges)
    val enhancedChanges: Seq[(Int, BigDecimal)] = rampHelper.toCutOffSequence(squashedChanges)
    val shiftedChanges: Seq[(Int, BigDecimal)] = rampHelper.shiftSequenceIndices(enhancedChanges)
    shiftedChanges
  }

  def buildEVInterpolator(EVs: Seq[BigDecimal]): LinearInterpolator[Double] = {
    val preparedEVs: Seq[(Int, BigDecimal)] = prepareSequenceForInterpolation(EVs)

    val x: DenseVector[Double] = DenseVector(preparedEVs.map(_._1.toDouble): _*)
    val y: DenseVector[Double] = DenseVector(preparedEVs.map(_._2.toDouble): _*)
    LinearInterpolator(x, y)
  }

  def buildWBInterpolator(WBs: Seq[Int]): LinearInterpolator[Int] = {
    def toAbsolute(remaining: List[(Int, Int)], acc: List[(Int, Int)], prevOriginalWB: Int): Seq[(Int, Int)] = remaining match {
      case Nil => acc.toSeq
      case head :: tail => {
        val absoluteCurrentWB = head._2 + prevOriginalWB
        val newAcc = (head._1, absoluteCurrentWB) +: acc
        toAbsolute(tail, newAcc, absoluteCurrentWB)
      }
    }

    val preparedEVs: Seq[(Int, Int)] = prepareSequenceForInterpolation(WBs.map(wb => BigDecimal(wb)))
      .map((wb: (Int, BigDecimal)) => (wb._1, wb._2.toInt))
    val absoluteValuesOfWBs: Seq[(Int, Int)] = toAbsolute(preparedEVs.toList, Nil, WBs.head)

    val x: DenseVector[Int] = DenseVector(absoluteValuesOfWBs.map(_._1.toInt): _*)
    val y: DenseVector[Int] = DenseVector(absoluteValuesOfWBs.map(_._2.toInt): _*)
    LinearInterpolator(x, y)
  }

  def interpolateBigDecimal(index: Int)(f: LinearInterpolator[Double]): BigDecimal = {
    val interpolated = f(index)
    BigDecimal(interpolated)
  }

  def interpolateInt(index: Int)(f: LinearInterpolator[Int]): Int = {
    val interpolated = f(index)
    interpolated
  }
}

object Interpolator {
  def apply(rampHelper: RampHelper): Interpolator = new Interpolator(rampHelper)
}