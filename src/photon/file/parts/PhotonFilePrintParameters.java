/*
 * MIT License
 *
 * Copyright (c) 2018 Bonosoft
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package photon.file.parts;

import photon.file.SlicedFileHeader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;

public class PhotonFilePrintParameters {
    public final static String BOTTOM_LIFT_DISTANCE_KEY = "bottomLiftDistance";
    public final static String BOTTOM_LIFT_SPEED_KEY = "bottomLiftSpeed";
    public final static String LIFT_DISTANCE_KEY = "LiftDistance";
    public final static String LIFT_SPEED_KEY = "LiftSpeed";
    public final static String RETRACT_SPEED_KEY = "retractSpeed";
    public final static String VOLUME_KEY = "volume";
    public final static String WEIGHT_KEY = "weight";
    public final static String COST_KEY = "cost";
    public final static String BOTTOM_LIGHT_OFF_DELAY_KEY = "bottomLightOffDelay";
    public final static String LIGHT_OFF_DELAY_KEY = "lightOffDelay";
    public final static String BOTTOM_LAYER_COUNT_KEY = "bottomLayerCount";


    public static final float DEFAULT_DISTANCE = 5.0f;
    public static final float DEFAULT_SPEED = 300.0f;
    public static final float DEFAULT_LIGHT_OFF_DELAY = 0.0f;

    /**
     * Check if the header has all the fields required for a print parameters block
     * @param header to check
     * @return true iff the header has all the required fields.
     */
    static public boolean hasPrintParameters(SlicedFileHeader header) {
        return header.containsKey(BOTTOM_LIFT_DISTANCE_KEY)
                && header.containsKey(BOTTOM_LIFT_SPEED_KEY)
                && header.containsKey(LIFT_DISTANCE_KEY)
                && header.containsKey(LIFT_SPEED_KEY)
                && header.containsKey(RETRACT_SPEED_KEY)
                && header.containsKey(VOLUME_KEY)
                && header.containsKey(WEIGHT_KEY)
                && header.containsKey(COST_KEY)
                && header.containsKey(BOTTOM_LIGHT_OFF_DELAY_KEY)
                && header.containsKey(LIGHT_OFF_DELAY_KEY)
                && header.containsKey(BOTTOM_LAYER_COUNT_KEY);
    }

    static public void initializePrintParameters(int parametersPos, byte[] file, SlicedFileHeader header) throws IOException {
        byte[] data = Arrays.copyOfRange(file, parametersPos, parametersPos + getByteSize());
        PhotonInputStream ds = new PhotonInputStream(new ByteArrayInputStream(data));
        
        header.put(BOTTOM_LIFT_DISTANCE_KEY, String.valueOf(ds.readFloat()));
        header.put(BOTTOM_LIFT_SPEED_KEY, String.valueOf(ds.readFloat()));
        header.put(LIFT_DISTANCE_KEY, String.valueOf(ds.readFloat()));
        header.put(LIFT_SPEED_KEY, String.valueOf(ds.readFloat()));
        header.put(RETRACT_SPEED_KEY, String.valueOf(ds.readFloat()));
        header.put(VOLUME_KEY, String.valueOf(ds.readFloat()));
        header.put(WEIGHT_KEY, String.valueOf(ds.readFloat()));
        header.put(COST_KEY, String.valueOf(ds.readFloat()));
        header.put(BOTTOM_LIGHT_OFF_DELAY_KEY, String.valueOf(ds.readFloat()));
        header.put(LIGHT_OFF_DELAY_KEY, String.valueOf(ds.readFloat()));
    }

    static public void initializePrintParameters(SlicedFileHeader header ) {
        header.put(BOTTOM_LIFT_DISTANCE_KEY, String.valueOf(DEFAULT_DISTANCE));
        header.put(BOTTOM_LIFT_SPEED_KEY, String.valueOf(DEFAULT_SPEED));
        header.put(LIFT_DISTANCE_KEY, String.valueOf(DEFAULT_DISTANCE));
        header.put(LIFT_SPEED_KEY, String.valueOf(DEFAULT_SPEED));
        header.put(RETRACT_SPEED_KEY, String.valueOf(DEFAULT_SPEED));
        header.put(VOLUME_KEY, "0.0");
        header.put(WEIGHT_KEY, "0.0");
        header.put(COST_KEY, "0.0");
        header.put(BOTTOM_LIGHT_OFF_DELAY_KEY, String.valueOf(DEFAULT_LIGHT_OFF_DELAY));
        header.put(LIGHT_OFF_DELAY_KEY, String.valueOf(DEFAULT_LIGHT_OFF_DELAY));
    }



    static public void save(PhotonOutputStream os, SlicedFileHeader header) throws Exception {
        // TODO:: VALIDATE? Really could do with logging.
        os.writeFloat(header.getFloatOrDefault(BOTTOM_LIFT_DISTANCE_KEY, PhotonFilePrintParameters.DEFAULT_DISTANCE));
        os.writeFloat(header.getFloatOrDefault(BOTTOM_LIFT_SPEED_KEY, PhotonFilePrintParameters.DEFAULT_SPEED ));

        os.writeFloat(header.getFloatOrDefault(LIFT_DISTANCE_KEY, PhotonFilePrintParameters.DEFAULT_DISTANCE));
        os.writeFloat(header.getFloatOrDefault(LIFT_SPEED_KEY, PhotonFilePrintParameters.DEFAULT_SPEED));
        os.writeFloat(header.getFloatOrDefault(RETRACT_SPEED_KEY, PhotonFilePrintParameters.DEFAULT_SPEED));

        os.writeFloat(header.getFloatOrDefault(VOLUME_KEY, 0f));
        os.writeFloat(header.getFloatOrDefault(WEIGHT_KEY, 0f));
        os.writeFloat(header.getFloatOrDefault(COST_KEY, 0f));

        os.writeFloat(header.getFloatOrDefault(BOTTOM_LIGHT_OFF_DELAY_KEY, PhotonFilePrintParameters.DEFAULT_LIGHT_OFF_DELAY));
        os.writeFloat(header.getFloatOrDefault(LIGHT_OFF_DELAY_KEY, PhotonFilePrintParameters.DEFAULT_LIGHT_OFF_DELAY));
        os.writeInt(header.getBottomLayers());
        // and some padding.
        os.writeInt(0);
        os.writeInt(0);
        os.writeInt(0);
        os.writeInt(0);
    }

    static public int getByteSize() {
        return 4+4 +4+4+4 +4+4+4 +4+4+4 +4+4+4+4;
    }
}
