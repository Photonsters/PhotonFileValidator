package photon.file.parts.sl1;

import photon.file.SlicedFileHeader;
import photon.file.parts.PhotonFileLayer;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class Sl1FileHeader extends SlicedFileHeader {

    private static Pattern linePattern = Pattern.compile("\\s*=\\s*");

    // This is used as the base of the image filenames.
    private String jobName;


    public String getJobName() {
        return jobName;
    }


    public Sl1FileHeader(SlicedFileHeader other) {
        super(other);
        jobName = "SL1";
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
                    additionalParameters.put(components[0], components[1]);
                    break;
                default:
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
                + String.format("NumFast = %d\n", numberOfLayers)
                + String.format("PrintTime = %d\n", printTimeSeconds);
        output.write(outputString.getBytes());
        for(Map.Entry<String, String> entry: additionalParameters.entrySet()) {
            output.write(String.format("%s = %s\n",entry.getKey(),entry.getValue()).getBytes());
        };
    }

    @Override
    public void unLink() {
        /* no-op */
    }

    public SlicedFileHeader fromIFileHeader(SlicedFileHeader other) {
        return new Sl1FileHeader(other);
    }

    void setResolutionX(int x) {
        resolutionX = x;
    }

    void setResolutionY(int y) {
        resolutionY = y;
    }

}
