package hu.szigyi.timelapse.ramping.service

import java.io.File

import com.typesafe.scalalogging.LazyLogging
import hu.szigyi.timelapse.ramping.algo.MirrorPrevious
import hu.szigyi.timelapse.ramping.cli.CLI
import hu.szigyi.timelapse.ramping.conf.Default
import hu.szigyi.timelapse.ramping.io.{IOUtil, Reader}
import hu.szigyi.timelapse.ramping.model.{XMP, XMPSettings}
import kantan.xpath.{XPathError, XPathResult}
//import com.drew.imaging.ImageMetadataReader
//import com.drew.metadata.Metadata

class XmpService(default: Default,
                 cli: CLI,
                 fsUtil: IOUtil,
                 reader: Reader,
                 rampMirrorPrevious: MirrorPrevious) extends LazyLogging {

  def ramp(standard: XMP, image: XMP): XMP = rampMirrorPrevious.ramp(standard, image)

  def getXMP(imageFile: File): XMP = {
    val xmpFile = fsUtil.replaceExtension(imageFile, ".xmp")
    val xmpAsString = getOrCreate(imageFile, xmpFile)
    parseXMP(xmpFile, xmpAsString)
  }

  private def getOrCreate(imageFile: File, xmpFile: File): String = {
    if (!reader.isExists(xmpFile)) {
      createXMP(imageFile, xmpFile)
    }
    reader.readFile(xmpFile)
  }

  private def createXMP(imageFile: File, xmpFile: File): String = {
    val imageName = imageFile.getName
    val xmpName = xmpFile.getName
    logger.debug(s"Creating XMP for $imageName")
//    cli.exec(fsUtil.workingDirectoryOf(imageFile), Seq("exiftool", "-xmp", "-b", imageName))
    cli.exec(fsUtil.workingDirectoryOf(imageFile), Seq("exiftool", "-Tagsfromfile", imageName, xmpName))
  }

  private def parseXMP(xmpFile: File, xmpAsString: String): XMP = {
    //    val metadata: Metadata = ImageMetadataReader.readMetadata(file)
    val settings = parseXMPSettings(xmpAsString)
    // TODO parse necessary settings and validate them!!!!!! else throw exception
    // TODO validate it and throw exception if there is no value!
    XMP(xmpFile, xmpAsString, settings)
  }

  private def parseXMPSettings(xmpAsString: String): XMPSettings = {
    val exposure = getShutterSpeed(xmpAsString)
    val iso = getISO(xmpAsString)
    val aperture = getAperture(xmpAsString)
    val bias = getExposureBias(xmpAsString)

    XMPSettings(iso, exposure, aperture, bias)
  }

  private def getShutterSpeed(xmpAsString: String): BigDecimal = {
    import kantan.xpath.implicits._
    // TODO consider all the possible values that can hold this data like: cr2 tag
    // TODO can get that from other tags
    val shutterSpeedPath = xp"//*[local-name()='ExposureTime']"
    val shutterSpeedStr = unliftOrError(xmpAsString.evalXPath[String](shutterSpeedPath),
      s"ExposureTime is not found in the XMP")
    calculateFromRationalNumber(shutterSpeedStr)
  }

  private def getISO(xmpAsString: String): Int = {
    import kantan.xpath.implicits._
    // TODO consider all the possible values that can hold this data like: cr2 tag
    val isoPath = xp"//*[local-name()='ISOSpeedRatings']/Seq/li[1]"
    val xPathResult = xmpAsString.evalXPath[Int](isoPath)
    unliftOrError(xPathResult, s"ISOSpeedRatings is not found in the XMP")
  }

  private def getAperture(xmpAsString: String): BigDecimal = {
    import kantan.xpath.implicits._
    // TODO consider all the possible values that can hold this data like: cr2 tag
    // TODO Can calculate from FNumber if that exists
    val aperturePath = xp"//*[local-name()='ApertureValue']"
    default.aperture match {
      case Some(aperture) => aperture
      case None => {
        val apertureStr = xmpAsString.evalXPath[String](aperturePath)
        calculateFromRationalNumber(unliftOrError(apertureStr, s"ApertureValue is not found in the XMP"))
      }
    }
  }

  private def getExposureBias(xmpAsString: String): BigDecimal = {
    import kantan.xpath.implicits._
    // TODO consider all the possible values that can hold this data like: cr2 tag
    val biasPath = xp"//*[local-name()='ExposureBiasValue']"
    default.exposureBias match {
      case Some(bias) => bias
      case None => {
        val biasStr = unliftOrError(xmpAsString.evalXPath[String](biasPath),
          s"ExposureBiasValue is not found in the XMP")
        calculateFromRationalNumber(biasStr)
      }
    }

  }

  private def unliftOrError[T](result: XPathResult[T], errorReason: String): T = result match {
    case Right(r) => r
    case Left(error: XPathError) => throw new RuntimeException(errorReason, error)
  }

  private def calculateFromRationalNumber(str: String): BigDecimal = {
    import math.BigDecimal._
    val numDen = str.split("/")
    val numerator = BigDecimal(numDen(0), defaultMathContext)
    val denominator = BigDecimal(numDen(1), defaultMathContext)
    numerator / denominator
  }
}

object XmpService {
  def apply(default: Default,
            cli: CLI,
            fsUtil: IOUtil,
            reader: Reader,
            rampMirrorPrevious: MirrorPrevious): XmpService = new XmpService(default, cli, fsUtil, reader, rampMirrorPrevious)
}