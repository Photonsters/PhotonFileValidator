package photon.file.parts.zip;

import photon.file.parts.IFileHeader;
import photon.file.parts.PhotonFileLayer;

import java.io.*;
import java.util.List;
import java.util.regex.Pattern;

public class ZipFileHeader implements IFileHeader {
    private static final String DEFAULT_START_GCODE =
            "G21;\n" +
            "G90;\n" +
            "M106 S0;\n" +
            "G28 Z0;\n";
    private static final String DEFAULT_END_GCODE =
            "M106 S0;\n" +
            "G1 Z150 F25;\n" +
            "M18;";
  //  private static Pattern linePattern = Pattern.compile(";\\s*:.*");

    private float layerHeightMilimeter;
    private float exposureTimeSeconds;
    private float exposureBottomTimeSeconds;
    private float offTimeSeconds;

    private int bottomLayers;
    private int numberOfLayers;

    private int printTimeSeconds;
    private int version;

    private int resolutionX;
    private int resolutionY;

    private String start_gcode;
    private String end_gcode;

    public ZipFileHeader(IFileHeader other) {
        layerHeightMilimeter = other.getLayerHeight();
        exposureTimeSeconds = other.getExposureTimeSeconds();
        exposureBottomTimeSeconds = other.getBottomExposureTimeSeconds();
        offTimeSeconds = other.getOffTimeSeconds();
        bottomLayers = other.getBottomLayers();
        numberOfLayers = other.getNumberOfLayers();
        printTimeSeconds = other.getPrintTimeSeconds();
        resolutionX = other.getResolutionX();
        resolutionY = other.getResolutionY();
        start_gcode = DEFAULT_START_GCODE;
        end_gcode = DEFAULT_END_GCODE;
        version = 1;
    }

    public ZipFileHeader(InputStream entry) throws IOException {
        BufferedReader headerStream = new BufferedReader(new InputStreamReader(entry));
        String[] components;
        while( headerStream.ready()) {
            String line = headerStream.readLine();
            components = line.split(":");
            switch (components[0].toLowerCase()) {
                case ";layerheight":
                    layerHeightMilimeter = Float.parseFloat(components[1]);
                    break;
                case ";normalexposuretime":
                    exposureTimeSeconds = Float.parseFloat(components[1]);
                    break;
                case ";bottomLayerexposuretime":
                    exposureBottomTimeSeconds = Float.parseFloat(components[1]);
                    break;
                case ";lightofftime":
                    offTimeSeconds = Float.parseFloat(components[1]);
                    break;
                case ";bottomlayercount":
                    bottomLayers = Integer.parseInt(components[1]);
                    break;
                case ";totallayer":
                    numberOfLayers = Integer.parseInt(components[1]);
                    break;
                case ";estimatedprinttime":
                    printTimeSeconds = (int)Float.parseFloat(components[1]);
                    break;
                case ";resolutionx":
                    resolutionX = Integer.parseInt(components[1]);
                    break;
                case ";resolutiony":
                    resolutionY = Integer.parseInt(components[1]);
                    break;
                default:
                    // TODO:: implement the rest
                    break;
            }
        }
        // TODO:: this is not accurate, it's really a v2. Need to consider this.
        version = 1;
    }

    public void write(OutputStream output) throws IOException {
        throw new UnsupportedOperationException("unimplemented");
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
        return resolutionY;
    }

    @Override
    public int getResolutionX() {
        return resolutionX;
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
        return 1;
    }

    @Override
    public void setAALevels(int levels, List<PhotonFileLayer> layers) { /* TODO */ }

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
    public void unLink() { /* no-op */ }

    @Override
    public IFileHeader fromIFileHeader(IFileHeader other) {
        return new ZipFileHeader(other);
    }
}
