package hu.szigyi.timelapse.ramping.xmp

import java.io.File

import breeze.interpolation.LinearInterpolator
import com.drew.imaging.ImageMetadataReader._
import com.drew.metadata.Metadata
import com.drew.metadata.exif.ExifSubIFDDirectory
import com.typesafe.scalalogging.LazyLogging
import hu.szigyi.timelapse.ramping.algo.ramp.{Interpolator, RampHelper}
import hu.szigyi.timelapse.ramping.cli.CLI
import hu.szigyi.timelapse.ramping.io.{IOUtil, Reader, Writer}
import hu.szigyi.timelapse.ramping.model.{XMP, XMPSettings}

class XmpService(cli: CLI,
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
    val EVs: Seq[BigDecimal] = xmps.map(xmp => rampHelper.toEV(xmp))
    implicit val f = ramp.buildInterpolator(EVs)
    val indicesOfXMPs = (0 to xmps.size - 1)
    indicesOfXMPs.map(index => ramp.rampExposure(index)(f))
  }

  def flushXMP(xmp: XMP): Unit = {
    val xmpStr = s"""
       |<x:xmpmeta xmlns:x='adobe:ns:meta/' x:xmptk='hu.szigyi.timelapse.ramping'>
       |<rdf:RDF xmlns:rdf='http://www.w3.org/1999/02/22-rdf-syntax-ns#'>
       | <rdf:Description rdf:about=''
       |  xmlns:crs='http://ns.adobe.com/camera-raw-settings/1.0/'>
       |  <crs:Exposure2012>${xmp.settings.exposure}</crs:Exposure2012>
       | </rdf:Description>
       |</rdf:RDF>
       |</x:xmpmeta>
     """.stripMargin
//    XmpWriter.write(new FileOutputStream(xmp.xmpFilePath), xmp.metadata)
    writer.write(xmp.xmpFilePath, xmpStr)
//    logger.info(s"XMP is created: ${xmp.xmpFilePath}")
  }

  private def parseXMP(imageFile: File, metadata: Metadata) = {
    val exifDir = metadata.getFirstDirectoryOfType(classOf[ExifSubIFDDirectory])
    if (null == exifDir) {
      throw new RuntimeException(
        s"""
           |FATAL ERROR
           |There is no EXIF information in the given picture!
           |${imageFile.getAbsolutePath}
           |
           |Cannot proceed because to set any metadata it requires to have any metadata (EXIF) for the picture.
           """.stripMargin)
    }

    val xmpFile = ioUtil.replaceExtension(imageFile, ".xmp")
    val xmpSettings = parseXMPSettings(exifDir)
    XMP(xmpFile, xmpSettings)
  }

  private def parseXMPSettings(exifDir: ExifSubIFDDirectory): XMPSettings = {

    val shutterSpeed = xmpParser.getShutterSpeed(exifDir)
    val aperture = xmpParser.getAperture(exifDir)
    val (exposure, isExists) = xmpParser.getExposure(exifDir)
    val iso = xmpParser.getISO(exifDir)

    XMPSettings(iso, shutterSpeed, aperture, exposure, isExists)
  }
}

object XmpService {
  def apply(cli: CLI,
            ioUtil: IOUtil,
            reader: Reader,
            xmpParser: XmpParser,
            rampHelper: RampHelper,
            ramp: Interpolator,
            writer: Writer): XmpService = new XmpService(cli, ioUtil, reader, xmpParser, rampHelper, ramp, writer)
}
