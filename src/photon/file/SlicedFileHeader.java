package photon.file;

import photon.file.parts.EParameter;
import photon.file.parts.PhotonFileLayer;
import photon.file.ui.Text;

import java.util.HashMap;
import java.util.List;


/**
 * The base class for a sliced file.
 * Implements the basic fields that are a minimum for a sliced file.
 * subclasses are expected to add their own as needed.
 */
abstract public class SlicedFileHeader {
    // TODO:: Should we break this down into maps by type?
    protected HashMap<EParameter, Object> values;

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

    /**
     * Get a short description of the file. used in the titlebar
     *
     * @return a description of this file.
     */
    public String getInformation() {
        return String.format("T: %.3f", getLayerHeight()) +
                ", E: " + Text.formatSeconds(getExposureTimeSeconds()) +
                ", O: " + Text.formatSeconds(getOffTimeSeconds()) +
                ", BE: " + Text.formatSeconds(getBottomExposureTimeSeconds()) +
                String.format(", BL: %d", getBottomLayers());
    }

    public SlicedFileHeader(SlicedFileHeader other) {
        super();
        values = new HashMap<>(other.values);
    }

    // Default constructor for children
    protected SlicedFileHeader() {
        super();
        values = new HashMap<>();
    }

    // Accessor functions.
    // These are mostly getters, but setters have been added where needed by the code.


    public String getFormattedFloat(EParameter parameter) {
        return formatFloat(getFloat(parameter));
    }
    /**
     * Get a format specific additional parameter, converted to a float
     * @param parameter to look for.
     * @return the value as a float, or 0.0 if it is not set.
     */
    public float getFloat(EParameter parameter) {
        return getFloatOrDefault(parameter, 0f);
    }

    /**
     * Get a format specific additional parameter, converted to an int
     * @param parameter to look for.
     * @return the value as a int, or 0 if it is not set.
     */
    public int getInt(EParameter parameter) {
        return getIntOrDefault(parameter, 0);
    }

    public String getString(EParameter parameter) {
        return (String)get(parameter);
    }

    public boolean has(EParameter parameter) {
        return values.containsKey(parameter);
    }

    /**
     * Get a format specific additional parameter, converted to an int if present,
     * or a default value otherwise
     * @param parameter to look for
     * @param defaultValue if the parameter is not set
     * @return either the value associated with the parameter or the default value, as an int
     */
    public int getIntOrDefault(EParameter parameter, int defaultValue) {
        return (int)values.getOrDefault(parameter, defaultValue);
    }

    /**
     * Get a format specific additional parameter, converted to a float if present,
     * or a default value otherwise
     * @param parameter to look for
     * @param defaultValue if the parameter is not set
     * @return either the value associated with the parameter or the default value, as a float
     */
    public float getFloatOrDefault(EParameter parameter, Float defaultValue) {
        return (float)values.getOrDefault(parameter, defaultValue);
    }

    public Object get(EParameter parameter) {
        return values.get(parameter);
    }

    public Object getOrDefault(EParameter parameter, Object defaultValue) {
        return values.getOrDefault(parameter, defaultValue);
    }

    public short getShort(EParameter parameter) { return (short)values.get(parameter); }

    public void put(EParameter parameter, Object value) {
        // TODO:: Validate the type on put?
        values.put(parameter, value);
    }

    /**
     * Set a parameter if it is not already set, and return whether 'put' was called.
     * Note that it will still return false even if you tried to set it to the value it already had.
     * @param parameter to set
     * @param value to set it to if it is unset
     * @return false iff the value was already set.
     */
    public boolean putIfMissing(EParameter parameter, Object value) {
        if( values.containsKey(parameter) ) return false;

        values.put(parameter, value);
        return true;
    }

    public int getNumberOfLayers() { return getInt(EParameter.layerCount); }

    public int getBottomLayers() { return getInt(EParameter.bottomLayerCount); }

    public void setBottomLayers(int bottomLayers) {
        put(EParameter.bottomLayerCount, bottomLayers);
    }

    public int getResolutionY() { return getInt(EParameter.resolutionY); }

    public int getResolutionX() { return getInt(EParameter.resolutionX); }

    public float getLayerHeight() { return getFloat(EParameter.layerHeightMM); }

    public float getExposureTimeSeconds() { return getFloat(EParameter.exposureTimeS); }

    public void setExposureTimeSeconds(float exposureTimeSeconds) {
        put(EParameter.exposureTimeS, exposureTimeSeconds);
    }

    public float getBottomExposureTimeSeconds() { return getFloat(EParameter.bottomExposureTimeS); }

    public void setExposureBottomTimeSeconds(float exposureBottomTimeSeconds) {
        put(EParameter.bottomExposureTimeS, exposureBottomTimeSeconds);
    }

    public float getOffTimeSeconds() { return getFloat(EParameter.lightOffTimeS); }

    public void setOffTimeSeconds(float offTimeSeconds) {
        put(EParameter.lightOffTimeS, offTimeSeconds);
    }

    abstract public boolean hasAA();
    //TODO:: move to an AA supporting subclass?
    abstract public int getAALevels();
    abstract public void setAALevels(int levels, List<PhotonFileLayer> layers);

    /**
     * Free up any allocated objects to save on ram when we force GC.
     */
    abstract public void unLink();

    /**
     * Whether the layers in this file need to be mirrored.
     * @return true iff the images need to be mirrrored.
     */
    public abstract boolean isMirrored();

    public void forceParameterToInt(EParameter parameter) {
        try{
            int tmp = getInt(parameter);
        } catch (ClassCastException e) {
            // its a float.
            put(parameter, (int)getFloat(parameter));
        }
    }

    public void forceParameterToFloat(EParameter parameter) {
        try{
            float tmp = getInt(parameter);
        } catch (ClassCastException e) {
            // its an int.
            put(parameter, (float)getInt(parameter));
        }
    }
}
