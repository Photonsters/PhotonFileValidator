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

import photon.file.SlicedFile;
import photon.file.parts.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.OutputStream;

/**
 * by bn on 30/06/2018.
 */
public class PhotonFile extends SlicedFile {
    // Dimensions of the preview images on a photon.
    // While I don't know they _have_ to be this, it doesn't hurt to make them this.


    // mostly debug.
    private boolean isValid = false;

    public PhotonFile readFromFile(File file, IPhotonProgress iPhotonProgress) throws Exception {
        byte[] fileData = getBinaryData(file);
        iPhotonProgress.showInfo("Reading Photon file header information...");
        PhotonFileHeader photonFileHeader = new PhotonFileHeader(fileData);
        fileHeader = photonFileHeader;

        iPhotonProgress.showInfo("Reading photon large preview image information...");
        previewOne = new PhotonFilePreview(photonFileHeader.getPreviewOneOffsetAddress(), fileData);
        iPhotonProgress.showInfo("Reading photon small preview image information...");
        previewTwo = new PhotonFilePreview(photonFileHeader.getPreviewTwoOffsetAddress(), fileData);
        if (photonFileHeader.getVersion() > 1) {
            iPhotonProgress.showInfo("Reading Print parameters information...");
            photonFileHeader.readParameters(fileData);
            isValid = PhotonFileMachineInfo.hasMachineInfo(photonFileHeader)
                && PhotonFilePrintParameters.hasPrintParameters(photonFileHeader);
        } else {
            isValid = true;
        }
        iPhotonProgress.showInfo("Reading photon layers information...");
        layers = PhotonFileLayer.readLayers(photonFileHeader, fileData, margin, iPhotonProgress);
        resetMarginAndIslandInfo();
        return this;
    }



    protected void writeFile(OutputStream outputStream) throws Exception {
        int antiAliasLevel = fileHeader.getAALevels();

        PhotonFileHeader photonFileHeader = (PhotonFileHeader)fileHeader;

        int headerPos = 0;
        int previewOnePos = headerPos + photonFileHeader.getByteSize();
        int previewTwoPos = previewOnePos + previewOne.getByteSize();
        int layerDefinitionPos = previewTwoPos + previewTwo.getByteSize();

        int parametersPos = 0;
        int machineInfoPos = 0;

        if (photonFileHeader.getVersion() > 1) {
            parametersPos = layerDefinitionPos;
            if (PhotonFileMachineInfo.getByteSize(fileHeader) > 0) {
	            machineInfoPos = parametersPos + PhotonFilePrintParameters.getByteSize();
                layerDefinitionPos = machineInfoPos + PhotonFileMachineInfo.getByteSize(fileHeader);
            } else {
                layerDefinitionPos = parametersPos + PhotonFilePrintParameters.getByteSize();
            }
        }

        int dataPosition = layerDefinitionPos + (PhotonFileLayer.getByteSize() * fileHeader.getNumberOfLayers() * antiAliasLevel);


        PhotonOutputStream os = new PhotonOutputStream(outputStream);

        photonFileHeader.save(os, previewOnePos, previewTwoPos, layerDefinitionPos, parametersPos, machineInfoPos);
        previewOne.save(os, previewOnePos);
        previewTwo.save(os, previewTwoPos);

        if (photonFileHeader.getVersion() > 1) {
            PhotonFilePrintParameters.save(os, photonFileHeader);
            PhotonFileMachineInfo.save(os, machineInfoPos, photonFileHeader);
        }

        // Optimize order for speed read on photon
        for (int i = 0; i < fileHeader.getNumberOfLayers(); i++) {
            PhotonFileLayer layer = layers.get(i);
            dataPosition = layer.savePos(dataPosition);
            if (antiAliasLevel > 1) {
                for (int a = 0; a < (antiAliasLevel - 1); a++) {
                    dataPosition = layer.getAntiAlias(a).savePos(dataPosition);
                }
            }
        }

        // Order for backward compatibility with photon/cbddlp version 1
        for (int i = 0; i < fileHeader.getNumberOfLayers(); i++) {
            layers.get(i).save(os);
        }

        if (antiAliasLevel > 1) {
            for (int a = 0; a < (antiAliasLevel - 1); a++) {
                for (int i = 0; i < fileHeader.getNumberOfLayers(); i++) {
                    layers.get(i).getAntiAlias(a).save(os);
                }
            }
        }

        // Optimize order for speed read on photon
        for (int i = 0; i < fileHeader.getNumberOfLayers(); i++) {
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
        fileHeader = new PhotonFileHeader(input.getHeader());
        if( input.hasPreviews() ) {
            previewOne = input.getPreviewOne();
            previewTwo = input.getPreviewTwo();
        } else {
            // need to fake them.
            previewOne = PhotonFilePreview.getDummyLargePreview();
            previewTwo = PhotonFilePreview.getDummySmallPreview();
        }
        layers = input.getLayers();
        islandList = input.getIslandList();
        islandLayerCount = input.getIslandLayerCount();
        islandLayers = input.getIslandLayers();
        margin = input.getMargin();
        marginLayers = input.getMarginLayers();
        isValid = PhotonFileMachineInfo.hasMachineInfo(fileHeader)
                && PhotonFilePrintParameters.hasPrintParameters(fileHeader);
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

