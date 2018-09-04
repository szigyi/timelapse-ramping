package hu.szigyi.timelapse.ramping.algo.ramp

import breeze.linalg.DenseVector
import com.typesafe.scalalogging.LazyLogging

import breeze.interpolation._

class Interpolator(rampHelper: RampHelper) extends LazyLogging {

  def buildInterpolator(EVs: Seq[BigDecimal]): LinearInterpolator[Double] = {
    val changesInEVs: Seq[BigDecimal] = rampHelper.relativeChangesInEVs(EVs)
    val squashedChangesInEVs: Seq[(Int, BigDecimal)] = rampHelper.removeNonBoundaryZeros(changesInEVs)
    val enhancedChangesInEVs: Seq[(Int, BigDecimal)] = rampHelper.toCutOffSequence(squashedChangesInEVs)
    val shiftedChangesInEVs: Seq[(Int, BigDecimal)] = rampHelper.shiftSequenceIndices(enhancedChangesInEVs)

    val x: DenseVector[Double] = DenseVector(shiftedChangesInEVs.map(_._1.toDouble): _*)
    val y: DenseVector[Double] = DenseVector(shiftedChangesInEVs.map(_._2.toDouble): _*)
    LinearInterpolator(x, y)
  }

  def rampExposure(index: Int)(f: LinearInterpolator[Double]): BigDecimal = {
    val interpolatedEV = f(index)
    BigDecimal(interpolatedEV)
  }
}

object Interpolator {
  def apply(rampHelper: RampHelper): Interpolator = new Interpolator(rampHelper)
}