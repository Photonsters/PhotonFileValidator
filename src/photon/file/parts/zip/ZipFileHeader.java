package photon.file.parts.zip;

import photon.file.SlicedFileHeader;
import photon.file.parts.PhotonFileLayer;

import java.io.*;
import java.util.List;

public class ZipFileHeader extends SlicedFileHeader {
    private static final String DEFAULT_START_GCODE =
            "G21;\n" +
            "G90;\n" +
            "M106 S0;\n" +
            "G28 Z0;\n";
    private static final String DEFAULT_END_GCODE =
            "M106 S0;\n" +
            "G1 Z150 F25;\n" +
            "M18;";


    // zip specific fields
    private String file_name;
    private String machine_type;
    private String volume;
    private String resin;
    private String weight;
    private String price;
    private String machine_x, machine_y, machine_z;



    private String start_gcode;
    private String end_gcode;

    public ZipFileHeader(SlicedFileHeader other) {
        super(other);
        start_gcode = DEFAULT_START_GCODE;
        end_gcode = DEFAULT_END_GCODE;
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
    public boolean hasAA() {
        return false;
    }

    @Override
    public int getAALevels() {
        return 0;
    }

    @Override
    public void setAALevels(int levels, List<PhotonFileLayer> layers) {
        //noop
    }


    @Override
    public void unLink() {
        // no-op
    }

    public SlicedFileHeader fromIFileHeader(SlicedFileHeader other) {
        return new ZipFileHeader(other);
    }
}
