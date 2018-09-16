package hu.szigyi.timelapse.ramping

import cats.data.Validated._
import cats.data.ValidatedNel
import cats.data.{Validated, ValidatedNel}
import cats.data.Validated.{Invalid, Valid}
import org.scalatest.{FunSpec, Matchers}
import cats.implicits._

import scala.collection.immutable

class TraverserTest extends FunSpec with Matchers {

  type Val[A] = ValidatedNel[Exception, A]

  val exception = new Exception()

  describe("Flipping inside to out") {
    it("All of is valid, then the seq should be valid") {
      val validated: List[Val[String]] = List(Valid("str"), Valid("str"), Valid("str"))

      val validList: Val[List[String]] = validated.sequence[Val, String]

      validList.isInvalid shouldBe false
    }

    it("One is invalid, then the seq should be invalid") {
      val validated: List[Val[String]] = List(Valid("str"), invalidNel(exception), Valid("str"))

      val validList: Val[List[String]] = validated.sequence[Val, String]

      validList.isInvalid shouldBe true
    }

    it("All of is invalid, then the seq should be invalid") {
      val validated: List[Val[String]] = List(invalidNel(exception), invalidNel(exception), invalidNel(exception))

      val validList: Val[List[String]] = validated.sequence[Val, String]

      validList.isInvalid shouldBe true
    }
  }
}
