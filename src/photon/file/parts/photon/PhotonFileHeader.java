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

/**
 *  by bn on 30/06/2018.
 */
public class PhotonFileHeader extends SlicedFileHeader {
    static final int MAGIC_NUMBER = 318570521;
    static final int PARAMETERS_SIZE = 60;
    static final int MACHINE_INFO_SIZE = 76;
    static final float BED_Z_DIMENSIONS = 150.0f;
    static final short DEFAULT_PWM = 255;

    public PhotonFileHeader(SlicedFileHeader other) {
        super(other);

        // Note we don't bother setting the addresses as they will be calculated on save.
        put(EParameter.magicHeader, MAGIC_NUMBER);
        // only create V2 files.
        put(EParameter.version, 2);

        putIfMissing(EParameter.bedZMM, BED_Z_DIMENSIONS);
        putIfMissing(EParameter.projectType, PhotonProjectType.lcdMirror);

        put(EParameter.parametersSize, PARAMETERS_SIZE);
        putIfMissing(EParameter.antialiasingLevel, other.getAALevels());

        putIfMissing(EParameter.lightPWM, DEFAULT_PWM);
        putIfMissing(EParameter.bottomLightPWM, DEFAULT_PWM);
        put(EParameter.machineInfoSize, MACHINE_INFO_SIZE);

        // check the print time!
        forceParameterToInt(EParameter.printTimeS);

        if( !PhotonFileMachineInfo.hasMachineInfo(this) ) {
            PhotonFileMachineInfo.initializeMachineInfo("Photon", MACHINE_INFO_SIZE, this);
        }
        if( !PhotonFilePrintParameters.hasPrintParameters(this) ){
            PhotonFilePrintParameters.initializePrintParameters( this);
        }
    }

    public PhotonFileHeader(byte[] file) throws Exception {
        PhotonInputStream ds = new PhotonInputStream(new ByteArrayInputStream(file));

        put(EParameter.magicHeader, ds.readInt());
        put(EParameter.version, ds.readInt());

        put(EParameter.bedXMM, ds.readFloat());
        put(EParameter.bedYMM, ds.readFloat());
        put(EParameter.bedZMM, ds.readFloat());

        // padding - TODO:: should we save?
        ds.readInt();
        ds.readInt();
        ds.readInt();

        put(EParameter.layerHeightMM, ds.readFloat());
        put(EParameter.exposureTimeS, ds.readFloat());
        put(EParameter.bottomExposureTimeS, ds.readFloat());

        put(EParameter.lightOffTimeS, ds.readFloat());
        put(EParameter.bottomLayerCount, ds.readInt());

        put(EParameter.resolutionX, ds.readInt());
        put(EParameter.resolutionY, ds.readInt());

        put(EParameter.previewOneAddress, ds.readInt());
        put(EParameter.layersAddress, ds.readInt());

        put(EParameter.layerCount, ds.readInt());

        put(EParameter.previewTwoAddress, ds.readInt());
        put(EParameter.printTimeS, ds.readInt());

        put(EParameter.projectType, PhotonProjectType.find(ds.readInt()));

        put(EParameter.parametersAddress, ds.readInt());
        put(EParameter.parametersSize, ds.readInt());
        put(EParameter.antialiasingLevel, ds.readInt());

        put(EParameter.lightPWM, ds.readShort());
        put(EParameter.bottomLightPWM, ds.readShort());

        ds.readInt();
        if( getVersion() > 1) {
            put(EParameter.machineInfoAddress, ds.readInt());
            put(EParameter.machineInfoSize, ds.readInt());
        }
    }

    public int getByteSize() {
        return 4+4 + 4+4+4 + 4+4+4 + 4+4+4 + 4+4 + 4+4 + 4+4 + 4 + 4+4 + 4 + 4+4+4 +2+2 +4+4+ (getVersion()>1?4:0);
    }

