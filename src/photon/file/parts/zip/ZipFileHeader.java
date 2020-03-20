package photon.file.parts.zip;

import photon.file.SlicedFileHeader;
import photon.file.parts.PhotonFileLayer;
import photon.file.parts.PhotonFilePrintParameters;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ZipFileHeader extends SlicedFileHeader {
    private static final String DEFAULT_START_GCODE = "G21;\n" +
            "G90;\n" +
            "M106 S0;\n" +
            "G28 Z0;\n";
    private static final String DEFAULT_END_GCODE = "M106 S0;\n" +
            "G1 Z150 F25;\n" +
            "M18;";
    private static final String START_GCODE_FORMAT = ";START_GCODE_BEGIN\n%s\n;START_GCODE_END\n";
    private static final String END_GCODE_FORMAT = ";END_GCODE_BEGIN\n%s\n;END_GCODE_END\n";
    private static final String LAYER_GCODE_FORMAT = ";LAYER_START:%d\n" +
            ";currPos:%d\n" +
            "M6054 \"%d.png\";show Image\n" +
            "G0 Z%f F65;\n" +
            "G0 Z%f F150;\n" +
            "G4 P0;\n" +
            "M106 S255;light on\n" +
            "G4 P%d;\n" +
            "M106 S0; light off\n" +
            ";LAYER_END\n";
    //A list of valid config.ini keys which are _not_ covered by 'core' values
    private static final List<String> KNOWN_ADDITIONAL_KEYS = Arrays.asList(
            "fileName",
            "machineType",
            "volume",
            "resin",
            "weight",
            "price",
            "machineZ",
            "projectType",
            "normalDropSpeed",
            "normalLayerLiftHeight",
            "zSlowUpDistance",
            "normalLayerLiftSpeed",
            "bottomLayerLiftHeight",
            "bottomLayerLiftSpeed",
            "bottomLightOffTime",
            "lightOffTime"
    );


    private String start_gcode;
    private String end_gcode;

    public ZipFileHeader(SlicedFileHeader other) {
        super(other);
        for (String key : KNOWN_ADDITIONAL_KEYS) {
            if (other.containsKey(key)) {
                put(key, other.get(key));
            }
        }
        start_gcode = DEFAULT_START_GCODE;
        end_gcode = DEFAULT_END_GCODE;
    }

    public ZipFileHeader(InputStream entry) throws IOException {
        // TODO:: I'm not 100% sure that this is appropriate for a non-photon file, but it _does_ have all the fields.
        version = 2;

        BufferedReader headerStream = new BufferedReader(new InputStreamReader(entry));
        String[] components;
        String gcode_line, line;

        while (headerStream.ready()) {
            line = headerStream.readLine();
            //skip blanks
            if (line.length() == 0) continue;

            components = line.split(":");
            // remove leading ;
            components[0] = components[0].substring(1);

            //No, I don't know why two of the bottom values are doubled up with different keys.
            switch (components[0]) {
                case "layerHeight":
                    layerHeightMilimeter = Float.parseFloat(components[1]);
                    continue;
                case "normalExposureTime":
                    exposureTimeSeconds = Float.parseFloat(components[1]);
                    continue;
                case "bottomLayerExposureTime":
                case "bottomLayExposureTime":
                    exposureBottomTimeSeconds = Float.parseFloat(components[1]);
                    continue;
                case "lightOffTime":
                    offTimeSeconds = Float.parseFloat(components[1]);
                    continue;
                case "bottomLayerCount":
                case "bottomLayCount":
                    bottomLayers = Integer.parseInt(components[1]);
                    continue;
                case "totalLayer":
                    numberOfLayers = Integer.parseInt(components[1]);
                    continue;
                case "estimatedPrintTime":
                    printTimeSeconds = (int) Float.parseFloat(components[1]);
                    continue;
                case "resolutionX":
                    resolutionX = Integer.parseInt(components[1]);
                    continue;
                case "resolutionY":
                    resolutionY = Integer.parseInt(components[1]);
                    continue;
                case "machineX":
                    buildAreaX = Float.parseFloat(components[1]);
                    continue;
                case "machineY":
                    buildAreaY = Float.parseFloat(components[1]);
                    continue;
                case "mirror":
                    isMirrored = Integer.parseInt(components[1]) > 0;
                    continue;
                case "START_GCODE_BEGIN":
                    // TODO:: check for EOF?
                    gcode_line = headerStream.readLine();
                    ;
                    start_gcode = "";
                    while (!gcode_line.startsWith(";START_GCODE_END")) {
                        start_gcode += String.format("%s\n", gcode_line);
                        gcode_line = headerStream.readLine();
                    }
                    continue;
                case "LAYER_START":
                    // TODO:: check for EOF?
                    gcode_line = headerStream.readLine();
                    ;
                    // Skip _all_ the layer stuff. We can calculate it ourselves.
                    while (!gcode_line.startsWith(";END_GCODE_BEGIN")) {
                        gcode_line = headerStream.readLine();
                    }
                    gcode_line = headerStream.readLine();
                    end_gcode = "";
                    while (!gcode_line.startsWith(";END_GCODE_END")) {
                        end_gcode += String.format("%s\n", gcode_line);
                        gcode_line = headerStream.readLine();
                    }
                    continue;
                default:
                    // no-op - handled below
                    break;
            }
            if (KNOWN_ADDITIONAL_KEYS.contains(components[0])) {
                put(components[0], components[1]);
            } else {
                throw new IllegalArgumentException("Unknown key in config.ini: " + line);
            }
        }
    }

    /**
     * Generates the gcode for a single layer.
     * While this could be done in a single String.format call, it is relatively complex and
     * best done long hand (and will hardly take any extra time).
     *
     * @param layerNumber the layer to write (0 indexed)
     * @return a byte array of the gcode to print this layer.
     */
    private byte[] generateLayerGcode(int layerNumber) {
        float curHeight = layerNumber * layerHeightMilimeter;
        // TODO:: standardise additional parameters
        float liftHeight, exposureTimeS, liftSpeed, dropSpeed, lightOffTime;

        dropSpeed = getFloatOrDefault("normalDropSpeed", PhotonFilePrintParameters.DEFAULT_SPEED);

        if (layerNumber < bottomLayers) {
            // TODO:: extract these to constants
            liftHeight = getFloatOrDefault("bottomLayerLiftHeight", PhotonFilePrintParameters.DEFAULT_DISTANCE);
            exposureTimeS = exposureBottomTimeSeconds;
            liftSpeed = getFloatOrDefault("bottomLayerLiftSpeed", PhotonFilePrintParameters.DEFAULT_SPEED);
            lightOffTime = getFloatOrDefault("bottomLightOffTime", PhotonFilePrintParameters.DEFAULT_LIGHT_OFF_DELAY);

        } else {
            liftHeight = getFloatOrDefault("normalLayerLiftHeight", PhotonFilePrintParameters.DEFAULT_DISTANCE);
            exposureTimeS = exposureTimeSeconds;
            liftSpeed = getFloatOrDefault("normalLayerLiftSpeed", PhotonFilePrintParameters.DEFAULT_SPEED);
            lightOffTime = getFloatOrDefault("lightOffTime", PhotonFilePrintParameters.DEFAULT_LIGHT_OFF_DELAY);
        }
        String result = String.format(";LAYER_START:%d\n;currPos:%s\n", layerNumber, SlicedFileHeader.formatFloat(curHeight))
                + String.format("M6054 \"%d.png\";show Image\n", layerNumber + 1);
            result += String.format("G0 Z%s F%d;\n", SlicedFileHeader.formatFloat(liftHeight + curHeight), (int)liftSpeed);
            result += String.format("G0 Z%s F%d;\n", SlicedFileHeader.formatFloat(curHeight + layerHeightMilimeter), (int)dropSpeed);
            result += String.format("G4 P%d;\n", (int)(lightOffTime*1000));
            // TODO:: Allow for different light strengths.
            result += "M106 S255;light on\n";
            result += String.format("G4 P%d;\n", (int)(exposureTimeS*1000));
            result += "M106 S0; light off\n;LAYER_END\n";

        return result.getBytes();
    }

    public void write(OutputStream output) throws IOException {
        // First the core values.
        String outputString = String.format(";normalExposureTime:%s\n", exposureTimeSeconds)
                + String.format(";bottomLayExposureTime:%s\n", exposureBottomTimeSeconds)
                + String.format(";bottomLayerExposureTime:%s\n", exposureBottomTimeSeconds)
                + String.format(";layerHeight:%.3f\n", layerHeightMilimeter)
                + String.format(";bottomLayCount:%d\n", bottomLayers)
                + String.format(";bottomLayerCount:%d\n", bottomLayers)
                + String.format(";totalLayer:%d\n", numberOfLayers)
                + String.format(";estimatedPrintTime:%d\n", printTimeSeconds);
        output.write(outputString.getBytes());

        for (Map.Entry<String, String> entry : entrySet()) {
            if (KNOWN_ADDITIONAL_KEYS.contains(entry.getKey())) {
                output.write(String.format(";%s:%s\n", entry.getKey(), entry.getValue()).getBytes());
            }
        }

        // Now the fun bit - we actually have to write the GCODE
        output.write(String.format(START_GCODE_FORMAT, start_gcode).getBytes());

        for (int i = 0; i < numberOfLayers; i++) {
            output.write(generateLayerGcode(i));
        }

        output.write(String.format(END_GCODE_FORMAT, end_gcode).getBytes());

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
}
