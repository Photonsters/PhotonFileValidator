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

/**
 *  by bn on 30/06/2018.
 */
public class PhotonFileHeader {
    private int header1;
    private int header2;
    private float bedXmm;
    private float bedYmm;
    private float bedZmm;
    private int unknown1;
    private int unknown2;
    private int unknown3;
    private float layerHeightMilimeter;
    private float exposureTimeSeconds;
    private float exposureBottomTimeSeconds;
    private float offTimeSeconds;
    private int bottomLayers;
    private int resolutionX;
    private int resolutionY;

    private int previewOneOffsetAddress;
    private int layersDefinitionOffsetAddress;
    private int numberOfLayers;

    private int previewTwoOffsetAddress;

    private int unknown4;
    private PhotonProjectType projectType;

    public PhotonFileHeader(byte[] file) throws Exception {
        PhotonInputStream ds = new PhotonInputStream(new ByteArrayInputStream(file));

        header1 = ds.readInt();
        header2 = ds.readInt();

        bedXmm = ds.readFloat();
        bedYmm = ds.readFloat();
        bedZmm = ds.readFloat();

        unknown1 = ds.readInt();
        unknown2 = ds.readInt();
        unknown3 = ds.readInt();

        layerHeightMilimeter = ds.readFloat();
        exposureTimeSeconds = ds.readFloat();
        exposureBottomTimeSeconds = ds.readFloat();

        offTimeSeconds = ds.readFloat();
        bottomLayers = ds.readInt();

        resolutionX = ds.readInt();
        resolutionY = ds.readInt();

        previewOneOffsetAddress = ds.readInt();
        layersDefinitionOffsetAddress = ds.readInt();

        numberOfLayers = ds.readInt();

        previewTwoOffsetAddress = ds.readInt();
        unknown4 = ds.readInt();

        projectType = PhotonProjectType.find(ds.readInt());

        // padding 6 ints (4bytes)
    }

    public void save(PhotonOutputStream os, int previewOnePos, int previewTwoPos, int layerDefinitionPos) throws Exception {
        previewOneOffsetAddress = previewOnePos;
        previewTwoOffsetAddress = previewTwoPos;
        layersDefinitionOffsetAddress = layerDefinitionPos;

        os.writeInt(header1);
        os.writeInt(header2);

        os.writeFloat(bedXmm);
        os.writeFloat(bedYmm);
        os.writeFloat(bedZmm);

        os.writeInt(unknown1);
        os.writeInt(unknown2);
        os.writeInt(unknown3);

        os.writeFloat(layerHeightMilimeter);
        os.writeFloat(exposureTimeSeconds);
        os.writeFloat(exposureBottomTimeSeconds);

        os.writeFloat(offTimeSeconds);
        os.writeInt(bottomLayers);

        os.writeInt(resolutionX);
        os.writeInt(resolutionY);

        os.writeInt(previewOneOffsetAddress);
        os.writeInt(layersDefinitionOffsetAddress);

        os.writeInt(numberOfLayers);

        os.writeInt(previewTwoOffsetAddress);
        os.writeInt(unknown4);

        os.writeInt(projectType.projectID);

        os.writeInt(0);
        os.writeInt(0);
        os.writeInt(0);
        os.writeInt(0);
        os.writeInt(0);
        os.writeInt(0);
    }

    public int getByteSize() {
        return 4+4 + 4+4+4 + 4+4+4 + 4+4+4 + 4+4 + 4+4 + 4+4 + 4 + 4+4 + 4 + 4+4+4+4+4+4;
    }

    public int getPreviewOneOffsetAddress() {
        return previewOneOffsetAddress;
    }

    public int getPreviewTwoOffsetAddress() {
        return previewTwoOffsetAddress;
    }

    public int getNumberOfLayers() {
        return numberOfLayers;
    }

    public int getLayersDefinitionOffsetAddress() {
        return layersDefinitionOffsetAddress;
    }

    public float getNormalExposure() {
        return exposureTimeSeconds;
    }

    public float getOffTime() {
        return offTimeSeconds;
    }

    public int getResolutionX() {
        return resolutionX;
    }

    public int getResolutionY() {
        return resolutionY;
    }

    public float getBuildAreaX() {
        return bedXmm;
    }

    public float getBuildAreaY() {
        return bedYmm;
    }

    public float getLayerHeight() {
        return layerHeightMilimeter;
    }

    public int getBottomLayers() {
        return bottomLayers;
    }

    public float getBottomExposureTimeSeconds() {
        return exposureBottomTimeSeconds;
    }

    public float getOffTimeSeconds() {
        return offTimeSeconds;
    }

    public float getExposureTimeSeconds() {
        return exposureTimeSeconds;
    }

    public void unLink() {
    }

    public void setExposureTimeSeconds(float exposureTimeSeconds) {
        this.exposureTimeSeconds = exposureTimeSeconds;
    }

    public void setExposureBottomTimeSeconds(float exposureBottomTimeSeconds) {
        this.exposureBottomTimeSeconds = exposureBottomTimeSeconds;
    }

    public void setOffTimeSeconds(float offTimeSeconds) {
        this.offTimeSeconds = offTimeSeconds;
    }

    public void setBottomLayers(int bottomLayers) {
        this.bottomLayers = bottomLayers;
    }
}
