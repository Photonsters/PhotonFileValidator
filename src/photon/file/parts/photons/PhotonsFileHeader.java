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

package photon.file.parts.photons;

import photon.file.parts.IFileHeader;
import photon.file.parts.PhotonFileLayer;
import photon.file.parts.PhotonInputStream;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.List;

public class PhotonsFileHeader  implements IFileHeader {

    private int fileVersion;

    private int fileMark0;
    private int fileMark1;
    private int fileMark2;
    private int fileMark3;
    private int fileMark4;


    private int areaNum;

    private int headerAddress;
    private int backup0;
    private int previewAddress;
    private int backup1;
    private int layersDefAddress;
    private int backup2;
    private int layersImageAddress;


    public PhotonsFileHeader(byte[] file) throws Exception {
        DataInputStream ds = new DataInputStream(new ByteArrayInputStream(file));

        fileVersion = ds.readInt();

        fileMark0 = ds.readInt();
        fileMark1 = ds.readInt();
        fileMark2 = ds.readInt();
        fileMark3 = ds.readInt();
        fileMark4 = ds.readInt();


        areaNum = ds.readInt();

        headerAddress = ds.readInt();
        backup0 = ds.readInt();
        previewAddress = ds.readInt();
        backup1 = ds.readInt();
        layersDefAddress = ds.readInt();
        backup2 = ds.readInt();
        layersImageAddress = ds.readInt();

    }



        @Override
    public String getInformation() {
        return null;
    }

    @Override
    public int getNumberOfLayers() {
        return 0;
    }

    @Override
    public int getResolutionY() {
        return 0;
    }

    @Override
    public int getResolutionX() {
        return 0;
    }

    @Override
    public float getBuildAreaX() {
        return 0;
    }

    @Override
    public float getBuildAreaY() {
        return 0;
    }

    @Override
    public int getBottomLayers() {
        return 0;
    }

    @Override
    public void setBottomLayers(int bottomLayers) {

    }

    @Override
    public float getLayerHeight() {
        return 0;
    }

    @Override
    public float getExposureTimeSeconds() {
        return 0;
    }

    @Override
    public float getBottomExposureTimeSeconds() {
        return 0;
    }

    @Override
    public void setExposureBottomTimeSeconds(float exposureBottomTimeSeconds) {

    }

    @Override
    public void setExposureTimeSeconds(float exposureTimeSeconds) {

    }

    @Override
    public float getNormalExposure() {
        return 0;
    }

    @Override
    public float getOffTimeSeconds() {
        return 0;
    }

    @Override
    public void setOffTimeSeconds(float offTimeSeconds) {

    }

    @Override
    public int getPrintTimeSeconds() {
        return 0;
    }

    @Override
    public boolean isMirrored() { return false; }

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

    @Override
    public int getVersion() {
        return 0;
    }

    @Override
    public void setFileVersion(int i) {

    }

    @Override
    public int getByteSize() {
        return 0;
    }

    @Override
    public void unLink() {

    }
}
