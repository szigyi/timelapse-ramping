package hu.szigyi.timelapse.ramping.algo.ramp

import hu.szigyi.timelapse.ramping.algo.ev.EV
import org.scalatest.{Matchers, Outcome, fixture}

class InterpolatorSpec extends fixture.FunSpec with Matchers {

  type B = scala.math.BigDecimal
  val B = scala.math.BigDecimal

  override type FixtureParam = Interpolator

  override protected def withFixture(test: OneArgTest): Outcome = test(Interpolator(RampHelper(EV())))

  describe("EV Interpolator test") {
    it("should return a real life example result") { int =>
      val EVs = Seq(
        B("0"),
        B("0"),
        B("2"),
        B("2"),
        B("2"),
        B("3"),
        B("3"),
        B("3"),
        B("3"),
        B("5"),
        B("5")
      )
      val expected = Seq(
        B("0"),
        B("-2"),
        B("0"),
        B("-0.5"),
        B("-1"),
        B("0"),
        B("-0.6666666666666666"),
        B("-1.3333333333333333"),
        B("-2"),
        B("0"),
        B("0")
      )

      val f = int.buildEVInterpolator(EVs)
      val indicesOfXMPs = (0 to EVs.size - 1)
      val result = indicesOfXMPs.map(index => int.interpolate(index)(f))

      result shouldEqual expected
    }
  }

  describe("Temperature Interpolator test") {
    it("should return a real life example result") { int =>
      val WBs = Seq(
        4300,
        4300,
        4300,
        4800,
        4800,
        4800,
        4800,
        5200,
        5200,
        2900
      )
      val expected = Seq(
        4300,
        4466,
        4633,
        4800,
        4900,
        5000,
        5100,
        5200,
        4050,
        2900
      )

      val f = int.buildTemperatureInterpolator(WBs)
      val indicesOfXMPs = (0 to WBs.size - 1)
      val result = indicesOfXMPs.map(index => (int.interpolate(index)(f)).toInt)

      result shouldEqual expected
    }
    it("long building up change") { int =>
      val WBs = Seq(
        2000,
        2000,
        2000,
        2000,
        2000,
        2000,
        2000,
        2000,
        2000,
        2000,
        3000,
        3000,
        3000
      )
      val expected = Seq(
        2000,
        2100,
        2200,
        2300,
        2400,
        2500,
        2600,
        2700,
        2800,
        2900,
        3000,
        3000,
        3000
      )

      val f = int.buildTemperatureInterpolator(WBs)
      val indicesOfXMPs = (0 to WBs.size - 1)
      val result = indicesOfXMPs.map(index => (int.interpolate(index)(f)).toInt)

      result shouldEqual expected
    }
  }
}
