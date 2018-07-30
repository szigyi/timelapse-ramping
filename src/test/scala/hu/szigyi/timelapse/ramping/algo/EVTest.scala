package hu.szigyi.timelapse.ramping.algo


import hu.szigyi.timelapse.ramping.testutil.BigDecimalTestExtension._

import BigDecimal._
import org.scalatest.{Matchers, Outcome, fixture}

class EVTest extends fixture.FunSpec with Matchers {

  override type FixtureParam = EV

  override protected def withFixture(test: OneArgTest): Outcome = test(EV())

  describe("Shutter speed") {
    it("result uses default math context") { eq =>
      val base = BigDecimal("1")
      val current = BigDecimal("0.5")

      val EVdiff = eq.shutterSpeeds(base, current)

      EVdiff.mc shouldEqual defaultMathContext
    }

    it("shutter speeds 1 stop away - current should be brighter") { eq =>
      val base = BigDecimal("1") // base brighter
      val current = BigDecimal("0.5") // current darker
      val expected = BigDecimal("1") // brightening current

      val EVdiff = eq.shutterSpeeds(base, current)

      EVdiff shouldEqual expected
    }

    it("shutter speeds -3 stops away - current should be darker") { eq =>
      val base = BigDecimal("0.25") // base darker
      val current = BigDecimal("2") // current brighter
      val expected = BigDecimal("-3") // darkening current

      val EVdiff = eq.shutterSpeeds(base, current)

      EVdiff shouldEqual expected
    }
  }

  describe("Aperture (rounding result to precision 1)") {
    it("result uses default math context") { eq =>
      val base = BigDecimal("2.8")
      val current = BigDecimal("8")

      val EVdiff = eq.shutterSpeeds(base, current)

      EVdiff.mc shouldEqual defaultMathContext
    }

    it("aperture 1 stop away - current should be brighter") { eq =>
      val base = BigDecimal("1") // base brighter
      val current = BigDecimal("1.4") // current darker
      val expected = BigDecimal("1") // brightening

      val EVdiff = eq.apertures(base, current)

      EVdiff.round(roundingMathContext) shouldEqual expected
    }

    it("aperture -3 stops away - current should be darker") { eq =>
      val base = BigDecimal("11") // base darker
      val current = BigDecimal("4") // current brighter
      val expected = BigDecimal("-3") // darkening

      val EVdiff = eq.apertures(base, current)

      EVdiff.round(roundingMathContext) shouldEqual expected
    }
  }

  describe("ISO") {
    it("result uses default math context") { eq =>
      val base = 100
      val current = 200

      val EVdiff = eq.ISOs(base, current)

      EVdiff.mc shouldEqual defaultMathContext
    }

    it("ISO 1 stop away - current should be brighter") { eq =>
      val base = 200 // base brighter
      val current = 100 // current darker
      val expected = BigDecimal("1") // brightening

      val EVdiff = eq.ISOs(base, current)

      EVdiff.round(roundingMathContext) shouldEqual expected
    }

    it("ISO -3 stops away - current should be darker") { eq =>
      val base = 100 // base darker
      val current = 800 // current brighter
      val expected = BigDecimal("-3") // darkening

      val EVdiff = eq.ISOs(base, current)

      EVdiff.round(roundingMathContext) shouldEqual expected
    }
  }

}
