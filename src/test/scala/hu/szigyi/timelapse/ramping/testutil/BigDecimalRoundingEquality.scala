package hu.szigyi.timelapse.ramping.testutil

import java.math.{MathContext, RoundingMode}

import hu.szigyi.timelapse.ramping.math.BigDecimalDecorator
import org.scalactic.Equality

object BigDecimalRoundingEquality {
  implicit val bigDecimalRoundedEquality = new Equality[BigDecimal] {
    override def areEqual(a: BigDecimal, b: Any): Boolean = {
      if (!b.isInstanceOf[BigDecimal]) false
      else {
        val mathContext = new MathContext(1, RoundingMode.HALF_EVEN)
        val scaledBD1 = a.setScale(mathContext.getPrecision, BigDecimalDecorator.roundingModeConverter(mathContext.getRoundingMode))
        val scaledBD2 = b.asInstanceOf[BigDecimal].setScale(mathContext.getPrecision, BigDecimalDecorator.roundingModeConverter(mathContext.getRoundingMode))
        scaledBD1.equals(scaledBD2)
      }
    }
  }
}

//object BigDecimalEquality {
//  implicit val bigDecimalEquality = new Equality[BigDecimal] {
//    override def areEqual(a: BigDecimal, b: Any): Boolean =
//      if (b.isInstanceOf[BigDecimal]) BigDecimalDecorator.decorateBigDecimal(a)===(b.asInstanceOf[BigDecimal])
//      else false
//  }
//}
