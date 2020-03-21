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
    public static final float DEFAULT_DISTANCE = 5.0f;
    public static final float DEFAULT_SPEED = 300.0f;
    public static final float DEFAULT_LIGHT_OFF_TIME = 0.0f;

    /**
     * Check if the header has all the fields required for a print parameters block
     * @param header to check
     * @return true iff the header has all the required fields.
     */
    static public boolean hasPrintParameters(SlicedFileHeader header) {
        return header.has(EParameter.bottomLiftDistance)
                && header.has(EParameter.bottomLiftSpeed)
                && header.has(EParameter.liftDistance)
                && header.has(EParameter.liftSpeed)
                && header.has(EParameter.retractSpeed)
                && header.has(EParameter.volume)
                && header.has(EParameter.weight)
                && header.has(EParameter.cost)
                && header.has(EParameter.bottomLightOffTimeS)
                && header.has(EParameter.lightOffTimeS)
                && header.has(EParameter.bottomLayerCount);
    }

    static public void initializePrintParameters(int parametersPos, byte[] file, SlicedFileHeader header) throws IOException {
        byte[] data = Arrays.copyOfRange(file, parametersPos, parametersPos + getByteSize());
        PhotonInputStream ds = new PhotonInputStream(new ByteArrayInputStream(data));
        header.put(EParameter.bottomLiftDistance, ds.readFloat());
        header.put(EParameter.bottomLiftSpeed, ds.readFloat());
        header.put(EParameter.liftDistance, ds.readFloat());
        header.put(EParameter.liftSpeed, ds.readFloat());
        header.put(EParameter.retractSpeed, ds.readFloat());
        header.put(EParameter.volume, ds.readFloat());
        header.put(EParameter.weight, ds.readFloat());
        header.put(EParameter.cost, ds.readFloat());
        header.put(EParameter.bottomLightOffTimeS, ds.readFloat());
        header.put(EParameter.lightOffTimeS, ds.readFloat());
    }

    static public void initializePrintParameters(SlicedFileHeader header ) {
        header.put(EParameter.bottomLiftDistance, DEFAULT_DISTANCE);
        header.put(EParameter.bottomLiftSpeed, DEFAULT_SPEED);
        header.put(EParameter.liftDistance, DEFAULT_DISTANCE);
        header.put(EParameter.liftSpeed, DEFAULT_SPEED);
        header.put(EParameter.retractSpeed, DEFAULT_SPEED);
        header.put(EParameter.volume, 0.0f);
        header.put(EParameter.weight, 0.0f);
        header.put(EParameter.cost, 0.0f);
        header.put(EParameter.bottomLightOffTimeS, DEFAULT_LIGHT_OFF_TIME);
        header.put(EParameter.lightOffTimeS, DEFAULT_LIGHT_OFF_TIME);
    }



    static public void save(PhotonOutputStream os, SlicedFileHeader header) throws Exception {
        // TODO:: VALIDATE? Really could do with logging.
        os.writeFloat(header.getFloat(EParameter.bottomLiftDistance));
        os.writeFloat(header.getFloat(EParameter.bottomLiftSpeed));

        os.writeFloat(header.getFloat(EParameter.liftDistance));
        os.writeFloat(header.getFloat(EParameter.liftSpeed));
        os.writeFloat(header.getFloat(EParameter.retractSpeed));

        os.writeFloat(header.getFloat(EParameter.volume));
        os.writeFloat(header.getFloat(EParameter.weight));
        os.writeFloat(header.getFloat(EParameter.cost));

        os.writeFloat(header.getFloat(EParameter.bottomLightOffTimeS));
        os.writeFloat(header.getFloat(EParameter.lightOffTimeS));
        os.writeInt(header.getInt(EParameter.bottomLayerCount));

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
