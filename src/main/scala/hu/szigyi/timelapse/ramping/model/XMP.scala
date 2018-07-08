package hu.szigyi.timelapse.ramping.model

import java.io.File

case class XMP(filePath: File,
               content: String,
               settings: XMPSettings)

case class XMPSettings(iso: Int,
                       exposure: Int,
                       aperture: Int)
