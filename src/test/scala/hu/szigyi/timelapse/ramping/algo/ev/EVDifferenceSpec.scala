package hu.szigyi.timelapse.ramping.algo.ev

import hu.szigyi.timelapse.ramping.model.FromCamera
import hu.szigyi.timelapse.ramping.testutil.BigDecimalTestExtension._
import org.scalatest.fixture.FunSpec
import org.scalatest.{Matchers, Outcome}

class EVDifferenceSpec extends FunSpec with Matchers {

  override type FixtureParam = EVDifference

  override protected def withFixture(test: OneArgTest): Outcome = test(EVDifference(EV()))

  private def xmpSS(ss: BigDecimal): FromCamera = FromCamera(100, ss, BigDecimal("1.0"), BigDecimal("0.0"), 3400)
  private def xmpA(a: BigDecimal): FromCamera = FromCamera(100, BigDecimal("0.25"), a, BigDecimal("0.0"), 3400)
  private def xmpI(i: Int): FromCamera = FromCamera(i, BigDecimal("0.25"), BigDecimal("1.0"), BigDecimal("0.0"), 3400)

  describe("EV difference based on Shutter Speeds") {
    it("should return None when shutter speeds are equal aka. not changed") { EVdiff =>
      val expected = None
      val baseXMP = xmpSS(BigDecimal("0.25"))
      val currentXMP = xmpSS(BigDecimal("0.25"))

      val maybeDiff = EVdiff.fromShutterSpeeds(baseXMP, currentXMP)

      maybeDiff shouldEqual expected
    }

    it("should return 1 stop when base's shutter speeds is bigger then current") { EVdiff =>
      val baseXMP = xmpSS(BigDecimal("0.5")) // base brighter
      val currentXMP = xmpSS(BigDecimal("0.25")) // current darker
      val expected = Some(BigDecimal("1")) // brightening

      val maybeDiff = EVdiff.fromShutterSpeeds(baseXMP, currentXMP)

      maybeDiff shouldEqual expected
    }

    it("should return -1 stop when base's shutter speeds is smaller then current") { EVdiff =>
      val baseXMP = xmpSS(BigDecimal("0.25")) // base darker
      val currentXMP = xmpSS(BigDecimal("0.5")) // current brighter
      val expected = Some(BigDecimal("-1")) // darkening

      val maybeDiff = EVdiff.fromShutterSpeeds(baseXMP, currentXMP)

      maybeDiff shouldEqual expected
    }
  }

  describe("EV difference based on Apertures") {
    it("should return None when apertures are equal aka. not changed") { EVdiff =>
      val expected = None
      val baseXMP = xmpA(BigDecimal("2.8"))
      val currentXMP = xmpA(BigDecimal("2.8"))

      val maybeDiff = EVdiff.fromApertures(baseXMP, currentXMP)

      maybeDiff shouldEqual expected
    }

    it("should return 1 stop when base's aperture is smaller then current") { EVdiff =>
      val baseXMP = xmpA(BigDecimal("2.8")) // base brighter
      val currentXMP = xmpA(BigDecimal("4")) // current darker
      val expected = BigDecimal("1", roundingMathContext) // brightening

      val maybeDiff = EVdiff.fromApertures(baseXMP, currentXMP)

      maybeDiff match {
        case Some(bd) => bd.round(roundingMathContext) shouldEqual expected
        case None => fail()
      }
    }

    it("should return -1 stop when base's aperture is bigger then current") { EVdiff =>
      val baseXMP = xmpA(BigDecimal("4")) // base darker
      val currentXMP = xmpA(BigDecimal("2.8")) // current brighter
      val expected = BigDecimal("-1", roundingMathContext) // darkening

      val maybeDiff = EVdiff.fromApertures(baseXMP, currentXMP)

      maybeDiff match {
        case Some(bd) => bd.round(roundingMathContext) shouldEqual expected
        case None => fail()
      }
    }
  }

  describe("EV difference based on ISOs") {
    it("should return None when ISOs are equal aka. not changed") { EVdiff =>
      val expected = None
      val baseXMP = xmpI(100)
      val currentXMP = xmpI(100)

      val maybeDiff = EVdiff.fromISOs(baseXMP, currentXMP)

      maybeDiff shouldEqual expected
    }

    it("should return 1 stop when base's ISO is smaller then current") { EVdiff =>
      val baseXMP = xmpI(200) // base brighter
      val currentXMP = xmpI(100) // current darker
      val expected = Some(BigDecimal("1")) // brightening

      val maybeDiff = EVdiff.fromISOs(baseXMP, currentXMP)

      maybeDiff shouldEqual expected
    }

    it("should return -1 stop when base's ISO is bigger then current") { EVdiff =>
      val baseXMP = xmpI(100) // base darker
      val currentXMP = xmpI(200) // current brighter
      val expected = Some(BigDecimal("-1")) // darkening

      val maybeDiff = EVdiff.fromISOs(baseXMP, currentXMP)

      maybeDiff shouldEqual expected
    }
  }
}
