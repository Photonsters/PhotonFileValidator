# Release procedure
This is a checklist for creating a release

### Version Number

- Update the version number (in src/photon/application/MainForm) of frame title located in the main funtion.
- Update the version number in makeapp.sh
- Update the version number in makeappjre.sh
- Update the version number in makewin.iss
- Update the version number in makewinjre.iss
- Update the version number in makewinlauncher.xml

### Build

- Compile and make the out/artifacts/PhotonFileValidator.jar
- Push the jar file to GitHub
- Pull the jar file and test it on both Mac and Windows.

### Jar Version

- Zip the two files in out/artifacts/* and name the file "jar-photonfilevalidator-x.x.zip" (x.x is version number)

### macOS version (on a mac)

- run makeapp.sh and pack the PhotonFileValidator.app to a DMG file, name the file "osx-photonfilevalidator-x.x.dmg"
- run makeappjre.sh and pack the PhotonFileValidator.app to a DMG file, name the file "osx-jre-photonfilevalidator-x.x.dmg"

### Windows version (on a Windows)

- run Launch4j, load install/makewinlauncher.xml and Build Wrapper (will be in out/win/*)
- run Inno Setup Compiler, load install/makewin.iss and compile
- run Inno Setup Compiler, load install/makewinjre.iss and compile

### Test

- unzip and test the jar version
- test the maxOS installers
- test the windows installers

## Create a Release

- Create a new release in GitHub
- Describe the release improvements and bugfixes
- Upload jar, mac and windows installers
- post a picture or video on facebook with a link to the GitHub release page




