package hu.szigyi.timelapse.ramping.math

import java.math.{MathContext, BigDecimal => JavaBigDecimal, RoundingMode => JavaRoundingMode}

import scala.math.BigDecimal.RoundingMode.RoundingMode

object CustomMathContext {
  def apply(precision: Int, roundMode: JavaRoundingMode) = new MathContext(precision, roundMode)
}

object BigDecimalConverter {
  import math.BigDecimal._
  implicit def convertToJava(bigDecimal: BigDecimal): JavaBigDecimal = bigDecimal.bigDecimal
  implicit def convertFromInt(int: Int): JavaBigDecimal = new JavaBigDecimal(int, defaultMathContext)
}

object BigDecimalDecorator {
  import math.BigDecimal._

  val ZERO: BigDecimal = BigDecimal("0.0", defaultMathContext)

  implicit def decorateBigDecimal(bigDecimal: BigDecimal): BigDecimalExt = BigDecimalExt(bigDecimal)

  sealed case class BigDecimalExt(bd1: BigDecimal) {
    def ===(bd2: BigDecimal): Boolean = {
      val scaledBD1 = bd1.setScale(defaultMathContext.getPrecision, defaultMathContext.getRoundingMode)
      val scaledBD2 = bd2.setScale(defaultMathContext.getPrecision, defaultMathContext.getRoundingMode)
      scaledBD1.equals(scaledBD2)
    }

    def `^2`: BigDecimal = {
      import BigDecimalConverter._
      import ch.obermuhlner.math.big.BigDecimalMath._
      pow(bd1, 2L, defaultMathContext)
    }

    def neg: BigDecimal = bd1 * -1
  }

  implicit def roundingModeConverter(roundingMode: JavaRoundingMode): RoundingMode = roundingMode match {
    case JavaRoundingMode.CEILING => RoundingMode.CEILING
    case JavaRoundingMode.DOWN => RoundingMode.DOWN
    case JavaRoundingMode.FLOOR => RoundingMode.FLOOR
    case JavaRoundingMode.HALF_DOWN => RoundingMode.HALF_DOWN
    case JavaRoundingMode.HALF_EVEN => RoundingMode.HALF_EVEN
    case JavaRoundingMode.HALF_UP => RoundingMode.HALF_UP
    case JavaRoundingMode.UNNECESSARY => RoundingMode.UNNECESSARY
    case JavaRoundingMode.UP => RoundingMode.UP
  }
}