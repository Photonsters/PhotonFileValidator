package photon.file.parts.sl1;

import photon.file.parts.IFileHeader;
import photon.file.parts.PhotonFileLayer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.regex.Pattern;

public class Sl1FileHeader implements IFileHeader {

    private static Pattern linePattern = Pattern.compile("\\s*=\\s*");

    private float layerHeightMilimeter;
    private float exposureTimeSeconds;
    private float exposureBottomTimeSeconds;
    private float offTimeSeconds;

    private int bottomLayers;
    private int numberOfLayers;

    private int printTimeSeconds;
    private int version;

    // This is used as the base of the image filenames.
    private String jobName;


    public Sl1FileHeader(InputStream entry) throws IOException {
        BufferedReader headerStream = new BufferedReader(new InputStreamReader(entry));
        while( headerStream.ready()) {
            String line = headerStream.readLine();
            String[] components = linePattern.split(line);
            if( components.length != 2 ) {
                throw new IllegalArgumentException("Unparsable line:" + line + " in config.ini");
            }
            /* Sample of config.ini
             * action = print
             * jobDir = 20mm_cube
             * expTime = 12
             * expTimeFirst = 70
             * fileCreationTimestamp = 2020-03-01 at 15:33:48 UTC
             * layerHeight = 0.05
             * materialName = Anycubic White 0.05
             * numFade = 8
             * numFast = 476
             * numSlow = 0
             * printProfile = 0.05 Normal - Copy
             * printTime = 8491.555556
             * printerModel = SL1
             * printerProfile = Original Prusa SL1
             * printerVariant = default
             * prusaSlicerVersion = PrusaSlicer-2.1.1+win64-201912101512
             * usedMaterial = 0.782136
             */
            switch(components[0].toLowerCase()) {
                case "jobdir":
                    jobName = components[1];
                    break;
                case "layerheight":
                    layerHeightMilimeter = Float.parseFloat(components[1]);
                    break;
                case "exptime":
                    exposureTimeSeconds = Float.parseFloat(components[1]);
                    break;
                case "exptimefirst":
                    exposureBottomTimeSeconds = Float.parseFloat(components[1]);
                    break;
                case "numfade":
                    bottomLayers = Integer.parseInt(components[1]);
                    break;
                case "numfast":
                    numberOfLayers = Integer.parseInt(components[1]);
                    break;
                case "printtime":
                    printTimeSeconds = (int)Float.parseFloat(components[1]);
                    break;
                case "numslow":
                case "action":
                case "filecreationtimestamp":
                case "materialname":
                case "printerprofile":
                case "printprofile":
                case "printermodel":
                case "printervariant":
                case "prusaslicerversion":
                case "usedmaterial":
                    /* no-op */
                    break;
                default:
                    throw new IllegalArgumentException("Unknown key in config.ini: " + line);
            }
        }

        // TODO:: add v2 support.
        version = 1;
    }

    @Override
    public String getInformation() {
        return null;
    }

    @Override
    public int getNumberOfLayers() {
        return numberOfLayers;
    }

    @Override
    public int getResolutionY() {
        return 0;
    }

    @Override
    public int getResolutionX() {
        return 0;
    }

    @Override
    public float getBuildAreaX() {
        return 0;
    }

    @Override
    public float getBuildAreaY() {
        return 0;
    }

    @Override
    public int getBottomLayers() {
        return bottomLayers;
    }

    @Override
    public void setBottomLayers(int bottomLayers) {
        this.bottomLayers = bottomLayers;
    }

    @Override
    public float getLayerHeight() {
        return layerHeightMilimeter;
    }

    @Override
    public float getExposureTimeSeconds() {
        return exposureTimeSeconds;
    }

    @Override
    public float getBottomExposureTimeSeconds() {
        return exposureBottomTimeSeconds;
    }

    @Override
    public void setExposureBottomTimeSeconds(float exposureBottomTimeSeconds) {
        this.exposureBottomTimeSeconds = exposureBottomTimeSeconds;
    }

    @Override
    public void setExposureTimeSeconds(float exposureTimeSeconds) {
        this.exposureTimeSeconds = exposureTimeSeconds;
    }

    @Override
    public float getNormalExposure() {
        return exposureTimeSeconds;
    }

    @Override
    public float getOffTimeSeconds() {
        return offTimeSeconds;
    }

    @Override
    public void setOffTimeSeconds(float offTimeSeconds) {
        this.offTimeSeconds = offTimeSeconds;
    }

    @Override
    public int getPrintTimeSeconds() {
        return printTimeSeconds;
    }

    @Override
    public boolean isMirrored() {
        return false;
    }

    @Override
    public boolean hasAA() {
        return false;
    }

    @Override
    public int getAALevels() {
        return 0;
    }

    @Override
    public void setAALevels(int levels, List<PhotonFileLayer> layers) {

    }

    @Override
    public int getVersion() {
        return version;
    }

    @Override
    public void setFileVersion(int i) {
        version = i;
    }

    @Override
    public int getByteSize() {
        return 0;
    }

    @Override
    public void unLink() {

    }
}
