package hu.szigyi.timelapse.ramping.algo

import breeze.interpolation.LinearInterpolator
import breeze.linalg.DenseVector
import cc.redberry.rings.poly.univar.{UnivariateInterpolation, UnivariatePolynomialZp64}
import com.typesafe.scalalogging.LazyLogging
import org.scalatest.FunSpec

class InterpolateTest extends FunSpec with LazyLogging {

  describe("Trying to understand Interpolation and rings") {

    it("linear interpolation test") {
      val x = DenseVector(0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0)
      val y = DenseVector(0.0, 0.8415, 0.9093, 0.1411, -0.7568, -0.9589, -0.2794)
      val f = LinearInterpolator(x ,y)

      assert(0.5252.equals(f(2.5)))
    }


//    it("how to read out data") {
//      val modulus = 173.toLong
//      val points: Array[Long] = Array(0, 10, 20, 30, 40, 50, 60)
//      val values: Array[Long] = Array(0, 8415, 9093, 1411, -7568, -9589, -2794)
//      val zp6: UnivariatePolynomialZp64 = UnivariateInterpolation.interpolateLagrange(modulus, points, values)
//      val l = zp6.evaluate(10)
//      logger.info(l.toString)
//    }

//    it("another try") {
//      import org.apache.commons.math3.fitting.PolynomialCurveFitter
//      import org.apache.commons.math3.fitting.WeightedObservedPoints
//      // Collect data.// Collect data.
//
//      val obs = new WeightedObservedPoints
//      obs.add(0, 0.0)
//      obs.add(1, 0.8415)
//      obs.add(2, 0.9093)
//      obs.add(3, 0.1411)
//      obs.add(4, -0.7568)
//      obs.add(5, -0.9589)
//      obs.add(6, -0.2794)
//
//      // Instantiate a third-degree polynomial fitter.
//      val fitter = PolynomialCurveFitter.create(5)
//
//      // Retrieve fitted parameters (coefficients of the polynomial function).
//      val coeff: Array[Double] = fitter.fit(obs.toList)
//      logger.info(coeff.mkString(", "))
////      logger.info(calc(coeff, 2.0).toString)
//      logger.info(calc(coeff, 2.5).toString)
////      logger.info(calc(coeff, 3.0).toString)
//
//    }

    def calc(coeff: Array[Double], x: Double): Double = {
      var f = 0.0
      val degrees = coeff.size to 1 by -1
      coeff.zip(degrees).foreach(t => {
        val c = t._1
        val d = t._2
        f += c * Math.pow(x, d)
        logger.info(s"f($f) += c($c) * x($x)^d($d) => (${Math.pow(x, d)})")
      })
      f
    }
  }
}
