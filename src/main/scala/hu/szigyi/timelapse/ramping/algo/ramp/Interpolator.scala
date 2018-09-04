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
    shiftedChanges
  }

  def buildEVInterpolator(EVs: Seq[BigDecimal]): LinearInterpolator[Double] = {
    val preparedEVs: Seq[(Int, BigDecimal)] = prepareForEVInterpolation(EVs)

    val x: DenseVector[Double] = DenseVector(preparedEVs.map(_._1.toDouble): _*)
    val y: DenseVector[Double] = DenseVector(preparedEVs.map(_._2.toDouble): _*)
    LinearInterpolator(x, y)
  }

  def buildWBInterpolator(WBs: Seq[Int]): LinearInterpolator[Int] = {
    val preparedEVs: Seq[(Int, Int)] = prepareForWBInterpolation(WBs.map(wb => BigDecimal(wb)))
      .map((wb: (Int, BigDecimal)) => (wb._1, wb._2.toInt))
    val absoluteValuesOfWBs: Seq[(Int, Int)] = rampHelper.toAbsolute(preparedEVs.toList, Nil, WBs.head)
    logger.info(s"${absoluteValuesOfWBs.toList}")

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