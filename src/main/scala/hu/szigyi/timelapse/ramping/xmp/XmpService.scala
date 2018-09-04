package hu.szigyi.timelapse.ramping.xmp

import java.io.File

import com.drew.imaging.ImageMetadataReader._
import com.drew.metadata.{Directory, Metadata}
import com.drew.metadata.exif.ExifSubIFDDirectory
import com.drew.metadata.exif.makernotes.CanonMakernoteDirectory
import com.typesafe.scalalogging.LazyLogging
import hu.szigyi.timelapse.ramping.algo.ramp.{Interpolator, RampHelper}
import hu.szigyi.timelapse.ramping.cli.CLI
import hu.szigyi.timelapse.ramping.conf.{DefaultConfig}
import hu.szigyi.timelapse.ramping.io.{IOUtil, Reader, Writer}
import hu.szigyi.timelapse.ramping.model.{XMP, XMPSettings}

class XmpService(defaultConfig: DefaultConfig,
                 cli: CLI,
                 ioUtil: IOUtil,
                 reader: Reader,
                 xmpParser: XmpParser,
                 rampHelper: RampHelper,
                 ramp: Interpolator,
                 writer: Writer) extends LazyLogging {

  def getXMP(imageFile: File): XMP = {
    val metadata: Metadata = readMetadata(imageFile)
    parseXMP(imageFile, metadata)
  }

  def rampExposure(xmps: Seq[XMP]): Seq[BigDecimal] = {
    val EVs: Seq[BigDecimal] = xmps.map(xmp => rampHelper.calculateEV(xmp))
    implicit val f = ramp.buildEVInterpolator(EVs)
    val indicesOfXMPs = (0 to xmps.size - 1)
    indicesOfXMPs.map(index => ramp.interpolateBigDecimal(index)(f))
  }

  def rampWhiteBalance(xmps: Seq[XMP]): Seq[Int] = {
    val f = ramp.buildWBInterpolator(xmps.map(xmp => xmp.settings.whiteBalance))
    val indicesOfXMPs = (0 to xmps.size - 1)
    val rampedWBs = indicesOfXMPs.map(index => ramp.interpolateInt(index)(f))
    rampedWBs
  }

  def flushXMP(xmp: XMP): Unit = {
    val xmpStr = s"""
       |<x:xmpmeta xmlns:x='adobe:ns:meta/' x:xmptk='hu.szigyi.timelapse.ramping'>
       |<rdf:RDF xmlns:rdf='http://www.w3.org/1999/02/22-rdf-syntax-ns#'>
       | <rdf:Description rdf:about=''
       |  xmlns:crs='http://ns.adobe.com/camera-raw-settings/1.0/'>
       |  <crs:Exposure2012>${xmp.settings.exposure}</crs:Exposure2012>
       |  <crs:Temperature>${xmp.settings.whiteBalance}</crs:Temperature>
       | </rdf:Description>
       |</rdf:RDF>
       |</x:xmpmeta>
     """.stripMargin
//    XmpWriter.write(new FileOutputStream(xmp.xmpFilePath), xmp.metadata)
    writer.write(xmp.xmpFilePath, xmpStr)
//    logger.info(s"XMP is created: ${xmp.xmpFilePath}")
  }

  private def parseXMP(imageFile: File, metadata: Metadata) = {
    val exifDir = getMetadataDirectory(imageFile, metadata, classOf[ExifSubIFDDirectory])
    // TODO get the right Dir, you can use the exif tag to find out what the brand of the camera, or use ENV variable by user
    val canonDir = getMetadataDirectory(imageFile, metadata, classOf[CanonMakernoteDirectory])

    val xmpSettings = parseXMPSettings(exifDir, canonDir)

    val xmpFile = ioUtil.replaceExtension(imageFile, "xmp")
    XMP(xmpFile, xmpSettings)
  }

  private def getMetadataDirectory[T <: Directory](file: File, metadata: Metadata, clazz: Class[T]): T = {
    val dir = metadata.getFirstDirectoryOfType(clazz)

    if (null == dir) {
      throw new RuntimeException(
        s"""
           |FATAL ERROR
           |There is no EXIF information in the given picture!
           |${file.getAbsolutePath}
           |
           |Cannot proceed because to set any metadata it requires to have metadata (EXIF) in the picture.
           """.stripMargin)
    } else {
      dir
    }
  }

  private def parseXMPSettings(exifDir: ExifSubIFDDirectory, canonDir: CanonMakernoteDirectory): XMPSettings = {
    val shutterSpeed = xmpParser.getShutterSpeed(exifDir)
    val aperture = xmpParser.getAperture(exifDir)
    val (exposure, isExists) = xmpParser.getExposure(exifDir)
    val iso = xmpParser.getISO(exifDir)
    val wb = xmpParser.getTemperature(canonDir)

    XMPSettings(iso, shutterSpeed, aperture, exposure, isExists, wb)
  }
}

object XmpService {
  def apply(defaultConfig: DefaultConfig,
            cli: CLI,
            ioUtil: IOUtil,
            reader: Reader,
            xmpParser: XmpParser,
            rampHelper: RampHelper,
            ramp: Interpolator,
            writer: Writer): XmpService = new XmpService(defaultConfig, cli, ioUtil, reader, xmpParser, rampHelper, ramp, writer)
}
