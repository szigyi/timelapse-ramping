package hu.szigyi.timelapse.ramping.algo.ramp

import breeze.linalg.DenseVector
import com.typesafe.scalalogging.LazyLogging

import breeze.interpolation._

class Interpolator(rampHelper: RampHelper) extends LazyLogging {

  private def prepareForEVInterpolation(data: Seq[BigDecimal]): Seq[(Int, BigDecimal)] = {
    val relativeChanges: Seq[BigDecimal] = rampHelper.relativeChangesInData(data)
    val squashedChanges: Seq[(Int, BigDecimal)] = rampHelper.removeNonBoundaryZeros(relativeChanges)
    val enhancedChanges: Seq[(Int, BigDecimal)] = rampHelper.toCutOffSequence(squashedChanges)
    val shiftedChanges: Seq[(Int, BigDecimal)] = rampHelper.shiftSequenceIndices(enhancedChanges)
    val negated: Seq[(Int, BigDecimal)] = rampHelper.negate(shiftedChanges)
    negated
  }

  private def prepareForWBInterpolation(data: Seq[BigDecimal]): Seq[(Int, BigDecimal)] = {
    val relativeChanges: Seq[BigDecimal] = rampHelper.relativeChangesInData(data)
    val squashedChanges: Seq[(Int, BigDecimal)] = rampHelper.removeNonBoundaryZeros(relativeChanges)
    val shiftedChanges: Seq[(Int, BigDecimal)] = rampHelper.shiftSequenceIndices(squashedChanges)
    val absoluteChanges: Seq[(Int, BigDecimal)] = rampHelper.toAbsolute(shiftedChanges.toList, Nil, data.head)
    absoluteChanges
  }

  def buildEVInterpolator(EVs: Seq[BigDecimal]): LinearInterpolator[Double] = {
    val preparedEVs: Seq[(Int, BigDecimal)] = prepareForEVInterpolation(EVs)

    val x: DenseVector[Double] = DenseVector(preparedEVs.map(_._1.toDouble): _*)
    val y: DenseVector[Double] = DenseVector(preparedEVs.map(_._2.toDouble): _*)
    LinearInterpolator(x, y)
  }

  def buildWBInterpolator(WBs: Seq[Int]): LinearInterpolator[Double] = {
    val preparedWBs: Seq[(Int, Int)] = prepareForWBInterpolation(WBs.map(wb => BigDecimal(wb)))
      .map((wb: (Int, BigDecimal)) => (wb._1, wb._2.toInt))

    val x: DenseVector[Double] = DenseVector(preparedWBs.map(_._1.toDouble): _*)
    val y: DenseVector[Double] = DenseVector(preparedWBs.map(_._2.toDouble): _*)
    LinearInterpolator(x, y)
  }

  def interpolate(index: Int)(f: LinearInterpolator[Double]): BigDecimal = {
    val interpolated = f(index)
    BigDecimal(interpolated)
  }
}

object Interpolator {
  def apply(rampHelper: RampHelper): Interpolator = new Interpolator(rampHelper)
}