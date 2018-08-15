# Timelapse Ramping

This project is based on [DevonCrawford](https://github.com/DevonCrawford)'s [Timelapse-Auto-Ramp-Photoshop-Plugin](https://github.com/DevonCrawford/Timelapse-Auto-Ramp-Photoshop-Plugin) project.

## TODO
   * [X] Use predefined EV as well, not just the calculated one. Predefined one overwrites calculated one.
   * [X] Set White Balance as well as the EV is set
   * [X] Improve XMP file writer. It should not be so drastic to overwrite everything. It should just enhance if there is an existing XMP.
   * [X] Create reporting mode - where it does not write the result into XMP file but creates a CSV with all the data that has been calculated.
   * [X] Make app more verbose for users

## Metadata used for Ramping
Information that is necessary to have in the raw image or in the sidecar (XMP) file:
   * Shutter Speed
   * ISO

Information that not necessary but user should provide is they are missing:
   * Aperture
   * Exposure

## Caveats
   * If you exported the XMP files by other than Lightroom, then this app may can not recognize the EXIF values. I'm working on it to handle all the possible EXIF data to get the basic data.
   * Manual lens: If you used manual lens then you can provide the Aperture that you used, the F-number. Please do not modify the aperture during the timelapse, because there is no way right now to add manually a dynamic aperture data.