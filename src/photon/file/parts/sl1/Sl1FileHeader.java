package photon.file.parts.sl1;

import photon.file.SlicedFileHeader;
import photon.file.parts.PhotonFileLayer;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class Sl1FileHeader extends SlicedFileHeader {

    private static Pattern linePattern = Pattern.compile("\\s*=\\s*");
    //A list of valid config.ini keys which are _not_ covered by 'core' values
    private static final List<String> KNOWN_ADDITIONAL_KEYS = Arrays.asList(
            "action",
            "fileCreationTimestamp",
            "materialName",
            "printerProfile",
            "printProfile",
            "printerModel",
            "printerVariant",
            "prusaSlicerVersion",
            "usedMaterial"
    );

    // This is used as the base of the image filenames.
    private String jobName;
    // A count of slow layers. Whatever that is (_not_ bottom layers). I've only ever seen it be 0.
    private int numberOfSlowLayers;


    public String getJobName() {
        return jobName;
    }


    public Sl1FileHeader(SlicedFileHeader other) {
        super(other);
        jobName = "SL1";
        for(String key : KNOWN_ADDITIONAL_KEYS) {
            if( other.containsKey(key) ) {
                put(key, other.get(key));
            }
        }
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
                    jobName = components[1];
                    continue;
                case "layerHeight":
                    layerHeightMilimeter = Float.parseFloat(components[1]);
                    continue;
                case "expTime":
                    exposureTimeSeconds = Float.parseFloat(components[1]);
                    continue;
                case "expTimeFirst":
                    exposureBottomTimeSeconds = Float.parseFloat(components[1]);
                    continue;
                case "numFade":
                    bottomLayers = Integer.parseInt(components[1]);
                    continue;
                case "numFast":
                    numberOfLayers = Integer.parseInt(components[1]);
                    continue;
                case "numSlow":
                    numberOfSlowLayers = Integer.parseInt(components[1]);
                    continue;
                case "printTime":
                    printTimeSeconds = (int) Float.parseFloat(components[1]);
                    continue;
                default:
                    // no-op - handled below.
                    break;
            }
            if( KNOWN_ADDITIONAL_KEYS.contains(components[0]) ) {
                put(components[0], components[1]);
            } else {
                throw new IllegalArgumentException("Unknown key in config.ini: " + line);
            }
        }

        version = 1;
    }

    public void write(OutputStream output) throws IOException {
        String outputString = String.format("jobDir = %s\n", jobName)
                + String.format("expTime = %.2f\n", exposureTimeSeconds)
                + String.format("expTimeFirst = %.2f\n", exposureBottomTimeSeconds)
                + String.format("layerHeight = %.3f\n", layerHeightMilimeter)
                + String.format("numFade = %d\n", bottomLayers)
                + String.format("numFast = %d\n", numberOfLayers)
                + String.format("numSlow = %d\n", numberOfSlowLayers)
                + String.format("printTime = %d\n", printTimeSeconds);
        output.write(outputString.getBytes());
        for (Map.Entry<String, String> entry : entrySet()) {
            if( KNOWN_ADDITIONAL_KEYS.contains(entry.getKey()) ) {
                output.write(String.format("%s = %s\n", entry.getKey(), entry.getValue()).getBytes());
            }
        }
        ;
    }

    @Override
    public void unLink() {
        /* no-op */
    }

    void setResolutionX(int x) {
        resolutionX = x;
    }

    void setResolutionY(int y) {
        resolutionY = y;
    }
}
