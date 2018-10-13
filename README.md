# Timelapse Ramping

This project is based on [DevonCrawford](https://github.com/DevonCrawford)'s [Timelapse-Auto-Ramp-Photoshop-Plugin](https://github.com/DevonCrawford/Timelapse-Auto-Ramp-Photoshop-Plugin) project.

## How it works
Input
   * A string that is a path to the folder where the images are
   * Optional environment variables or program arguments like
      * Aperture if you used manual lens
      * A flag which indicates that you want to interpolate the white balance as well

Main process
   * Finds all the images in the folder
      * There is a list of file extensions in the app to filter out the non-images
      * Images are sorted by their name as string. Therefore, if sequence of the file is not '0001', prefixed with zeros, then the order could be wrong
   * Reads the metadata from the images, one by one
   * Builds the interpolator for the series of images' Exposure Value (EV)
   * Interpolates the EVs for every image
   * Check if white balance interpolation is required
      * If yes, then it build the interpolator for the series of images' White Balance
      * Interpolates the White Balances for every image
   * Creates the XMP (xml) files for every image where EV or WB has changed
   * Writes the XMP files next to the images in the folder

Output
   * XMP sidecar file for every image which was ramped

## TODO
   * [ ] Add anti-flickering - for EV and Temperature as well
   * [ ] Create reporting mode - it does not write the result into XMP file but creates a CSV with all the data that has been calculated - mark every line, image which was used as key for ramping!.
   * [ ] Use run script which uses the built jar
   * [ ] Use predefined EV as well, not just the calculated one. Predefined one overwrites calculated one.
   * [ ] Set White Balance as well as the EV is set
   * [ ] Improve XMP file writer. It should not be so drastic to overwrite everything. It should just enhance if there is an existing XMP.
   * [ ] Make app more verbose for users

## Metadata used for Ramping
Information that is necessary to be in the raw image:
   * Shutter Speed
   * ISO

Information that is not necessary
   * The user should provide if they are missing:
      * Aperture
   * The user should not provide if they are missing:
      * Exposure

## Caveats
   * Manual lens: If you used manual lens then you can provide the Aperture that you used, the F-number. Please do not modify the aperture during the timelapse, because there is no way right now to add manually a dynamic aperture data.