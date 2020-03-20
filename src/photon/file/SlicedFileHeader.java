package photon.file;

import photon.file.parts.PhotonFileLayer;
import photon.file.ui.Text;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The base class for a sliced file. Implements the basic fields that are a minimum for a sliced file.
 * subclasses are expected to add their own as needed.
 */
abstract public class SlicedFileHeader {
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
     * A store of all the additional, format specific parameters.
     */
    protected Map<String, String> additionalParameters;

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
        // Note the default type _does_ not have any additional parameters.
        additionalParameters = new HashMap<>();
    }

    // Default constructor for children
    protected SlicedFileHeader() {
        additionalParameters = new HashMap<>();
    }

    // Accessor functions.
    // These are mostly getters, but setters have been added where needed by the code.

    /**
     * Get a format specific additional parameter.
     * @param parameter to look for
     * @return the value for the parameter, or NULL if not present
     */
    public String getParam(String parameter) {
        return additionalParameters.get(parameter);
    }
    /**
     * Get a format specific additional parameter, with a default if it is missing
     * @param parameter to look for.
     * @param defaultValue to return if parameter does not exist
     * @return the value for the parameter, or defaultValue if not present
     */
    public String getParamOrDefault(String parameter, String defaultValue) {
        return additionalParameters.getOrDefault(parameter, defaultValue);
    }

    /**
     * Check if the file has the specified parameter
     * @param parameter to check for
     * @return true iff it has been set for this file
     */
    public boolean hasParam(String parameter) {
        return additionalParameters.containsKey(parameter);
    }

    /**
     * Get a format specific additional parameter, converted to a float
     * @param parameter to look for.
     * @return the value as a float, or 0.0 if it is not set.
     */
    public Float getFloatParam(String parameter) {
        return getFloatParamOrDefault(parameter, 0f);
    }

    /**
     * Get a format specific additional parameter, converted to an int
     * @param parameter to look for.
     * @return the value as a int, or 0 if it is not set.
     */
    public int getIntParam(String parameter) {
        if (additionalParameters.containsKey(parameter)) {
            return Integer.parseInt(additionalParameters.get(parameter));
        }
        return 0;
    }

    /**
     * Get a format specific additional parameter, converted to a float if present,
     * or a default value otherwise
     * @param parameter to look for (case insensitive)
     * @param defaultValue if the parameter is not set
     * @return either the value associated with the parameter or the default value, as a float
     */
    public Float getFloatParamOrDefault(String parameter, Float defaultValue) {
        if( additionalParameters.containsKey(parameter) )
        {
            return Float.parseFloat(additionalParameters.get(parameter));
        }
        return defaultValue;
    }

    /**
     * Set a given additional parameter.
     * @param parameter to set
     * @param value to set
     */
    public void setParam(String parameter, String value) {
        additionalParameters.put(parameter, value);
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

    abstract public SlicedFileHeader fromIFileHeader(SlicedFileHeader other);

}
