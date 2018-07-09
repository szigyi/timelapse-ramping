# Timelapse Ramping

This project is based on [DevonCrawford](https://github.com/DevonCrawford)'s [Timelapse-Auto-Ramp-Photoshop-Plugin](https://github.com/DevonCrawford/Timelapse-Auto-Ramp-Photoshop-Plugin) project.

## Metadata used for Ramping
Information that is necessary to have in the raw image or in the sidecar (XMP) file:
   * Shutter Speed
   * ISO

Information that not necessary but user should provide is they are missing:
   * Aperture
   * Exposure Bias

## Caveats
   * If you exported the XMP files by other than Lightroom, then this app may can not recognize the EXIF values. I'm working on it to handle all the possible EXIF data to get the basic data.
   * Manual lens: If you used manual lens then you can provide the Aperture that you used, the F-number. Please do not modify the aperture during the timelapse, because there is no way right now to add manually a dynamic aperture data.