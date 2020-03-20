package photon.file;

import photon.file.parts.PhotonFileLayer;
import photon.file.ui.Text;

import java.util.HashMap;
import java.util.List;

/**
 * The base class for a sliced file.
 * Implements the basic fields that are a minimum for a sliced file.
 * Extends HashMap to store additional fields, with a bunch of bonus accessors to preconvert values.
 * subclasses are expected to add their own as needed.
 */
abstract public class SlicedFileHeader extends HashMap<String, String> {

    /**
     * Tidies up a float to remove trailing 0s.
     * @param in float to format
     * @return the float, formatted as a string, without trailing 0s
     */
    public static String formatFloat(float in) {
        // This is not the fastest of code, but it _does_ work and will be called rarely enough.
        String result = String.valueOf(in);
        int idx=0;
        while( idx < result.length() && result.charAt(idx)!='.') idx++;
        idx+=2;
        if( idx >= result.length() ) return result; // nothing to chop

        while( idx < result.length() && result.charAt(idx)!='0') idx++;
        if( idx >= result.length() ) return result; // nothing to chop

        result = result.substring(0, idx);
        return result;
    }

    protected float layerHeightMilimeter;
    protected float exposureTimeSeconds;
    protected float exposureBottomTimeSeconds;
    protected float offTimeSeconds;

    protected int bottomLayers;
    protected int numberOfLayers;

    protected int printTimeSeconds;
    protected int version;

    protected int resolutionX, resolutionY;
    protected float buildAreaX, buildAreaY;

    protected boolean isMirrored;

    protected int headerSize;

    /**
     * Get a short description of the file. used in the titlebar
     *
     * @return a description of this file.
     */
    public String getInformation() {
        return String.format("T: %.3f", layerHeightMilimeter) +
                ", E: " + Text.formatSeconds(exposureTimeSeconds) +
                ", O: " + Text.formatSeconds(offTimeSeconds) +
                ", BE: " + Text.formatSeconds(exposureBottomTimeSeconds) +
                String.format(", BL: %d", bottomLayers);
    }

    public SlicedFileHeader(SlicedFileHeader other) {
        super();
        layerHeightMilimeter = other.layerHeightMilimeter;
        exposureTimeSeconds = other.exposureTimeSeconds;
        exposureBottomTimeSeconds = other.exposureBottomTimeSeconds;
        offTimeSeconds = other.offTimeSeconds;
        bottomLayers = other.bottomLayers;
        numberOfLayers = other.numberOfLayers;
        printTimeSeconds = other.printTimeSeconds;
        version = other.version;
        resolutionX = other.resolutionX;
        resolutionY = other.resolutionY;
        buildAreaX = other.buildAreaX;
        buildAreaY = other.buildAreaY;
        headerSize = other.headerSize;
    }

    // Default constructor for children
    protected SlicedFileHeader() {
        super();
    }

    // Accessor functions.
    // These are mostly getters, but setters have been added where needed by the code.

    /**
     * Get a format specific additional parameter, converted to a float
     * @param parameter to look for.
     * @return the value as a float, or 0.0 if it is not set.
     */
    public Float getFloat(String parameter) {
        return getFloatOrDefault(parameter, 0f);
    }

    /**
     * Get a format specific additional parameter, converted to an int
     * @param parameter to look for.
     * @return the value as a int, or 0 if it is not set.
     */
    public int getInt(String parameter) {
        return getIntOrDefault(parameter, 0);
    }

    /**
     * Get a format specific additional parameter, converted to an int if present,
     * or a default value otherwise
     * @param parameter to look for
     * @param defaultValue if the parameter is not set
     * @return either the value associated with the parameter or the default value, as an int
     */
    public int getIntOrDefault(String parameter, int defaultValue) {
        if (containsKey(parameter)) {
            return Integer.parseInt(get(parameter));
        }
        return defaultValue;
    }

    /**
     * Get a format specific additional parameter, converted to a float if present,
     * or a default value otherwise
     * @param parameter to look for
     * @param defaultValue if the parameter is not set
     * @return either the value associated with the parameter or the default value, as a float
     */
    public Float getFloatOrDefault(String parameter, Float defaultValue) {
        if( containsKey(parameter) )
        {
            return Float.parseFloat(get(parameter));
        }
        return defaultValue;
    }

    public int getNumberOfLayers() { return numberOfLayers; }

    public int getBottomLayers() { return bottomLayers; }

    public void setBottomLayers(int bottomLayers) {
        this.bottomLayers = bottomLayers;
    }

    public int getResolutionY() { return resolutionY; }

    public int getResolutionX() { return resolutionX; }

    public float getBuildAreaX() { return buildAreaX; }

    public float getBuildAreaY() { return buildAreaY; }

    public float getLayerHeight() { return layerHeightMilimeter; }


    public float getExposureTimeSeconds() { return exposureTimeSeconds; }

    public void setExposureTimeSeconds(float exposureTimeSeconds) {
        this.exposureTimeSeconds = exposureTimeSeconds;
    }

    public float getBottomExposureTimeSeconds() { return exposureBottomTimeSeconds; }

    public void setExposureBottomTimeSeconds(float exposureBottomTimeSeconds) {
        this.exposureBottomTimeSeconds = exposureBottomTimeSeconds;
    }

    //TODO:: Why is this duplicated? can we remove?
    public float getNormalExposure() { return exposureTimeSeconds; }


    public float getOffTimeSeconds() { return offTimeSeconds; }


    public void setOffTimeSeconds(float offTimeSeconds) {
        this.offTimeSeconds = offTimeSeconds;
    }

    public int getPrintTimeSeconds() { return printTimeSeconds; }

    public boolean isMirrored() {
        return isMirrored;
    }

    abstract public boolean hasAA();

    abstract public int getAALevels();

    //TODO:: move to an AA supporting subclass?
    abstract public void setAALevels(int levels, List<PhotonFileLayer> layers);

    public int getVersion() { return version; }

    public void setFileVersion(int i) {
        this.version = version;
    }


    public int getByteSize() { return headerSize; }


    /**
     * Free up any allocated objects to save on ram when we force GC.
     */
    abstract public void unLink();
}
