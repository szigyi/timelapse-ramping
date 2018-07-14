package hu.szigyi.timelapse.ramping.math

import java.math.MathContext

import scala.math.BigDecimal.RoundingMode
import scala.math.BigDecimal.RoundingMode.RoundingMode

object BigDecimalContext {
  val mathContext: MathContext = MathContext(8, java.math.RoundingMode.HALF_UP)
}

object MathContext {
  def apply(precision: Int, roundMode: java.math.RoundingMode) = new MathContext(precision, roundMode)
}

object BigDecimalConverter {
  implicit def convertToJava(bigDecimal: BigDecimal): java.math.BigDecimal = bigDecimal.bigDecimal
  implicit def convertToScala(bigDecimal: java.math.BigDecimal): BigDecimal = BigDecimal(bigDecimal)
  implicit def convertFromInt(int: Int): java.math.BigDecimal = new java.math.BigDecimal(int)
}

object BigDecimalEquals {
  import BigDecimalContext._
  def bdEquals(bd1: BigDecimal, bd2: BigDecimal): Boolean = {
    val scaledBD1 = bd1.setScale(mathContext.getPrecision, mathContext.getRoundingMode)
    val scaledBD2 = bd2.setScale(mathContext.getPrecision, mathContext.getRoundingMode)
    scaledBD1.equals(scaledBD2)
  }

  private implicit def roundingModeConverter(roundingMode: java.math.RoundingMode): RoundingMode = roundingMode match {
    case java.math.RoundingMode.CEILING => RoundingMode.CEILING
    case java.math.RoundingMode.DOWN => RoundingMode.DOWN
    case java.math.RoundingMode.FLOOR => RoundingMode.FLOOR
    case java.math.RoundingMode.HALF_DOWN => RoundingMode.HALF_DOWN
    case java.math.RoundingMode.HALF_EVEN => RoundingMode.HALF_EVEN
    case java.math.RoundingMode.HALF_UP => RoundingMode.HALF_UP
    case java.math.RoundingMode.UNNECESSARY => RoundingMode.UNNECESSARY
    case java.math.RoundingMode.UP => RoundingMode.UP
  }
}