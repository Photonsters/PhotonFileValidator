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

import photon.file.SlicedFileHeader;
import photon.file.parts.*;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;

/**
 *  by bn on 30/06/2018.
 */
public class PhotonFileHeader extends SlicedFileHeader {
    static final int MAGIC_NUMBER = 318570521;
    static final int PARAMETERS_SIZE = 60;
    static final int MACHINE_INFO_SIZE = 76;
    static final int BED_Z_DIMENSIONS = 150;

    public static final float DEFAULT_BOTTOM_LIFT_DISTANCE = 5.0f;
    public static final float DEFAULT_BOTTOM_LIFT_SPEED = 300.0f;
    public static final float DEFAULT_LIFT_DISTANCE = 5.0f;
    public static final float DEFAULT_LIFT_SPEED = 300.0f;
    public static final float DEFAULT_RETRACT_SPEED = 300.0f;


    private int header1;
    private float bedZmm;
    private int unknown1;
    private int unknown2;
    private int unknown3;

    private int previewOneOffsetAddress;
    private int layersDefinitionOffsetAddress;

    private int previewTwoOffsetAddress;

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

    public PhotonFileHeader(SlicedFileHeader other) {
        super(other);
        // Note we don't bother setting the addresses as they will be calculated on save.
        header1 = MAGIC_NUMBER;
        version = 2;
        bedZmm = BED_Z_DIMENSIONS;
        projectType = PhotonProjectType.lcdMirror;

        printParametersSize = PARAMETERS_SIZE;
        antiAliasingLevel = other.getAALevels();

        lightPWM = 255;
        bottomLightPWM = 255;
        machineInfoSize = MACHINE_INFO_SIZE;

        photonFilePrintParameters = new PhotonFilePrintParameters();
        additionalParameters.put("bottomLayerCount", String.valueOf(getBottomLayers()));
        photonFileMachineInfo = new PhotonFileMachineInfo("Photon", MACHINE_INFO_SIZE);

    }

    public PhotonFileHeader(byte[] file) throws Exception {
        PhotonInputStream ds = new PhotonInputStream(new ByteArrayInputStream(file));

        header1 = ds.readInt();
        version = ds.readInt();

        buildAreaX = ds.readFloat();
        buildAreaY = ds.readFloat();
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

        os.writeFloat(buildAreaX);
        os.writeFloat(buildAreaY);
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


    public int getLayersDefinitionOffsetAddress() {
        return layersDefinitionOffsetAddress;
    }

    public void unLink() {
    }

    public SlicedFileHeader fromIFileHeader(SlicedFileHeader other) {
        return new PhotonFileHeader(other);
    }

    public int getPrintParametersOffsetAddress() {
        return printParametersOffsetAddress;
    }

    public int getMachineInfoOffsetAddress() {
    	return machineInfoOffsetAddress;
    }
    
    public int getMachineInfoSize() {
    	return machineInfoSize;
    }

    //TODO:: this should probably be removed in favour of getAALevels
    public int getAntiAliasingLevel() {
        return antiAliasingLevel;
    }

    public void setAntiAliasingLevel(int antiAliasingLevel) {
        this.antiAliasingLevel = antiAliasingLevel;
    }

    @Override
    public void setFileVersion(int i) {
        version = i;
        antiAliasingLevel = 1;
        lightPWM = 255;
        bottomLightPWM = 255;
        additionalParameters.put("bottomLayerCount", String.valueOf(getBottomLayers()));
        photonFilePrintParameters = new PhotonFilePrintParameters();
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


    @Override
    public boolean isMirrored() {
        return projectType == PhotonProjectType.lcdMirror;
    }

    public void readParameters(byte[] file) throws Exception {
        photonFilePrintParameters = new PhotonFilePrintParameters(getPrintParametersOffsetAddress(), file, additionalParameters);
        photonFileMachineInfo = new PhotonFileMachineInfo(getMachineInfoOffsetAddress(), getMachineInfoSize(), file);
    }

    /**
     * Get the entire map of additional parameters.
     * This is mostly a transitional function for things which created machine / print parameter objects.
     * @return a map of parameters to values
     */
    public Map<String, String> getAdditionalParameters() { return additionalParameters; }
}
