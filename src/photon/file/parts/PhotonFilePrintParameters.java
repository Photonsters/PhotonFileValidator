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

import java.io.ByteArrayInputStream;
import java.util.Arrays;

public class PhotonFilePrintParameters {
    public float bottomLiftDistance = 5.0f;
    public float bottomLiftSpeed = 300.0f;

    public float liftingDistance = 5.0f;
    public float liftingSpeed = 300.0f;
    public float retractSpeed = 300.0f;

    public float volumeMl = 0;
    public float weightG =  0;
    public float costDollars = 0;

    public float bottomLightOffDelay = 0.0f;
    public float lightOffDelay = 0.0f;
    public int bottomLayerCount;

    public int p1;
    public int p2;
    public int p3;
    public int p4;


    public PhotonFilePrintParameters(int bottomLayerCount) {
        this.bottomLayerCount = bottomLayerCount;
    }

    public PhotonFilePrintParameters(int parametersPos, byte[] file) throws Exception {
        byte[] data = Arrays.copyOfRange(file, parametersPos, parametersPos + getByteSize());
        PhotonInputStream ds = new PhotonInputStream(new ByteArrayInputStream(data));

        bottomLiftDistance = ds.readFloat();
        bottomLiftSpeed = ds.readFloat();

        liftingDistance = ds.readFloat();
        liftingSpeed = ds.readFloat();
        retractSpeed = ds.readFloat();

        volumeMl = ds.readFloat();
        weightG = ds.readFloat();
        costDollars = ds.readFloat();

        bottomLightOffDelay = ds.readFloat();
        lightOffDelay = ds.readFloat();
        bottomLayerCount = ds.readInt();

        p1 = ds.readInt();
        p2 = ds.readInt();
        p3 = ds.readInt();
        p4 = ds.readInt();
    }

    public void save(PhotonOutputStream os) throws Exception {
        os.writeFloat(bottomLiftDistance);
        os.writeFloat(bottomLiftSpeed);

        os.writeFloat(liftingDistance);
        os.writeFloat(liftingSpeed);
        os.writeFloat(retractSpeed);

        os.writeFloat(volumeMl);
        os.writeFloat(weightG);
        os.writeFloat(costDollars);

        os.writeFloat(bottomLightOffDelay);
        os.writeFloat(lightOffDelay);
        os.writeInt(bottomLayerCount);

        os.writeInt(p1);
        os.writeInt(p2);
        os.writeInt(p3);
        os.writeInt(p4);
    }

    public int getByteSize() {
        return 4+4 +4+4+4 +4+4+4 +4+4+4 +4+4+4+4;
    }
}
