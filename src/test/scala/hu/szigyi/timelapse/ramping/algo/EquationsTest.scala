package hu.szigyi.timelapse.ramping.algo


import java.math.{MathContext, RoundingMode}

import BigDecimal._
import org.scalatest.{Matchers, Outcome, fixture}

class EquationsTest extends fixture.FunSpec with Matchers {

  val roundingMathContext = new MathContext(1, RoundingMode.HALF_EVEN)

  override type FixtureParam = Equations

  override protected def withFixture(test: OneArgTest): Outcome = test(Equations())

  describe("Shutter speed") {
    it("result uses default math context") { eq =>
      val ss1 = BigDecimal("1")
      val ss2 = BigDecimal("0.5")

      val EVdiff = eq.shutterSpeeds(ss1, ss2)

      EVdiff.mc shouldEqual defaultMathContext
    }

    it("shutter speeds 1 stop away") { eq =>
      val expected = BigDecimal("1.0", defaultMathContext)
      val ss1 = BigDecimal("1")
      val ss2 = BigDecimal("0.5")

      val EVdiff = eq.shutterSpeeds(ss1, ss2)

      EVdiff shouldEqual expected
    }

    it("shutter speeds -3 stops away") { eq =>
      val expected = BigDecimal("-3.0", defaultMathContext)
      val ss1 = BigDecimal("0.25")
      val ss2 = BigDecimal("2")

      val EVdiff = eq.shutterSpeeds(ss1, ss2)
      EVdiff shouldEqual expected
    }
  }

  describe("Aperture (rounding result to precision 1)") {
    it("result uses default math context") { eq =>
      val a1 = BigDecimal("2.8")
      val a2 = BigDecimal("8")

      val EVdiff = eq.shutterSpeeds(a1, a2)

      EVdiff.mc shouldEqual defaultMathContext
    }

    it("aperture 1 stop away") { eq =>
      val expected = BigDecimal("-1.0")
      val a1 = BigDecimal("1")
      val a2 = BigDecimal("1.4")

      val EVdiff = eq.apertures(a1, a2)

      EVdiff.round(roundingMathContext) shouldEqual expected
    }

    it("aperture 3 stops away") { eq =>
      val expected = BigDecimal("-3.0")
      val a1 = BigDecimal("4.0")
      val a2 = BigDecimal("11.0")

      val EVdiff = eq.apertures(a1, a2)

      EVdiff.round(roundingMathContext) shouldEqual expected
    }
  }

  describe("ISO") {
    it("result uses default math context") { eq =>
      val i1 = 100
      val i2 = 200

      val EVdiff = eq.ISOs(i1, i2)

      EVdiff.mc shouldEqual defaultMathContext
    }

    it("ISO 1 stop away") { eq =>
      val expected = BigDecimal("-1.0")
      val i1 = 100
      val i2 = 200

      val EVdiff = eq.ISOs(i1, i2)

      EVdiff.round(roundingMathContext) shouldEqual expected
    }

    it("ISO 3 stops away") { eq =>
      val expected = BigDecimal("-3.0")
      val i1 = 100
      val i2 = 800

      val EVdiff = eq.ISOs(i1, i2)

      EVdiff.round(roundingMathContext) shouldEqual expected
    }
  }

}
