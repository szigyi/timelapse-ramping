package hu.szigyi.timelapse.ramping.algo.ramp

import breeze.linalg.DenseVector
import com.typesafe.scalalogging.LazyLogging
import hu.szigyi.timelapse.ramping.algo.ev.EV
import hu.szigyi.timelapse.ramping.model.XMP

import breeze.interpolation._

class Interpolator(ev: EV, rampHelper: RampHelper) extends LazyLogging {


  def buildInterpolator(xmps: Seq[XMP]): LinearInterpolator[Double] = {
    val EVs: Seq[BigDecimal] = xmps.map(xmp => rampHelper.toEV(xmp))
    val resids: Seq[Double] = rampHelper.residuals(EVs).map(_.toDouble)
    val indices: Seq[Double] = (0 to xmps.size - 1).map(_.toDouble)

    val x: DenseVector[Double] = DenseVector(indices: _*)
    val y: DenseVector[Double] = DenseVector(resids: _*)
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