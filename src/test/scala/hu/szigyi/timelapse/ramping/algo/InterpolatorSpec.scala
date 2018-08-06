package hu.szigyi.timelapse.ramping.algo

import hu.szigyi.timelapse.ramping.algo.ramp.Interpolator
import org.scalatest.{Matchers, Outcome, fixture}


class InterpolatorSpec extends fixture.FunSpec with Matchers {

  override type FixtureParam = Interpolator

  override protected def withFixture(test: OneArgTest): Outcome = test(Interpolator(EV()))

  describe("Calculate Residuals first") {

    it("should return same size of seq") { ramp =>
      val evs = Seq(BigDecimal("0.0"), BigDecimal("0.0"), BigDecimal("2.0"))
      val size = evs.size

      val result = ramp.residuals(evs)

      result.size shouldEqual size
    }
    it("should duplicate the first element, therefore first element in the result should be ZERO") { ramp =>
      val evs = Seq(BigDecimal("5.3464578"), BigDecimal("1.0"), BigDecimal("2.0"))
      val expected = Seq(BigDecimal("0.00000000"), BigDecimal("4.3464578"), BigDecimal("-1.0"))

      val result = ramp.residuals(evs)

      result shouldEqual expected
    }
    it("should be ZERO if there is no change between to number") { ramp =>
      val evs = Seq(BigDecimal("0.0"), BigDecimal("0.0"), BigDecimal("1.5"))
      val expected = Seq(BigDecimal("0.00000000"), BigDecimal("0.00000000"), BigDecimal("-1.5"))

      val result = ramp.residuals(evs)

      result shouldEqual expected
    }
  }
}
