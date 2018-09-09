package hu.szigyi.timelapse.ramping.algo.ramp

import hu.szigyi.timelapse.ramping.algo.ev.EV
import org.scalatest.{Matchers, Outcome, fixture}
import hu.szigyi.timelapse.ramping.testutil.BigDecimalTestExtension._

class RampHelperSpec extends fixture.FunSpec with Matchers {

  override type FixtureParam = RampHelper

  override protected def withFixture(test: OneArgTest): Outcome = test(RampHelper(EV()))

  describe("Calculate Changes in EVs") {

    it("should return same size of seq") { helper =>
      val evs = Seq(BigDecimal("0.0"), BigDecimal("0.0"), BigDecimal("2.0"))
      val size = evs.size

      val result = helper.relativeChangesInData(evs)

      result.size shouldEqual size
    }
    it("should duplicate the first element, therefore first element in the result should be ZERO") { helper =>
      val evs = Seq(BigDecimal("5.3464578"), BigDecimal("1.0"), BigDecimal("2.0"))
      val expected = Seq(BigDecimal("0.00000000"), BigDecimal("-4.3464578"), BigDecimal("1.0"))

      val result = helper.relativeChangesInData(evs)

      result shouldEqual expected
    }
    it("should be ZERO if there is no change between to number") { helper =>
      val evs = Seq(BigDecimal("0.0"), BigDecimal("0.0"), BigDecimal("1.5"))
      val expected = Seq(BigDecimal("0.00000000"), BigDecimal("0.00000000"), BigDecimal("1.5"))

      val result = helper.relativeChangesInData(evs)

      result shouldEqual expected
    }
  }

  describe("Remove not boundary zeros from Seq aka. Squeeze the sequence") {

    it("should return the same seq if every value is non zero and the same") { helper =>
      val value = BigDecimal("2.36834")
      val EVs = Seq(value, value, value, value, value)
      val expected = Seq((0, value), (1, value),(2, value), (3, value), (4, value))

      val squashed = helper.removeNonBoundaryZeros(EVs)

      squashed shouldEqual expected
    }

    it("should return the first and last zeros if every value is zero") { helper =>
      val value = "0.0"
      val EVs = Seq(BigDecimal(value), BigDecimal(value), BigDecimal(value), BigDecimal(value), BigDecimal(value))
      val expected = Seq(bd(0, value), bd(4, value))

      val squashed = helper.removeNonBoundaryZeros(EVs)

      squashed shouldEqual expected
    }

    it("should remove the zeros from the middle of the seq even if the first element is zero") { helper =>
      val EVs = Seq(BigDecimal("0.0"), BigDecimal("0.0"), BigDecimal("0.0"), BigDecimal("0.1"))
      val expected = Seq(bd(0, "0.0"), bd(3, "0.1"))

      val squashed = helper.removeNonBoundaryZeros(EVs)

      squashed shouldEqual expected
    }

    it("should remove the zeros from the middle of the seq even if the first element is not zero") { helper =>
      val EVs = Seq(BigDecimal("0.1"), BigDecimal("0.0"), BigDecimal("0.0"), BigDecimal("0.0"), BigDecimal("0.1"))
      val expected = Seq(bd(0, "0.1"), bd(4, "0.1"))

      val squashed = helper.removeNonBoundaryZeros(EVs)

      squashed shouldEqual expected
    }

    it("should remove the zeros from the end of the seq even if the last element is zero") { helper =>
      val EVs = Seq(BigDecimal("0.1"), BigDecimal("0.0"), BigDecimal("0.0"), BigDecimal("0.0"))
      val expected = Seq(bd(0, "0.1"), bd(3, "0.0"))

      val squashed = helper.removeNonBoundaryZeros(EVs)

      squashed shouldEqual expected
    }

    it("should remove the zeros from a real life sequence") { helper =>
      val EVs = Seq(BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("-5.64385619"), BigDecimal("0"), BigDecimal("0"), BigDecimal("1.321928095"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("-1"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("-1"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("-1"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("-1.321928095"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("-2"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("-0.5849625007"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("-1.415037499"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("-1"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("-1"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("-1.321928095"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"))
      val expected = Seq(
        bd(0, "0.0"),
        bd(105, "-5.64385619"),
        bd(108, "1.321928095"),
        bd(133, "-1"),
        bd(153, "-1"),
        bd(170, "-1"),
        bd(193, "-1.321928095"),
        bd(208, "-2"),
        bd(224, "-0.5849625007"),
        bd(239, "-1.415037499"),
        bd(259, "-1"),
        bd(279, "-1"),
        bd(302, "-1.321928095"),
        bd(322, "0.0")
      )

      val squashed = helper.removeNonBoundaryZeros(EVs)

      squashed shouldEqual expected
    }
  }

  describe("Convert sequence to a Cut-Off sequence") {
    it("should not add zero if the sequence contains only two elements aka. sequence is a boundary only seq") { helper =>
      val changes = Seq(bd(0, "0.0"), bd(4, "4.6547"))
      val expected = Seq(bd(0, "0.0"), bd(4, "4.6547"))

      val result = helper.toCutOffSequence(changes)

      result shouldEqual expected
    }

    it("should return the original sequence if the changes are neighbours") { helper =>
      val changes = Seq(bd(0, "0.0"), bd(1, "4.6547"), bd(2, "2.547"), bd(3, "7.64"))
      val expected = Seq(bd(0, "0.0"), bd(1, "4.6547"), bd(2, "2.547"), bd(3, "7.64"))

      val result = helper.toCutOffSequence(changes)

      result shouldEqual expected
    }

    it("should return the cut-off sequence if there is a gap in indices") { helper =>
      val changes = Seq(bd(0, "0.0"), bd(2, "4.6547"), bd(4, "2.547"), bd(6, "7.64"))
      val expected = Seq(bd(0, "0.0"), bd(2, "4.6547"), bd(3, "0.0"), bd(4, "2.547"), bd(5, "0.0"), bd(6, "7.64"))

      val result = helper.toCutOffSequence(changes)

      result shouldEqual expected
    }
  }

  describe("Shifting the Sequence's indices") {
    it("should return the shifted sequence") { helper =>
      val sequence = Seq(bd(0, "0"), bd(3, "3"), bd(5, "5"), bd(8, "8"))
      val expected = Seq(bd(0, "0"), bd(2, "3"), bd(4, "5"), bd(7, "8"), bd(8, "0"))

      val result = helper.shiftSequenceIndices(sequence)

      result shouldEqual expected
    }
  }

  describe("Convert changes to absolute") {
    it("should return the absolute values when changes are positive") { helper =>
      val base = 4000
      val sequence = List(bd(0, "0"), bd(3, "200"), bd(6, "100"), bd(8, "600"))
      val expected = List(bd(0, "4000"), bd(3, "4200"), bd(6, "4300"), bd(8, "4900"))

      val result = helper.toAbsolute(sequence, Nil, base)

      result shouldEqual expected
    }

    it("should return the absolute values when changes are mixed, positive and negative") { helper =>
      val base = 4000
      val sequence = List(bd(0, "0"), bd(3, "-200"), bd(6, "-100"), bd(8, "600"))
      val expected = List(bd(0, "4000"), bd(3, "3800"), bd(6, "3700"), bd(8, "4300"))

      val result = helper.toAbsolute(sequence, Nil, base)

      result shouldEqual expected
    }
  }
}