    public void save(PhotonOutputStream os, int previewOnePos, int previewTwoPos, int layerDefinitionPos, int parametersPos, int machineInfoPos) throws Exception {
        put(EParameter.previewOneAddress, previewOnePos);
        put(EParameter.previewTwoAddress, previewTwoPos);
        put(EParameter.layersAddress, layerDefinitionPos);
        put(EParameter.parametersAddress, parametersPos);
        put(EParameter.machineInfoAddress, machineInfoPos);

        os.writeInt(getInt(EParameter.magicHeader));
        os.writeInt(getVersion());

        os.writeFloat(getFloat(EParameter.bedXMM));
        os.writeFloat(getFloat(EParameter.bedYMM));
        os.writeFloat(getFloat(EParameter.bedZMM));

        // write padding
        os.writeInt(0);
        os.writeInt(0);
        os.writeInt(0);

        os.writeFloat(getLayerHeight());
        os.writeFloat(getExposureTimeSeconds());
        os.writeFloat(getBottomExposureTimeSeconds());

        os.writeFloat(getOffTimeSeconds());
        os.writeInt(getBottomLayers());

        os.writeInt(getResolutionX());
        os.writeInt(getResolutionY());

        os.writeInt(getPreviewOneOffsetAddress());
        os.writeInt(getLayersDefinitionOffsetAddress());

        os.writeInt(getNumberOfLayers());

        os.writeInt(getPreviewTwoOffsetAddress());
        os.writeInt(getInt(EParameter.printTimeS));

        os.writeInt(((PhotonProjectType)get(EParameter.projectType)).getProjectID());

        os.writeInt(getPrintParametersOffsetAddress());
        os.writeInt(getInt(EParameter.parametersSize));
        os.writeInt(getInt(EParameter.antialiasingLevel));

        os.writeShort(getShort(EParameter.lightPWM));
        os.writeShort(getShort(EParameter.bottomLightPWM));

        os.writeInt(0);
        if (getVersion()>1) {
            os.writeInt(getMachineInfoOffsetAddress());
            os.writeInt(getMachineInfoSize());
        } else {
            // yes they added 1 byte to the header for v2, despite having 4 bytes of padding availiable.
            os.writeInt(0);
        }
    }

    public int getVersion() {
        return getInt(EParameter.version);
    }

    public int getPreviewOneOffsetAddress() {
        return getInt(EParameter.previewOneAddress);
    }

    public int getPreviewTwoOffsetAddress() {
        return getInt(EParameter.previewTwoAddress);
    }

    public int getLayersDefinitionOffsetAddress() {
        return getInt(EParameter.layersAddress);
    }

    public void unLink() {
    }

    public int getPrintParametersOffsetAddress() {
        return getInt(EParameter.parametersAddress);
    }

    public int getMachineInfoOffsetAddress() {
    	return getInt(EParameter.machineInfoAddress);
    }
    
    public int getMachineInfoSize() {
    	return getInt(EParameter.machineInfoSize);
    }


    public void setAntiAliasingLevel(int antiAliasingLevel) {
        put(EParameter.antialiasingLevel, antiAliasingLevel);
    }

    public void setFileVersion(int i) {
        put(EParameter.version, i);
        setAntiAliasingLevel(1);
        put(EParameter.lightPWM, 255);
        put(EParameter.bottomLightPWM, 255);
        if( i == 2) {
            if (!PhotonFilePrintParameters.hasPrintParameters(this)) {
                PhotonFilePrintParameters.initializePrintParameters(this);
            }
            if (!PhotonFileMachineInfo.hasMachineInfo(this)) {
                PhotonFileMachineInfo.initializeMachineInfo("Photon", MACHINE_INFO_SIZE, this);
            }
        }
    }

    public boolean hasAA() {
        return getAALevels() > 1;
    }

    public int getAALevels() {
        if (getVersion()>1) {
            return getInt(EParameter.antialiasingLevel);
        }
        return 1;
    }

    public void setAALevels(int levels, List<PhotonFileLayer> layers) {
        if (getVersion()>1) {
            if (levels < getAALevels()) {
                reduceAaLevels(levels, layers);
            }
            if (levels > getAALevels()) {
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

    public boolean isMirrored() {
        return get(EParameter.projectType) == PhotonProjectType.lcdMirror;
    }

    public void readParameters(byte[] file) throws Exception {
        PhotonFilePrintParameters.initializePrintParameters(getPrintParametersOffsetAddress(), file, this);
        PhotonFileMachineInfo.initializeMachineInfo(getMachineInfoOffsetAddress(), getMachineInfoSize(), file, this);
    }
}
