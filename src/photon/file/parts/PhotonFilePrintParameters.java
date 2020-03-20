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


    static public void initializePrintParameters(int parametersPos, byte[] file, SlicedFileHeader header) throws IOException {
        byte[] data = Arrays.copyOfRange(file, parametersPos, parametersPos + getByteSize());
        PhotonInputStream ds = new PhotonInputStream(new ByteArrayInputStream(data));
        
        header.setParam(BOTTOM_LIFT_DISTANCE_KEY, String.valueOf(ds.readFloat()));
        header.setParam(BOTTOM_LIFT_SPEED_KEY, String.valueOf(ds.readFloat()));
        header.setParam(LIFT_DISTANCE_KEY, String.valueOf(ds.readFloat()));
        header.setParam(LIFT_SPEED_KEY, String.valueOf(ds.readFloat()));
        header.setParam(RETRACT_SPEED_KEY, String.valueOf(ds.readFloat()));
        header.setParam(VOLUME_KEY, String.valueOf(ds.readFloat()));
        header.setParam(WEIGHT_KEY, String.valueOf(ds.readFloat()));
        header.setParam(COST_KEY, String.valueOf(ds.readFloat()));
        header.setParam(BOTTOM_LIGHT_OFF_DELAY_KEY, String.valueOf(ds.readFloat()));
        header.setParam(LIGHT_OFF_DELAY_KEY, String.valueOf(ds.readFloat()));
        header.setParam(BOTTOM_LAYER_COUNT_KEY, String.valueOf(ds.readInt()));
    }




    static public void save(PhotonOutputStream os, SlicedFileHeader header) throws Exception {
        os.writeFloat(header.getFloatParam(BOTTOM_LIFT_DISTANCE_KEY));
        os.writeFloat(header.getFloatParam(BOTTOM_LIFT_SPEED_KEY));
        os.writeFloat(header.getFloatParam(LIFT_DISTANCE_KEY));
        os.writeFloat(header.getFloatParam(LIFT_SPEED_KEY));
        os.writeFloat(header.getFloatParam(RETRACT_SPEED_KEY));
        os.writeFloat(header.getFloatParam(VOLUME_KEY));
        os.writeFloat(header.getFloatParam(WEIGHT_KEY));
        os.writeFloat(header.getFloatParam(COST_KEY));
        os.writeFloat(header.getFloatParam(BOTTOM_LIGHT_OFF_DELAY_KEY));
        os.writeFloat(header.getFloatParam(LIGHT_OFF_DELAY_KEY));
        os.writeFloat(header.getIntParam(BOTTOM_LAYER_COUNT_KEY));
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
