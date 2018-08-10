package hu.szigyi.timelapse.ramping.algo.ramp

import breeze.linalg.DenseVector
import com.typesafe.scalalogging.LazyLogging
import hu.szigyi.timelapse.ramping.algo.ev.EV
import hu.szigyi.timelapse.ramping.model.XMP

import breeze.interpolation._

class Interpolator(ev: EV, rampHelper: RampHelper) extends LazyLogging {

  def buildInterpolator(xmps: Seq[XMP]): LinearInterpolator[Double] = {
    val EVs: Seq[BigDecimal] = xmps.map(xmp => rampHelper.toEV(xmp))
    val changesInEVs: Seq[BigDecimal] = rampHelper.relativeChangesInEVs(EVs)
    val squashedChangesInEVs: Seq[(Int, BigDecimal)] = rampHelper.removeNotBoundaryZeros(changesInEVs)
    val enhancedChangesInEVs: Seq[(Int, BigDecimal)] = rampHelper.addZeroRightBetweenChanges(squashedChangesInEVs)

    val x: DenseVector[Double] = DenseVector(enhancedChangesInEVs.map(_._1.toDouble): _*)
    val y: DenseVector[Double] = DenseVector(enhancedChangesInEVs.map(_._2.toDouble): _*)
    LinearInterpolator(x, y)
  }

  def rampExposure(index: Int)(f: LinearInterpolator[Double]): BigDecimal = {
    val interpolatedEV = f(index)
    BigDecimal(interpolatedEV)
  }
}

object Interpolator {
  def apply(ev: EV, rampHelper: RampHelper): Interpolator = new Interpolator(ev, rampHelper)
}