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
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

public class PhotonFilePrintParameters {
    
    public PhotonFilePrintParameters() {};

    public PhotonFilePrintParameters(int parametersPos, byte[] file, Map<String,String> paramMap) throws IOException {
        byte[] data = Arrays.copyOfRange(file, parametersPos, parametersPos + getByteSize());
        PhotonInputStream ds = new PhotonInputStream(new ByteArrayInputStream(data));

        paramMap.put("bottomLiftDistance", String.valueOf(ds.readFloat()));
        paramMap.put("bottomLiftSpeed", String.valueOf(ds.readFloat()));
        paramMap.put("LiftDistance", String.valueOf(ds.readFloat()));
        paramMap.put("liftSpeed", String.valueOf(ds.readFloat()));
        paramMap.put("retractSpeed", String.valueOf(ds.readFloat()));
        paramMap.put("volume", String.valueOf(ds.readFloat()));
        paramMap.put("weight", String.valueOf(ds.readFloat()));
        paramMap.put("cost", String.valueOf(ds.readFloat()));
        paramMap.put("bottomLightOffDelay", String.valueOf(ds.readFloat()));
        paramMap.put("bottomLayerCount", String.valueOf(ds.readInt()));
    }


    public void save(PhotonOutputStream os, Map<String, String> paramMap) throws Exception {
        os.writeFloat(Float.parseFloat(paramMap.get("bottomLiftDistance")));
        os.writeFloat(Float.parseFloat(paramMap.get("bottomLiftSpeed")));
        os.writeFloat(Float.parseFloat(paramMap.get("LiftDistance")));
        os.writeFloat(Float.parseFloat(paramMap.get("liftSpeed")));
        os.writeFloat(Float.parseFloat(paramMap.get("retractSpeed")));
        os.writeFloat(Float.parseFloat(paramMap.get("volume")));
        os.writeFloat(Float.parseFloat(paramMap.get("weight")));
        os.writeFloat(Float.parseFloat(paramMap.get("cost")));
        os.writeFloat(Float.parseFloat(paramMap.get("bottomLightOffDelay")));
        os.writeInt(Integer.parseInt(paramMap.get("bottomLayerCount")));
        os.writeInt(0);
        os.writeInt(0);
        os.writeInt(0);
        os.writeInt(0);
    }

    public int getByteSize() {
        return 4+4 +4+4+4 +4+4+4 +4+4+4 +4+4+4+4;
    }
}
