package hu.szigyi.timelapse.ramping.math

import java.math.MathContext

import scala.math.BigDecimal.RoundingMode.RoundingMode

object MathContext {
  def apply(precision: Int, roundMode: java.math.RoundingMode) = new MathContext(precision, roundMode)
}

object BigDecimalConverter {
  import math.BigDecimal._
  implicit def convertToJava(bigDecimal: BigDecimal): java.math.BigDecimal = bigDecimal.bigDecimal
  implicit def convertFromInt(int: Int): java.math.BigDecimal = new java.math.BigDecimal(int, defaultMathContext)
}

object BigDecimalEquals {
  import math.BigDecimal._
  def bdEquals(bd1: BigDecimal, bd2: BigDecimal): Boolean = {
    val scaledBD1 = bd1.setScale(defaultMathContext.getPrecision, defaultMathContext.getRoundingMode)
    val scaledBD2 = bd2.setScale(defaultMathContext.getPrecision, defaultMathContext.getRoundingMode)
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