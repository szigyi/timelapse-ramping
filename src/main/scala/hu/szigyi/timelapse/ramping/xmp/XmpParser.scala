package hu.szigyi.timelapse.ramping.xmp

import com.drew.metadata.exif.makernotes.CanonMakernoteDirectory
import com.typesafe.scalalogging.LazyLogging
import hu.szigyi.timelapse.ramping.conf.DefaultConfig
import kantan.xpath.{XPathError, XPathResult}

import scala.util.{Failure, Success, Try}
import com.drew.metadata.exif.{ExifDirectoryBase, ExifSubIFDDirectory}


class XmpParser(default: DefaultConfig) extends LazyLogging {

  /**
    * @param exifDir
    * @return the parsed ExposureTime (Shutter Speed) or exception
    */
  def getShutterSpeed(exifDir: ExifSubIFDDirectory): Try[BigDecimal] = {
    val errorMsg = "ExposureTime is not found in the image!"
    val shutterSpeedStr = nullToTry(exifDir.getString(ExifDirectoryBase.TAG_EXPOSURE_TIME), errorMsg)
    rationalToDecimal(shutterSpeedStr)
  }

  /**
    * @param exifDir
    * @return the parsed ISO number or exception
    */
  def getISO(exifDir: ExifSubIFDDirectory): Try[Int] = {
    val errorMsg = "ISOSpeedRatings is not found in the image!"
    nullToTry(exifDir.getInt(ExifDirectoryBase.TAG_ISO_EQUIVALENT), errorMsg)
  }

  /**
    * If there is a default value for Aperture from Configuration
    * then it uses it. Otherwise it tries to parse the value from XMP.
    * @param exifDir
    * @return default Aperture if provided or parsed F-Number or exception
    */
  def getAperture(exifDir: ExifSubIFDDirectory): Try[BigDecimal] = {
    val errorMsg = "F-Number is not found in the image and Default value ('DEFAULT_APERTURE') is not provided!"
//    TAG_FNUMBER
    default.aperture match {
      case Some(aperture) => Success(aperture)
      case None => {
        val apertureStr = exifDir.getString(ExifDirectoryBase.TAG_FNUMBER)
        val apertureTry = nullToTry(apertureStr, errorMsg)
        rationalToDecimal(apertureTry)
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
    val errorMsg = "Exposure2012 is not found in the image!"
//    val exposureTry = mapToTry(exifDir.getString())
//    exposureTry match {
//      case Success(exposure) => (exposure, true)
//      case Failure(_) => {
//        logger.trace(errorMsg)
//        (default.exposure, false)
//      }
//    }
    // TODO use actual exposure value
    nullToTry[BigDecimal](null, errorMsg)
  }

  def getTemperature(canonDir: CanonMakernoteDirectory): Try[Int] = {
    val colorData7 = 0x4001
    val colorTempAsShot = 67
    val errorMsg = "Temperature is not found in the image!"
    // TODO error handling way before get int array's result
    val wbInt = canonDir.getIntArray(colorData7)(colorTempAsShot)
    nullToTry(wbInt, errorMsg)
  }

  // TODO adding ClassTag to keep type information for runtime??????? should I ??
  private def nullToTry[T](result: T, errorReason: String): Try[T] = result match {
    case realResult: T => Success(realResult)
    case null => Failure(new RuntimeException(errorReason))
  }

  // TODO revisit, method name is not good
  private def rationalToDecimal(str: Try[String]): Try[BigDecimal] = str match {
    case Success(value) => {
      import math.BigDecimal._
      /**
        * If Rational's value in XML is less than 1/200 then it calculates the decimal value!
        * {@link com.drew.lang.Rational#toSimpleString}
        */
      try {
        Success(BigDecimal(value, defaultMathContext))
      } catch {
        case _: NumberFormatException => {
          val numDen = value.split("/")
          val numerator = BigDecimal(numDen(0), defaultMathContext)
          val denominator = BigDecimal(numDen(1), defaultMathContext)
          Success(numerator / denominator)
        }
      }
    }
    case Failure(exception) => Failure(exception)
  }
}

object XmpParser {
  def apply(default: DefaultConfig): XmpParser = new XmpParser(default)
}
