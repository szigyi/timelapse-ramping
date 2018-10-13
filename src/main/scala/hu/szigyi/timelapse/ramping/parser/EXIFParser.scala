package hu.szigyi.timelapse.ramping.parser

import com.drew.metadata.exif.makernotes.CanonMakernoteDirectory
import com.drew.metadata.exif.{ExifDirectoryBase, ExifSubIFDDirectory}
import com.typesafe.scalalogging.LazyLogging
import hu.szigyi.timelapse.ramping.conf.DefaultConfig
import hu.szigyi.timelapse.ramping.parser.ParserError._

import scala.util.{Failure, Success, Try}

object ParserError {
  sealed trait ParserException extends Exception {
    val message: String
  }

  case object ISOParserException extends ParserException {
    override val message: String = "ISOSpeedRatings is not found in the image!"
  }

  case object ShutterSpeedParserException extends ParserException {
    override val message: String = "ExposureTime (Shutter Speed) is not found in the image!"
  }

  case object ApertureParserException extends ParserException {
    override val message: String = "F-Number (Aperture) is not found in the image and Default value ('DEFAULT_APERTURE') is not provided!"
  }

  case object ExposureParserException extends ParserException {
    override val message: String = "Exposure is not found in the image and Default value ('DEFAULT_EXPOSURE') is not provided!"
  }

  case object TemperatureParserException extends ParserException {
    override val message: String = "Temperature (WB) is not found in the image!"
  }
}

class EXIFParser(default: DefaultConfig) extends LazyLogging {

  /**
    * @param exifDir
    * @return the parsed ISO number or exception
    */
  def getISO(exifDir: ExifSubIFDDirectory): Try[Int] = {
    toTry(exifDir.getInt(ExifDirectoryBase.TAG_ISO_EQUIVALENT), ISOParserException)
  }

  /**
    * @param exifDir
    * @return the parsed ExposureTime (Shutter Speed) or exception
    */
  def getShutterSpeed(exifDir: ExifSubIFDDirectory): Try[BigDecimal] = {
    val shutterSpeedTry = toTry(exifDir.getString(ExifDirectoryBase.TAG_EXPOSURE_TIME), ShutterSpeedParserException)
    shutterSpeedTry.map(ss => rationalToDecimal(ss))
  }

  /**
    * If there is a default value for Aperture from Configuration
    * then it uses it. Otherwise it tries to parse the value from XMP.
    * @param exifDir
    * @return default Aperture if provided or parsed F-Number or exception
    */
  def getAperture(exifDir: ExifSubIFDDirectory): Try[BigDecimal] = {
    default.aperture match {
      case Some(aperture) => Success(aperture)
      case None => {
        val apertureStr = exifDir.getString(ExifDirectoryBase.TAG_FNUMBER)
        val apertureTry = toTry(apertureStr, ApertureParserException)
        apertureTry.map(ap => rationalToDecimal(ap))
      }
    }
  }

  /**
    * Exposure is not a required field. Therefore it uses the default value
    * if it does not exists in the image.
    * @param exifDir
    * @return
    */
  def getExposure(exifDir: ExifSubIFDDirectory): Try[BigDecimal] = {
    val exposureDouble = exifDir.getDouble((ExifDirectoryBase.TAG_EXPOSURE_BIAS))
    toTry(exposureDouble, ExposureParserException)
  }

  def getTemperature(canonDir: CanonMakernoteDirectory): Try[Int] = {
    val colorData7 = 0x4001
    val colorTempAsShot = 67
    // TODO error handling way before get int array's result
    val wbInt = canonDir.getIntArray(colorData7)(colorTempAsShot)
    toTry(wbInt, TemperatureParserException)
  }

  private def toTry[T](result: T, exception: ParserException): Try[T] = result match {
    case realResult: T => Success(realResult)
    case null => Failure(exception)
  }

  // TODO revisit, method name is not good
  private def rationalToDecimal(str: String): BigDecimal = {
    import math.BigDecimal._
    /**
      * If Rational's value in XML is less than 1/200 then it calculates the decimal value!
      * {@link com.drew.lang.Rational#toSimpleString}
      */
    try {
      BigDecimal(str, defaultMathContext)
    } catch {
      case _: NumberFormatException => {
        val numDen = str.split("/")
        val numerator = BigDecimal(numDen(0), defaultMathContext)
        val denominator = BigDecimal(numDen(1), defaultMathContext)
        numerator / denominator
      }
    }
  }
}

object EXIFParser {
  def apply(default: DefaultConfig): EXIFParser = new EXIFParser(default)
}
