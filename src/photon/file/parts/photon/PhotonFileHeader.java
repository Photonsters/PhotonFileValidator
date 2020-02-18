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

package photon.file.parts.photon;

import photon.file.parts.*;
import photon.file.ui.Text;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.List;

/**
 *  by bn on 30/06/2018.
 */
public class PhotonFileHeader implements IFileHeader {
    private int header1;
    private int version;
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

    private int printTimeSeconds;
    private PhotonProjectType projectType;

    private int printParametersOffsetAddress;
    private int printParametersSize;
    private int antiAliasingLevel;

    private short lightPWM;
    private short bottomLightPWM;

    private int unknown4;
    private int machineInfoOffsetAddress;
    private int machineInfoSize;


    public PhotonFilePrintParameters photonFilePrintParameters;
    public PhotonFileMachineInfo photonFileMachineInfo;


    public PhotonFileHeader(byte[] file) throws Exception {
        PhotonInputStream ds = new PhotonInputStream(new ByteArrayInputStream(file));

        header1 = ds.readInt();
        version = ds.readInt();

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
        printTimeSeconds = ds.readInt();

        projectType = PhotonProjectType.find(ds.readInt());

        printParametersOffsetAddress = ds.readInt();
        printParametersSize = ds.readInt();
        antiAliasingLevel = ds.readInt();

        lightPWM = ds.readShort();
        bottomLightPWM = ds.readShort();

        unknown4 = ds.readInt();
        machineInfoOffsetAddress = ds.readInt();
        if (version>1) {
            machineInfoSize = ds.readInt();
        }
    }

    public int getByteSize() {
        return 4+4 + 4+4+4 + 4+4+4 + 4+4+4 + 4+4 + 4+4 + 4+4 + 4 + 4+4 + 4 + 4+4+4 +2+2 +4+4+ (version>1?4:0);
    }

    public void save(PhotonOutputStream os, int previewOnePos, int previewTwoPos, int layerDefinitionPos, int parametersPos, int machineInfoPos) throws Exception {
        previewOneOffsetAddress = previewOnePos;
        previewTwoOffsetAddress = previewTwoPos;
        layersDefinitionOffsetAddress = layerDefinitionPos;
        printParametersOffsetAddress = parametersPos;
        machineInfoOffsetAddress = machineInfoPos;

        os.writeInt(header1);
        os.writeInt(version);

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
        os.writeInt(printTimeSeconds);

        os.writeInt(projectType.getProjectID());

        os.writeInt(printParametersOffsetAddress);
        os.writeInt(printParametersSize);
        os.writeInt(antiAliasingLevel);

        os.writeShort(lightPWM);
        os.writeShort(bottomLightPWM);

        os.writeInt(unknown4);
        os.writeInt(machineInfoOffsetAddress);
        if (version>1) {
            os.writeInt(machineInfoSize);
        }
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

    public int getVersion() {
        return version;
    }

    public int getPrintParametersOffsetAddress() {
        return printParametersOffsetAddress;
    }

    public int getPrintParametersSize() {
        return printParametersSize;
    }

    public int getMachineInfoOffsetAddress() {
    	return machineInfoOffsetAddress;
    }
    
    public int getMachineInfoSize() {
    	return machineInfoSize;
    }
    
    public int getAntiAliasingLevel() {
        return antiAliasingLevel;
    }

    public void setAntiAliasingLevel(int antiAliasingLevel) {
        this.antiAliasingLevel = antiAliasingLevel;
    }

    public void setFileVersion(int i) {
        version = i;
        antiAliasingLevel = 1;
        lightPWM = 255;
        bottomLightPWM = 255;

        photonFilePrintParameters = new PhotonFilePrintParameters(getBottomLayers());
    }

    public String getInformation() {
        return String.format("T: %.3f", layerHeightMilimeter) +
                ", E: " + Text.formatSeconds(exposureTimeSeconds) +
                ", O: " + Text.formatSeconds(offTimeSeconds) +
                ", BE: " + Text.formatSeconds(exposureBottomTimeSeconds) +
                String.format(", BL: %d", bottomLayers);
    }

    public boolean hasAA() {
        return (getVersion()>1 && getAntiAliasingLevel()>1);
    }

    public int getAALevels() {
        if (getVersion()>1) {
            return getAntiAliasingLevel();
        }
        return 1;
    }

    public void setAALevels(int levels, List<PhotonFileLayer> layers) {
        if (getVersion()>1) {
            if (levels < getAntiAliasingLevel()) {
                reduceAaLevels(levels, layers);
            }
            if (levels > getAntiAliasingLevel()) {
                increaseAaLevels(levels, layers);
            }
        }
    }

    private void increaseAaLevels(int levels, List<PhotonFileLayer> layers) {
        // insert base layer to the correct count, as we are to recalc the AA anyway
        for(PhotonFileLayer photonFileLayer : layers) {
            while (photonFileLayer.getAntiAlias().size()<(levels-1)) {
                photonFileLayer.getAntiAlias().add(new PhotonFileLayer(photonFileLayer, this));
            }
        }
        setAntiAliasingLevel(levels);
    }

    private void reduceAaLevels(int levels, List<PhotonFileLayer> layers) {
        // delete any layers to the correct count, as we are to recalc the AA anyway
        for(PhotonFileLayer photonFileLayer : layers) {
            while (photonFileLayer.getAntiAlias().size()>(levels-1)) {
                photonFileLayer.getAntiAlias().remove(0);
            }
        }
        setAntiAliasingLevel(levels);
    }


    public int getPrintTimeSeconds() {
        return printTimeSeconds;
    }

    public boolean isMirrored() {
        return projectType == PhotonProjectType.lcdMirror;
    }

    public void readParameters(byte[] file) throws Exception {
        photonFilePrintParameters = new PhotonFilePrintParameters(getPrintParametersOffsetAddress(), file);
        photonFileMachineInfo = new PhotonFileMachineInfo(getMachineInfoOffsetAddress(), getMachineInfoSize(), file);
    }
}
