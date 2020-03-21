package photon.file.parts.sl1;

import photon.file.SlicedFileHeader;
import photon.file.parts.EParameter;
import photon.file.parts.PhotonFileLayer;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class Sl1FileHeader extends SlicedFileHeader {
    private static String DEFAULT_JOB_NAME = "sl1";
    private static String DEFAULT_ACTION = "print";
    private static String DEFAULT_TIMESTAMP = "2020-01-01 at 00:00:01 UTC";
    private static String DEFAULT_MATERIAL = "resin";
    private static int DEFAULT_SLOW = 0;
    private static String DEFAULT_PRINT_PROFILE = "normal";
    private static String DEFAULT_MODEL = "sl1";
    private static String DEFAULT_PRINTER_PROFILE = "photon";
    private static String DEFAULT_PRINTER_VARIANT = "default";
    private static String DEFAULT_SLICER_VERSION = "PrusaSlicer";


    private static Pattern linePattern = Pattern.compile("\\s*=\\s*");

    public String getJobName() {
        return getString(EParameter.jobName);
    }


    public Sl1FileHeader(SlicedFileHeader other) {
        super(other);
        putIfMissing(EParameter.jobName, DEFAULT_JOB_NAME);
        putIfMissing(EParameter.action, DEFAULT_ACTION);
        putIfMissing(EParameter.fileCreationTimestamp, DEFAULT_TIMESTAMP);
        putIfMissing(EParameter.slowLayerCount, DEFAULT_SLOW);
        putIfMissing(EParameter.materialName, DEFAULT_MATERIAL);
        putIfMissing(EParameter.printerProfile, DEFAULT_PRINTER_PROFILE);
        putIfMissing(EParameter.printProfile, DEFAULT_PRINT_PROFILE);
        putIfMissing(EParameter.machineName, DEFAULT_MODEL);
        putIfMissing(EParameter.prusaSlicerVersion, DEFAULT_SLICER_VERSION);
        putIfMissing(EParameter.printerVariant, DEFAULT_PRINTER_VARIANT);
        putIfMissing(EParameter.volume, 0.0f);
        forceParameterToFloat(EParameter.printTimeS);
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
    public void setAALevels(int levels, List<PhotonFileLayer> layers) {}

    public Sl1FileHeader(InputStream entry) throws IOException {
        BufferedReader headerStream = new BufferedReader(new InputStreamReader(entry));
        while (headerStream.ready()) {
            String line = headerStream.readLine();
            String[] components = linePattern.split(line);
            if (components.length != 2) {
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
            switch (components[0]) {
                case "jobDir":
                    put(EParameter.jobName, components[1]);
                    continue;
                case "layerHeight":
                    put(EParameter.layerHeightMM, Float.parseFloat(components[1]));
                    continue;
                case "expTime":
                    put(EParameter.exposureTimeS, Float.parseFloat(components[1]));
                    continue;
                case "expTimeFirst":
                    put(EParameter.bottomExposureTimeS, Float.parseFloat(components[1]));
                    continue;
                case "numFade":
                    put(EParameter.bottomLayerCount, Integer.parseInt(components[1]));
                    continue;
                case "numFast":
                    put(EParameter.layerCount, Integer.parseInt(components[1]));
                    continue;
                case "numSlow":
                    put(EParameter.slowLayerCount, Integer.parseInt(components[1]));
                    continue;
                case "printTime":
                    put(EParameter.printTimeS, Float.parseFloat(components[1]));
                    continue;
                case "action":
                    put(EParameter.action, components[1]);
                    continue;
                case "fileCreationTimestamp":
                    put(EParameter.fileCreationTimestamp, components[1]);
                    continue;
                case "materialName":
                    put(EParameter.materialName, components[1]);
                    continue;
                case "printerProfile":
                    put(EParameter.printerProfile, components[1]);
                    continue;
                case "printProfile":
                    put(EParameter.printProfile, components[1]);
                    continue;
                case "printerModel":
                    put(EParameter.machineName, components[1]);
                    continue;
                case "printerVariant":
                    put(EParameter.printerVariant, components[1]);
                    continue;
                case "prusaSlicerVersion":
                    put(EParameter.prusaSlicerVersion, components[1]);
                    continue;
                case "usedMaterial":
                    put(EParameter.volume, components[1]);
                    continue;
                default:
                    throw new IllegalArgumentException("Unknown key in config.ini: " + line);
            }
        }
    }

    public void write(OutputStream output) throws IOException {
        String outputString = String.format("action = %s\n", getString(EParameter.action))
                + String.format("jobDir = %s\n", getString(EParameter.jobName))
                + String.format("expTime = %.2f\n", getFloat(EParameter.exposureTimeS))
                + String.format("expTimeFirst = %.2f\n", getFloat(EParameter.bottomExposureTimeS))
                + String.format("fileCreationTimestamp = %s\n", getString(EParameter.fileCreationTimestamp))
                + String.format("layerHeight = %.3f\n", getFloat(EParameter.layerHeightMM))
                + String.format("materialName = %s\n", getString(EParameter.materialName))
                + String.format("numFade = %d\n", getInt(EParameter.bottomLayerCount))
                + String.format("numFast = %d\n", getInt(EParameter.layerCount))
                + String.format("numSlow = %d\n", getInt(EParameter.slowLayerCount))
                + String.format("printProfile = %s\n", getString(EParameter.printProfile))
                + String.format("printTime = %f\n", getFloat(EParameter.printTimeS))
                + String.format("printerModel = %s\n", getString(EParameter.machineName))
                + String.format("printerProfile = %s\n", getString(EParameter.printerProfile))
                + String.format("printerVariant = %s\n", getString(EParameter.printerVariant))
                + String.format("prusaSlicerVersion = %s\n", getString(EParameter.prusaSlicerVersion))
                + String.format("usedMaterial = %f\n", getFloat(EParameter.volume));
        output.write(outputString.getBytes());
        //TODO:: WRITE THE REST
    }

    @Override
    public void unLink() {
        /* no-op */
    }

    @Override
    public boolean isMirrored() {
        return false;
    }
}
