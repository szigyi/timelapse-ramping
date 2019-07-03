package hu.szigyi.timelapse.ramping.model

import java.io.File

import hu.szigyi.timelapse.ramping.math.BigDecimalDecorator._

case class Metadata(xmpFilePath: File,
                    keyFrame: Boolean,
                    settings: Settings)

case class Settings(iso: Int,
                    shutterSpeed: BigDecimal,
                    aperture: BigDecimal,
                    exposure: BigDecimal,
                    temperature: Int)

case class Processed(metadata: Metadata,
                     processedSettings: ProcessedSettings)

case class ProcessedSettings(exposure: Option[BigDecimal],
                             temperature: Option[Int])

case class Scaled(processed: Processed, scaledSettings: ScaledSettings)

case class ScaledSettings(iso: BigDecimal,
                          shutterSpeed: BigDecimal,
                          aperture: BigDecimal,
                          exposure: BigDecimal,
                          temperature: BigDecimal,
                          rampedExposure: BigDecimal,
                          rampedTemperature: BigDecimal)


sealed trait FieldForScale
trait ISO extends FieldForScale
trait ShutterSpeed extends FieldForScale
trait Aperture extends FieldForScale
trait Exposure extends FieldForScale
trait Temperature extends FieldForScale
trait RampedExposure extends FieldForScale
trait RampedTemperature extends FieldForScale

trait Scale[FieldForScale] {
  def scale(value: BigDecimal): BigDecimal
}

object Scale {
  def zeroOneScale[T <: FieldForScale](value: BigDecimal)(implicit scale: Scale[T]): BigDecimal = scale.scale(value)
}

case class ZeroOneScale[T <: FieldForScale](min: BigDecimal, max: BigDecimal) extends Scale[T] {
  override def scale(value: BigDecimal): BigDecimal = {
    val divisor = (max - min)
    val dividend = (value - min)

    if (divisor === ZERO) {
      value
    } else {
      dividend / divisor
    }
  }
}