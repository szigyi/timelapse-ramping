package hu.szigyi.timelapse.ramping.xmp

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
  def getShutterSpeed(exifDir: ExifSubIFDDirectory): BigDecimal = {
    val errorMsg = "ExposureTime is not found in the image!"
    val shutterSpeedStr = nullToError(exifDir.getString(ExifDirectoryBase.TAG_EXPOSURE_TIME), errorMsg)
    rationalToDecimal(shutterSpeedStr)
  }

  /**
    * @param exifDir
    * @return the parsed ISO number or exception
    */
  def getISO(exifDir: ExifSubIFDDirectory): Int = {
    val errorMsg = "ISOSpeedRatings is not found in the image!"
    nullToError(exifDir.getInt(ExifDirectoryBase.TAG_ISO_EQUIVALENT), errorMsg)
  }

  /**
    * If there is a default value for Aperture from Configuration
    * then it uses it. Otherwise it tries to parse the value from XMP.
    * @param exifDir
    * @return default Aperture if provided or parsed F-Number or exception
    */
  def getAperture(exifDir: ExifSubIFDDirectory): BigDecimal = {
    val errorMsg = "F-Number is not found in the image and Default value ('DEFAULT_APERTURE') is not provided!"
//    TAG_FNUMBER
    default.aperture match {
      case Some(aperture) => aperture
      case None => {
        val apertureStr = exifDir.getString(ExifDirectoryBase.TAG_FNUMBER)
        logger.info(s"F-Number: $apertureStr")
        val aperture = nullToError(apertureStr, errorMsg)
        rationalToDecimal(aperture)
      }
    }
  }

  /**
    * Exposure is not a required field. Therefore it uses the default value
    * if it does not exists in the image.
    * @param exifDir
    * @return
    */
  def getExposure(exifDir: ExifSubIFDDirectory): (BigDecimal, Boolean) = {
    val errorMsg = "Exposure2012 is not found in the image!"
    // TODO consider all the possible values that can hold this data like: cr2 tag
//    val exposureTry = mapToTry(exifDir.getString())
//    exposureTry match {
//      case Success(exposure) => (exposure, true)
//      case Failure(_) => {
//        logger.trace(errorMsg)
//        (default.exposure, false)
//      }
//    }
    (BigDecimal(0.0), false)
  }

  // TODO adding ClassTag to keep type information for runtime
  private def nullToError[T](result: T, errorReason: String): T = result match {
    case realResult: T => realResult
    case null => throw new RuntimeException(errorReason)
  }

  private def mapToTry[T](result: XPathResult[T]): Try[T] = result match {
    case Right(r) => Success(r)
    case Left(error: XPathError) => Failure(error)
  }

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

object XmpParser {
  def apply(default: DefaultConfig): XmpParser = new XmpParser(default)
}
