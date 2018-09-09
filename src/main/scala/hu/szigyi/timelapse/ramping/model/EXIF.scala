package hu.szigyi.timelapse.ramping.model

import java.io.File

import com.drew.metadata.Metadata

case class EXIF(xmpFilePath: File,
                settings: EXIFSettings) {
  override def toString: String =
    s"""
       |XMP($settings, $xmpFilePath)""".stripMargin
}

case class EXIFSettings(iso: Int,
                        shutterSpeed: BigDecimal,
                        aperture: BigDecimal,
                        exposure: BigDecimal,
                        exposureExistsInImage: Boolean,
                        temperature: Int,
                        tint: Int) {
  override def toString: String = s"{iso: $iso, shutterSpeed: $shutterSpeed, aperture: $aperture, exposure: $exposure, temperature: $temperature}"
}