package hu.szigyi.timelapse.ramping.algo.ramp

import hu.szigyi.timelapse.ramping.algo.ev.EV
import org.scalatest.{Matchers, Outcome, fixture}

class InterpolatorSpec extends fixture.FunSpec with Matchers {
  override type FixtureParam = Interpolator

  override protected def withFixture(test: OneArgTest): Outcome = test(Interpolator(RampHelper(EV())))

  describe("Interpolator test") {
    it("should return a real life example result") { int =>
      val EVs = Seq(
        BigDecimal("0"),
        BigDecimal("0"),
        BigDecimal("2"),
        BigDecimal("2"),
        BigDecimal("2"),
        BigDecimal("3"),
        BigDecimal("3"),
        BigDecimal("3"),
        BigDecimal("3"),
        BigDecimal("5"),
        BigDecimal("5")
      )
      val expected = Seq(
        BigDecimal("0"),
        BigDecimal("-2"),
        BigDecimal("0"),
        BigDecimal("-0.5"),
        BigDecimal("-1"),
        BigDecimal("0"),
        BigDecimal("-0.6666666666666666"),
        BigDecimal("-1.3333333333333333"),
        BigDecimal("-2"),
        BigDecimal("0"),
        BigDecimal("0")
      )

      val f = int.buildInterpolator(EVs)
      val indicesOfXMPs = (0 to EVs.size - 1)
      val result = indicesOfXMPs.map(index => int.rampExposure(index)(f))

      result shouldEqual expected
    }
  }
}
