package hu.szigyi.timelapse.ramping.testutil

import java.math.{MathContext, RoundingMode}

object BigDecimalTestExtension {
  val roundingMathContext = new MathContext(1, RoundingMode.HALF_EVEN)
}
