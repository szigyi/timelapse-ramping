package hu.szigyi.timelapse.ramping.algo.ramp

import hu.szigyi.timelapse.ramping.algo.ev.EV
import org.scalatest.{Matchers, Outcome, fixture}


class RampHelperSpec extends fixture.FunSpec with Matchers {

  override type FixtureParam = RampHelper

  override protected def withFixture(test: OneArgTest): Outcome = test(RampHelper(EV()))

  def t(i: Int, v: String): (Int, BigDecimal) = (i, BigDecimal(v))

  describe("Calculate Changes in EVs") {

    it("should return same size of seq") { helper =>
      val evs = Seq(BigDecimal("0.0"), BigDecimal("0.0"), BigDecimal("2.0"))
      val size = evs.size

      val result = helper.relativeChangesInData(evs)

      result.size shouldEqual size
    }
    it("should duplicate the first element, therefore first element in the result should be ZERO") { helper =>
      val evs = Seq(BigDecimal("5.3464578"), BigDecimal("1.0"), BigDecimal("2.0"))
      val expected = Seq(BigDecimal("0.00000000"), BigDecimal("4.3464578"), BigDecimal("-1.0"))

      val result = helper.relativeChangesInData(evs)

      result shouldEqual expected
    }
    it("should be ZERO if there is no change between to number") { helper =>
      val evs = Seq(BigDecimal("0.0"), BigDecimal("0.0"), BigDecimal("1.5"))
      val expected = Seq(BigDecimal("0.00000000"), BigDecimal("0.00000000"), BigDecimal("-1.5"))

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
      val value = BigDecimal("0.0")
      val EVs = Seq(value, value, value, value, value)
      val expected = Seq((0, value), (4, value))

      val squashed = helper.removeNonBoundaryZeros(EVs)

      squashed shouldEqual expected
    }

    it("should remove the zeros from the middle of the seq even if the first element is zero") { helper =>
      val EVs = Seq(BigDecimal("0.0"), BigDecimal("0.0"), BigDecimal("0.0"), BigDecimal("0.1"))
      val expected = Seq((0, BigDecimal("0.0")), (3, BigDecimal("0.1")))

      val squashed = helper.removeNonBoundaryZeros(EVs)

      squashed shouldEqual expected
    }

    it("should remove the zeros from the middle of the seq even if the first element is not zero") { helper =>
      val EVs = Seq(BigDecimal("0.1"), BigDecimal("0.0"), BigDecimal("0.0"), BigDecimal("0.0"), BigDecimal("0.1"))
      val expected = Seq((0, BigDecimal("0.1")), (4, BigDecimal("0.1")))

      val squashed = helper.removeNonBoundaryZeros(EVs)

      squashed shouldEqual expected
    }

    it("should remove the zeros from the end of the seq even if the last element is zero") { helper =>
      val EVs = Seq(BigDecimal("0.1"), BigDecimal("0.0"), BigDecimal("0.0"), BigDecimal("0.0"))
      val expected = Seq((0, BigDecimal("0.1")), (3, BigDecimal("0.0")))

      val squashed = helper.removeNonBoundaryZeros(EVs)

      squashed shouldEqual expected
    }

    it("should remove the zeros from a real life sequence") { helper =>
      val EVs = Seq(BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("-5.64385619"), BigDecimal("0"), BigDecimal("0"), BigDecimal("1.321928095"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("-1"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("-1"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("-1"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("-1.321928095"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("-2"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("-0.5849625007"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("-1.415037499"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("-1"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("-1"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("-1.321928095"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"), BigDecimal("0"))
      val expected = Seq(
        (0, BigDecimal("0.0")),
        (105, BigDecimal("-5.64385619")),
        (108, BigDecimal("1.321928095")),
        (133, BigDecimal("-1")),
        (153, BigDecimal("-1")),
        (170, BigDecimal("-1")),
        (193, BigDecimal("-1.321928095")),
        (208, BigDecimal("-2")),
        (224, BigDecimal("-0.5849625007")),
        (239, BigDecimal("-1.415037499")),
        (259, BigDecimal("-1")),
        (279, BigDecimal("-1")),
        (302, BigDecimal("-1.321928095")),
        (322, BigDecimal("0.0"))
      )

      val squashed = helper.removeNonBoundaryZeros(EVs)

      squashed shouldEqual expected
    }
  }

  describe("Convert sequence to a Cut-Off sequence") {
    it("should not add zero if the sequence contains only two elements aka. sequence is a boundary only seq") { helper =>
      val changes = Seq(t(0, "0.0"), t(4, "4.6547"))
      val expected = Seq(t(0, "0.0"), t(4, "4.6547"))

      val result = helper.toCutOffSequence(changes)

      result shouldEqual expected
    }

    it("should return the original sequence if the changes are neighbours") { helper =>
      val changes = Seq(t(0, "0.0"), t(1, "4.6547"), t(2, "2.547"), t(3, "7.64"))
      val expected = Seq(t(0, "0.0"), t(1, "4.6547"), t(2, "2.547"), t(3, "7.64"))

      val result = helper.toCutOffSequence(changes)

      result shouldEqual expected
    }

    it("should return the cut-off sequence if there is a gap in indices") { helper =>
      val changes = Seq(t(0, "0.0"), t(2, "4.6547"), t(4, "2.547"), t(6, "7.64"))
      val expected = Seq(t(0, "0.0"), t(2, "4.6547"), t(3, "0.0"), t(4, "2.547"), t(5, "0.0"), t(6, "7.64"))

      val result = helper.toCutOffSequence(changes)

      result shouldEqual expected
    }
  }

  describe("Shifting the Sequence's indices") {
    it("should return the shifted sequence") { helper =>
      val sequence = Seq(t(0, "0"), t(3, "3"), t(5, "5"), t(8, "8"))
      val expected = Seq(t(0, "0"), t(2, "3"), t(4, "5"), t(7, "8"), t(8, "0"))

      val result = helper.shiftSequenceIndices(sequence)

      result shouldEqual expected
    }
  }
}
