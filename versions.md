# Version History and change log

### 1.5 Render in 3D (July 31, 2018)
 - Added a view where some layers are shown in 3D (peel the onion)
 - Added option to fix layer Z position round error from slicer
 - Added option to cancel calculation by opening a new file
 - Updated to the new logo and color
 - Added option to use "Command / Alt" and Arrows to navigate layer and render
 - Changed time calculation to use peel time or offtime (whichever is greatest)

### 1.4 Layer Edit (July 17, 2018)
 - Added option to manually edit layer data.
 - Fixed null pointer when saved during calculation

### 1.3 Navigation, file load and Fix dialog (July 16, 2018)
 - Added an option to auto fix small pixel related islands
 - Added the option to browse backwards for margin and problem areas
 - Now using native file open dialog, so users can use all attached drives
 - Fixed a bug where some overhangs was reported as islands
 - Separated file load and layer calculation workers

### 1.2 Slider and new name: Photon File Validator (July 13, 2018)
 - Changed java memory to min 1GB and max 5GB
 - Added a slider for the layers, so you can navigate quickly through all layers
 - Renamed the application, as validating much better describes the purpose of the application
 - Created new launchers for macOS and Windows, also created new installers

### 1.1 Added Zoom and Margin checks (July 11, 2018)
 - Changed memory handling to allow for 6000+ layer files on 4GB machines
 - Added Zoom from 0.5x to 2x in 5 steps where the middle is no zoom 1:1
 - Added margin checks, to avoid printing to close to the border

### 1.0 First release of Photon File Viewer (July 9, 2018)
 - Open a Photon and ChiTu sliced file
 - Save the file with new settings for exposure, offtime, and bottom layers
 - View file layers in full pixel perfect size
 - View small and large preview images
 - View information on pixels, print time calculation, and the resin use in ml
 - View layers with island, and quickly navigate to these