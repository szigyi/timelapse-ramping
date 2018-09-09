package hu.szigyi.timelapse.ramping.testutil

import java.math.{MathContext, RoundingMode}

object BigDecimalTestExtension {

  val roundingMathContext = new MathContext(1, RoundingMode.HALF_EVEN)

  def bd(index: Int, bigDecimalValue: String): (Int, BigDecimal) = (index, BigDecimal(bigDecimalValue))
}
