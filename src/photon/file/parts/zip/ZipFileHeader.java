package photon.file.parts.zip;

import photon.file.SlicedFileHeader;
import photon.file.parts.EParameter;
import photon.file.parts.PhotonFileLayer;
import photon.file.parts.PhotonProjectType;

import java.io.*;
import java.util.List;

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

    public ZipFileHeader(SlicedFileHeader other) {
        super(other);
        putIfMissing(EParameter.startGCode, DEFAULT_START_GCODE);
        putIfMissing(EParameter.endGCode, DEFAULT_END_GCODE);
        putIfMissing(EParameter.fileName, "default?"); // TODO: pass this in?

        putIfMissing(EParameter.bottomLightPWM, EParameter.DEFAULT_PWM);
        putIfMissing(EParameter.lightPWM, EParameter.DEFAULT_PWM);
        forceParameterToFloat(EParameter.printTimeS);
        // TODO:: add rest of expected fields.
    }

    public ZipFileHeader(InputStream entry) throws IOException {
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
                    put(EParameter.layerHeightMM, Float.parseFloat(components[1]));
                    break;
                case "normalExposureTime":
                    put(EParameter.exposureTimeS, Float.parseFloat(components[1]));
                    break;
                case "bottomLayerExposureTime": // yes, it duplicates some fields.
                case "bottomLayExposureTime":
                    put(EParameter.bottomExposureTimeS, Float.parseFloat(components[1]));
                    break;
                case "lightOffTime":
                    put(EParameter.lightOffTimeS, Float.parseFloat(components[1]));
                    break;
                case "bottomLayerCount":
                case "bottomLayCount":
                    put(EParameter.bottomLayerCount, Integer.parseInt(components[1]));
                    break;
                case "totalLayer":
                    put(EParameter.layerCount, Integer.parseInt(components[1]));
                    break;
                case "estimatedPrintTime":
                    put(EParameter.printTimeS, Float.parseFloat(components[1]));
                    break;
                case "resolutionX":
                    put(EParameter.resolutionX, Integer.parseInt(components[1]));
                    break;
                case "resolutionY":
                    put(EParameter.resolutionY, Integer.parseInt(components[1]));
                    break;
                case "machineX":
                    put(EParameter.bedXMM, Float.parseFloat(components[1]));
                    break;
                case "machineY":
                    put(EParameter.bedYMM, Float.parseFloat(components[1]));
                    break;
                case "machineZ":
                    put(EParameter.bedZMM, Float.parseFloat(components[1]));
                    break;
                case "mirror":
                    boolean isMirrored = Integer.parseInt(components[1]) > 0;
                    if (isMirrored) {
                        put(EParameter.projectType, PhotonProjectType.lcdMirror);
                    } else {
                        put(EParameter.projectType, PhotonProjectType.cast);
                    }
                    break;
                case "START_GCODE_BEGIN":
                    // TODO:: check for EOF?
                    gcode_line = headerStream.readLine();
                    ;
                    String start_gcode = "";
                    while (!gcode_line.startsWith(";START_GCODE_END")) {
                        start_gcode += String.format("%s\n", gcode_line);
                        gcode_line = headerStream.readLine();
                    }
                    put(EParameter.startGCode, start_gcode);
                    break;
                case "LAYER_START":
                    // TODO:: check for EOF?
                    gcode_line = headerStream.readLine();
                    ;
                    // Skip _all_ the layer stuff. We can calculate it ourselves.
                    while (!gcode_line.startsWith(";END_GCODE_BEGIN")) {
                        gcode_line = headerStream.readLine();
                    }
                    gcode_line = headerStream.readLine();
                    String end_gcode = "";
                    while (!gcode_line.startsWith(";END_GCODE_END")) {
                        end_gcode += String.format("%s\n", gcode_line);
                        gcode_line = headerStream.readLine();
                    }
                    put(EParameter.endGCode, end_gcode);
                    break;
                case "fileName":
                    put(EParameter.fileName, components[1]);
                    break;
                case "dropSpeed":
                case "normalDropSpeed":
                    put(EParameter.retractSpeed, Float.parseFloat(components[1]));
                    break;
                case "zSlowUpDistance":
                    put(EParameter.zSlowUpDistance, Float.parseFloat(components[1]));
                    break;
                case "price":
                    put(EParameter.cost, Float.parseFloat(components[1]));
                    break;
                default:
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
        float curHeight = layerNumber * getFloat(EParameter.layerHeightMM);

        float liftHeight, exposureTimeS, liftSpeed, dropSpeed, lightOffTime;
        int lightPWM;

        dropSpeed = getFloat(EParameter.retractSpeed);

        if (layerNumber < getInt(EParameter.bottomLayerCount)) {
            liftHeight = getFloat(EParameter.bottomLiftDistance);
            exposureTimeS = getFloat(EParameter.bottomExposureTimeS);
            liftSpeed = getFloat(EParameter.bottomLiftSpeed);
            lightOffTime = getFloat(EParameter.bottomLightOffTimeS);
            lightPWM = getShortOrDefault(EParameter.bottomLightPWM, EParameter.DEFAULT_PWM);
        } else {
            liftHeight = getFloat(EParameter.liftDistance);
            exposureTimeS = getFloat(EParameter.exposureTimeS);
            liftSpeed = getFloat(EParameter.liftSpeed);
            lightOffTime = getFloat(EParameter.lightOffTimeS);
            lightPWM = getShortOrDefault(EParameter.lightPWM, EParameter.DEFAULT_PWM);
        }
        String result = String.format(";LAYER_START:%d\n;currPos:%s\n", layerNumber, SlicedFileHeader.formatFloat(curHeight))
                + String.format("M6054 \"%d.png\";show Image\n", layerNumber + 1);
        result += String.format("G0 Z%s F%d;\n", SlicedFileHeader.formatFloat(liftHeight + curHeight), (int) liftSpeed);
        result += String.format("G0 Z%s F%d;\n", SlicedFileHeader.formatFloat(curHeight + getFloat(EParameter.layerHeightMM)), (int) dropSpeed);
        result += String.format("G4 P%d;\n", (int) (lightOffTime * 1000));
        // AFAICT, the Zip format doesn't actually allow for varying the PWM, but if you've converted it from photon, it may have it...
        result += String.format("M106 S%d;light on\n", lightPWM);
        result += String.format("G4 P%d;\n", (int) (exposureTimeS * 1000));
        result += "M106 S0; light off\n;LAYER_END\n";

        return result.getBytes();
    }

    public void write(OutputStream output) throws IOException {
        // First the core values.
        String outputString = String.format(";normalExposureTime:%f\n", getFloat(EParameter.exposureTimeS))
                + String.format(";bottomLayExposureTime:%f\n", getFloat(EParameter.bottomExposureTimeS))
                + String.format(";bottomLayerExposureTime:%f\n", getFloat(EParameter.bottomExposureTimeS))
                + String.format(";layerHeight:%.3f\n", getFloat(EParameter.layerHeightMM))
                + String.format(";bottomLayCount:%d\n", getInt(EParameter.bottomLayerCount))
                + String.format(";bottomLayerCount:%d\n",  getInt(EParameter.bottomLayerCount))
                + String.format(";totalLayer:%d\n",  getInt(EParameter.layerCount))
                + String.format(";estimatedPrintTime:%f\n",  getFloat(EParameter.printTimeS));
        output.write(outputString.getBytes());

        // Now the fun bit - we actually have to write the GCODE
        output.write(String.format(START_GCODE_FORMAT, getString(EParameter.startGCode)).getBytes());

        for (int i = 0; i < getInt(EParameter.layerCount); i++) {
            output.write(generateLayerGcode(i));
        }

        output.write(String.format(END_GCODE_FORMAT, getString(EParameter.endGCode)).getBytes());

    }

    @Override
    public boolean hasAA() {
        // TODO:: implement loading AA
        return false;
    }

    @Override
    public int getAALevels() {
        return 1;
    }

    @Override
    public void setAALevels(int levels, List<PhotonFileLayer> layers) {
        //noop
    }


    @Override
    public void unLink() {
        // no-op
    }

    @Override
    public boolean isMirrored() {
        return ((PhotonProjectType)get(EParameter.projectType)) == PhotonProjectType.lcdMirror;
    }
}
