package hu.szigyi.timelapse.ramping.xmp

import java.io.File

import com.drew.imaging.ImageMetadataReader._
import com.drew.metadata.Metadata
import com.drew.metadata.exif.{ExifDirectoryBase, ExifSubIFDDirectory}
import com.typesafe.scalalogging.LazyLogging
import hu.szigyi.timelapse.ramping.algo.MirrorPrevious
import hu.szigyi.timelapse.ramping.cli.CLI
import hu.szigyi.timelapse.ramping.io.{IOUtil, Reader, Writer}
import hu.szigyi.timelapse.ramping.model.{XMP, XMPSettings}

class XmpService(cli: CLI,
                 ioUtil: IOUtil,
                 reader: Reader,
                 xmpParser: XmpParser,
                 rampMirrorPrevious: MirrorPrevious,
                 writer: Writer) extends LazyLogging {

  def getXMP(imageFile: File): XMP = {
    val metadata: Metadata = readMetadata(imageFile)
    parseXMP(imageFile, metadata)
  }

  def ramp(standard: XMP, image: XMP): XMP = rampMirrorPrevious.ramp(standard, image)

  def flushRampedXMP(xmp: XMP): Unit = {
    val exposureTag = s"<crs:Exposure2012>${xmp.settings.exposure}</crs:Exposure2012>"
    if (xmp.settings.exposureExistsInXMP) {
      // Update
//      val updatedXMP = xmp.content.replaceFirst("<crs:Exposure2012>(.*)</crs:Exposure2012>", exposureTag)
//      writer.write(xmp.xmpFilePath, updatedXMP)
    } else {
      // Add
      val beginningOfCRS = "xmlns:crs.*[^>]>{1}(\\s)<"
//      val addedXMP = xmp.content.replaceFirst(beginningOfCRS, exposureTag)
//      writer.write(xmp.xmpFilePath, addedXMP)
    }
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
    logger.info(xmpSettings.toString)
    XMP(xmpFile, metadata, xmpSettings)
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
            rampMirrorPrevious: MirrorPrevious,
            writer: Writer): XmpService = new XmpService(cli, ioUtil, reader, xmpParser, rampMirrorPrevious, writer)
}
