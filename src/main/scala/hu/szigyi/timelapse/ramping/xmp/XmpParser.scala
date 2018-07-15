package hu.szigyi.timelapse.ramping.xmp

import com.typesafe.scalalogging.LazyLogging
import hu.szigyi.timelapse.ramping.conf.DefaultConfig
import kantan.xpath.{XPathError, XPathResult}
import kantan.xpath.implicits._

import scala.util.{Failure, Success, Try}

import com.drew.imaging.ImageMetadataReader
import com.drew.metadata.Metadata


class XmpParser(default: DefaultConfig) extends LazyLogging {

  /**
    * @param xmpAsString
    * @return the parsed value or exception
    */
  def getShutterSpeed(xmpAsString: String): BigDecimal = {
    // TODO consider all the possible values that can hold this data like: cr2 tag
    // TODO can get that from other tags
    val shutterSpeedPath = xp"//*[local-name()='ExposureTime']"
    val shutterSpeedStr = unliftOrError(xmpAsString.evalXPath[String](shutterSpeedPath),
      s"ExposureTime is not found in the XMP")
    calculateFromRationalNumber(shutterSpeedStr)
  }

  /**
    * @param xmpAsString
    * @return the parsed ISO number or exception
    */
  def getISO(xmpAsString: String): Int = {
    // TODO consider all the possible values that can hold this data like: cr2 tag
    val isoPath = xp"//*[local-name()='ISOSpeedRatings']/Seq/li[1]"
    val xPathResult = xmpAsString.evalXPath[Int](isoPath)
    unliftOrError(xPathResult, s"ISOSpeedRatings is not found in the XMP")
  }

  /**
    * If there is a default value for Aperture from Configuration
    * then it uses it. Otherwise it tries to parse the value from XMP.
    * @param xmpAsString
    * @return default aperture if provided or parsed value or exception
    */
  def getAperture(xmpAsString: String): BigDecimal = {
    // TODO consider all the possible values that can hold this data like: cr2 tag
    // TODO Can calculate from FNumber if that exists
    val aperturePath = xp"//*[local-name()='ApertureValue']"
    default.aperture match {
      case Some(aperture) => aperture
      case None => {
        val apertureStr = xmpAsString.evalXPath[String](aperturePath)
        val aperture = unliftOrError(apertureStr,
          s"ApertureValue is not found in the XMP and there is no Default value ('DEFAULT_APERTURE') for it.")
        calculateFromRationalNumber(aperture)
      }
    }
  }

  /**
    * Exposure is not a required field. Therefore it uses the default value
    * does not exists in the XMP.
    * @param xmpAsString
    * @return
    */
  def getExposure(xmpAsString: String): (BigDecimal, Boolean) = {
    val exposureNotFoundMsg = "Exposure2012 is not found in the XMP."
    // TODO consider all the possible values that can hold this data like: cr2 tag
    val exposurePath = xp"//*[local-name()='Exposure2012']"
    val exposureTry = mapToTry(xmpAsString.evalXPath[BigDecimal](exposurePath))
    exposureTry match {
      case Success(exposure) => (exposure, true)
      case Failure(_) => {
        logger.trace(exposureNotFoundMsg)
        (default.exposure, false)
      }
    }
  }

  private def unliftOrError[T](result: XPathResult[T], errorReason: String): T = result match {
    case Right(r) => r
    case Left(error: XPathError) => throw new RuntimeException(errorReason, error)
  }

  private def mapToTry[T](result: XPathResult[T]): Try[T] = result match {
    case Right(r) => Success(r)
    case Left(error: XPathError) => Failure(error)
  }

  private def calculateFromRationalNumber(str: String): BigDecimal = {
    import math.BigDecimal._
    val numDen = str.split("/")
    val numerator = BigDecimal(numDen(0), defaultMathContext)
    val denominator = BigDecimal(numDen(1), defaultMathContext)
    numerator / denominator
  }
}

object XmpParser {
  def apply(default: DefaultConfig): XmpParser = new XmpParser(default)
}
