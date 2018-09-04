package hu.szigyi.timelapse.ramping.model

import java.io.File

import com.drew.metadata.Metadata

case class XMP(xmpFilePath: File,
               settings: XMPSettings) {
  override def toString: String =
    s"""
       |XMP($settings, $xmpFilePath)""".stripMargin
}

case class XMPSettings(iso: Int,
                       shutterSpeed: BigDecimal,
                       aperture: BigDecimal,
                       exposure: BigDecimal,
                       exposureExistsInImage: Boolean,
                       whiteBalance: Int) {
  override def toString: String = s"{iso: $iso, shutterSpeed: $shutterSpeed, aperture: $aperture, exposure: $exposure, wb: $whiteBalance}"
}