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

package photon.file;

import photon.file.parts.*;
import photon.file.parts.photon.PhotonFileHeader;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.OutputStream;

/**
 * by bn on 30/06/2018.
 */
public class PhotonFile extends SlicedFile{
    // Dimensions of the preview images on a photon.
    // While I don't know they _have_ to be this, it doesn't hurt to make them this.
    final static int PREVIEW_LARGE_X = 400;
    final static int PREVIEW_LARGE_Y = 300;
    final static int PREVIEW_SMALL_X = 200;
    final static int PREVIEW_SMALL_Y = 125;

    public PhotonFile readFromFile(File file, IPhotonProgress iPhotonProgress) throws Exception {
        byte[] fileData = getBinaryData(file);
        iPhotonProgress.showInfo("Reading Photon file header information...");
        PhotonFileHeader photonFileHeader = new PhotonFileHeader(fileData);
        iFileHeader = photonFileHeader;

        iPhotonProgress.showInfo("Reading photon large preview image information...");
        previewOne = new PhotonFilePreview(photonFileHeader.getPreviewOneOffsetAddress(), fileData);
        iPhotonProgress.showInfo("Reading photon small preview image information...");
        previewTwo = new PhotonFilePreview(photonFileHeader.getPreviewTwoOffsetAddress(), fileData);
        if (photonFileHeader.getVersion() > 1) {
            iPhotonProgress.showInfo("Reading Print parameters information...");
            photonFileHeader.readParameters(fileData);
        }
        iPhotonProgress.showInfo("Reading photon layers information...");
        layers = PhotonFileLayer.readLayers(photonFileHeader, fileData, margin, iPhotonProgress);
        resetMarginAndIslandInfo();

        return this;
    }



    protected void writeFile(OutputStream outputStream) throws Exception {
        int antiAliasLevel = iFileHeader.getAALevels();

        int headerPos = 0;
        int previewOnePos = headerPos + iFileHeader.getByteSize();
        int previewTwoPos = previewOnePos + previewOne.getByteSize();
        int layerDefinitionPos = previewTwoPos + previewTwo.getByteSize();

        int parametersPos = 0;
        int machineInfoPos = 0;
        if (iFileHeader.getVersion() > 1) {
            parametersPos = layerDefinitionPos;
            if (((PhotonFileHeader)iFileHeader).photonFileMachineInfo.getByteSize() > 0) {
	            machineInfoPos = parametersPos + ((PhotonFileHeader)iFileHeader).photonFilePrintParameters.getByteSize();
                layerDefinitionPos = machineInfoPos + ((PhotonFileHeader)iFileHeader).photonFileMachineInfo.getByteSize();
            } else {
                layerDefinitionPos = parametersPos + ((PhotonFileHeader)iFileHeader).photonFilePrintParameters.getByteSize();
            }
        }

        int dataPosition = layerDefinitionPos + (PhotonFileLayer.getByteSize() * iFileHeader.getNumberOfLayers() * antiAliasLevel);


        PhotonOutputStream os = new PhotonOutputStream(outputStream);

        ((PhotonFileHeader)iFileHeader).save(os, previewOnePos, previewTwoPos, layerDefinitionPos, parametersPos, machineInfoPos);
        previewOne.save(os, previewOnePos);
        previewTwo.save(os, previewTwoPos);

        if (iFileHeader.getVersion() > 1) {
            ((PhotonFileHeader)iFileHeader).photonFilePrintParameters.save(os);
            ((PhotonFileHeader)iFileHeader).photonFileMachineInfo.save(os, machineInfoPos);
        }

        // Optimize order for speed read on photon
        for (int i = 0; i < iFileHeader.getNumberOfLayers(); i++) {
            PhotonFileLayer layer = layers.get(i);
            dataPosition = layer.savePos(dataPosition);
            if (antiAliasLevel > 1) {
                for (int a = 0; a < (antiAliasLevel - 1); a++) {
                    dataPosition = layer.getAntiAlias(a).savePos(dataPosition);
                }
            }
        }

        // Order for backward compatibility with photon/cbddlp version 1
        for (int i = 0; i < iFileHeader.getNumberOfLayers(); i++) {
            layers.get(i).save(os);
        }

        if (antiAliasLevel > 1) {
            for (int a = 0; a < (antiAliasLevel - 1); a++) {
                for (int i = 0; i < iFileHeader.getNumberOfLayers(); i++) {
                    layers.get(i).getAntiAlias(a).save(os);
                }
            }
        }

        // Optimize order for speed read on photon
        for (int i = 0; i < iFileHeader.getNumberOfLayers(); i++) {
            PhotonFileLayer layer = layers.get(i);
            layer.saveData(os);
            if (antiAliasLevel > 1) {
                for (int a = 0; a < (antiAliasLevel - 1); a++) {
                    layer.getAntiAlias(a).saveData(os);
                }
            }
        }
    }

    @Override
    public SlicedFile fromSlicedFile(SlicedFile input) {
        iFileHeader = new PhotonFileHeader(input.iFileHeader);
        if( input.hasPreviews() ) {
            previewOne = input.previewOne;
            previewTwo = input.previewTwo;
        } else {
            // need to fake them.

        }
        layers = input.layers;
        islandList = input.islandList;
        islandLayerCount = input.islandLayerCount;
        islandLayers = input.islandLayers;
        margin = input.margin;
        marginLayers = input.marginLayers;
        return this;
    }

    @Override
    public boolean hasPreviews() {
        return true;
    }

    @Override
    public EFileType getType() {
        return EFileType.Photon;
    }
}

