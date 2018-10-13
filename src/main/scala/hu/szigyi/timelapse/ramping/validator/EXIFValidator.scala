package hu.szigyi.timelapse.ramping.validator

import java.io.File

import cats.implicits._
import cats.data.Validated._
import cats.data.{ValidatedNel}
import com.typesafe.scalalogging.LazyLogging
import hu.szigyi.timelapse.ramping.conf.DefaultConfig
import hu.szigyi.timelapse.ramping.model.{EXIF, EXIFSettings}
import hu.szigyi.timelapse.ramping.parser.ParserError._
import hu.szigyi.timelapse.ramping.validator.EXIFValidator.EXIFValid
import hu.szigyi.timelapse.ramping.validator.Violations._

import scala.util.{Failure, Success, Try}

object Violations {

  sealed trait Violation {
    val message: String
    val imagePath: String
  }

  trait ParsingViolation extends Violation

  trait RangeViolation[T] extends Violation {
    val actualValue: T
  }

  case class ISOParsingViolation(message: String, imagePath: String) extends ParsingViolation

  case class ShutterSpeedParsingViolation(message: String, imagePath: String) extends ParsingViolation

  case class ApertureParsingViolation(message: String, imagePath: String) extends ParsingViolation

  case class ExposureParsingViolation(message: String, imagePath: String) extends ParsingViolation

  case class TemperatureParsingViolation(message: String, imagePath: String) extends ParsingViolation

  case class ISORangeViolation(actualValue: Int, imagePath: String) extends RangeViolation[Int] {
    override val message: String = s"ISO should be an Integer and in range, between ${ISORange.min} and ${ISORange.max}. Actual value is not in the range: $actualValue"
  }

  case class ApertureRangeViolation(actualValue: BigDecimal, imagePath: String) extends RangeViolation[BigDecimal] {
    override val message: String = s"Aperture should be a BigDecimal and in range, between ${ApertureRange.min} and ${ApertureRange.max}. Actual value is not in the range: $actualValue"
  }

  case class TemperatureRangeViolation(actualValue: Int, imagePath: String) extends RangeViolation[Int] {
    override val message: String = s"Temperature should be an Integer and in range, between ${TemperatureRange.min} and ${TemperatureRange.max}. Actual value is not in the range: $actualValue"
  }


  trait Ranges[T] {
    /**
      * min is exclusive
      */
    val min: T
    /**
      * max is inclusive
      */
    val max: T
  }

  case object ApertureRange extends Ranges[BigDecimal] {
    val min: BigDecimal = 0
    val max: BigDecimal = Double.MaxValue
  }

  case object ISORange extends Ranges[Int] {
    val min: Int = 0
    val max: Int = 512000
  }

  case object TemperatureRange extends Ranges[Int] {
    val min: Int = 2000
    val max: Int = 50000
  }

}

class EXIFValidator(default: DefaultConfig) extends LazyLogging {

  private def isParsed[T](parsed: Try[T], imagePath: String, defaultValue: Option[T] = None): EXIFValid[T] = defaultValue match {
    case Some(d: T) => valid(d)
    case None => parsed match {
      case Success(value: T) => valid(value)
      case Failure(exception: ParserException) => exception match {
        case ISOParserException => invalidNel(ISOParsingViolation(exception.message, imagePath))
        case ShutterSpeedParserException => invalidNel(ShutterSpeedParsingViolation(exception.message, imagePath))
        case ApertureParserException => invalidNel(ApertureParsingViolation(exception.message, imagePath))
        case ExposureParserException => invalidNel(ExposureParsingViolation(exception.message, imagePath))
        case TemperatureParserException => invalidNel(TemperatureParsingViolation(exception.message, imagePath))
      }
    }
  }

  private def inRange[T](value: EXIFValid[T],
                         ranges: Ranges[T],
                         imagePath: String): EXIFValid[T] = {

    def inRangeInt(value: Int, ranges: Ranges[Int]): Boolean = {
      val lower = value > ranges.min
      val upper = value <= ranges.max
      lower && upper
    }

    def inRangeBigDecimal(value: BigDecimal, ranges: Ranges[BigDecimal]): Boolean = {
      val lower = value > ranges.min
      val upper = value <= ranges.max
      lower && upper
    }

    value match {
      case Invalid(e) => Invalid(e)
      case Valid(v) => ranges match {
        case ISORange => {
          if (inRangeInt(v.asInstanceOf[Int], ranges.asInstanceOf[Ranges[Int]])) valid(v)
          else invalidNel(ISORangeViolation(v.asInstanceOf[Int], imagePath))
        }
        case ApertureRange => {
          if (inRangeBigDecimal(v.asInstanceOf[BigDecimal], ranges.asInstanceOf[Ranges[BigDecimal]])) valid(v)
          else invalidNel(ApertureRangeViolation(v.asInstanceOf[BigDecimal], imagePath))
        }
        case TemperatureRange => {
          if (inRangeInt(v.asInstanceOf[Int], ranges.asInstanceOf[Ranges[Int]])) valid(v)
          else invalidNel(TemperatureRangeViolation(v.asInstanceOf[Int], imagePath))
        }
      }
    }
  }

  def validateEXIFSettings(imagePath: String,
                           iso: Try[Int],
                           shutterSpeed: Try[BigDecimal],
                           aperture: Try[BigDecimal],
                           exposure: Try[BigDecimal],
                           temperature: Try[Int]): EXIFValid[EXIFSettings] = {

    val isoParsed = isParsed[Int](iso, imagePath)
    val isoRange = inRange[Int](isoParsed, ISORange, imagePath)

    val shutterParsed = isParsed[BigDecimal](shutterSpeed, imagePath)

    val apertureParsed = isParsed[BigDecimal](aperture, imagePath, default.aperture)
    val apertureRange = inRange[BigDecimal](apertureParsed, ApertureRange, imagePath)

    val exposureParsed = isParsed[BigDecimal](exposure, imagePath, Some(default.exposure))

    val temperatureParsed = isParsed[Int](temperature, imagePath)
    val temperatureRange = inRange[Int](temperatureParsed, TemperatureRange, imagePath)

    (isoRange, shutterParsed, apertureRange, exposureParsed, temperatureRange).map5(EXIFSettings)
  }

  def validateEXIF(imagePath: String,
                   xmpFilePath: File,
                   iso: Try[Int],
                   shutterSpeed: Try[BigDecimal],
                   aperture: Try[BigDecimal],
                   exposure: Try[BigDecimal],
                   temperature: Try[Int]): EXIFValid[EXIF] = {
    (validateFile(xmpFilePath),
      validateEXIFSettings(imagePath, iso, shutterSpeed, aperture, exposure, temperature)).map2(EXIF)
  }

  private def validateFile(file: File): EXIFValid[File] = valid(file)
}

object EXIFValidator {

  type EXIFValid[A] = ValidatedNel[Violation, A]

  def apply(default: DefaultConfig): EXIFValidator = new EXIFValidator(default)
}
