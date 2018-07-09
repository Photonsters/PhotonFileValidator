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

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * by bn on 30/06/2018.
 */
public class PhotonFile {
    private PhotonFileHeader photonFileHeader;
    private PhotonFilePreview previewOne;
    private PhotonFilePreview previewTwo;
    private List<PhotonFileLayer> layers;

    private StringBuilder islandList;
    private int islandLayerCount;
    private ArrayList<Integer> islandLayers;

    private ArrayList<Integer> marginLayers;

    public PhotonFile readFile(File file) throws Exception {
        return readFile(getBinaryData(file));
    }

    public PhotonFile readFile(File file, IPhotonLoadProgress iPhotonLoadProgress) throws Exception {
        return readFile(getBinaryData(file), iPhotonLoadProgress);
    }

    public PhotonFile readFile(byte[] file) throws Exception {
        return readFile(file, new DummyPhotonLoadProgress());
    }

    public PhotonFile readFile(byte[] file, IPhotonLoadProgress iPhotonLoadProgress) throws Exception {
        iPhotonLoadProgress.showInfo("Reading photon file header information...");
        photonFileHeader = new PhotonFileHeader(file);
        iPhotonLoadProgress.showInfo("Reading photon large preview image information...");
        previewOne = new PhotonFilePreview(photonFileHeader.getPreviewOneOffsetAddress(), file);
        iPhotonLoadProgress.showInfo("Reading photon small preview image information...");
        previewTwo = new PhotonFilePreview(photonFileHeader.getPreviewTwoOffsetAddress(), file);
        iPhotonLoadProgress.showInfo("Reading photon layers information...");
        layers = PhotonFileLayer.readLayers(photonFileHeader, file, iPhotonLoadProgress);
        islandList = null;
        islandLayerCount = 0;
        islandLayers = new ArrayList<>();
        return this;
    }

    public void saveFile(File file) throws Exception {
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        writeFile(fileOutputStream);
        fileOutputStream.flush();
        fileOutputStream.close();
    }

    public byte[] saveFile() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        writeFile(baos);
        return baos.toByteArray();
    }

    private void writeFile(OutputStream outputStream) throws Exception {
        int headerPos = 0;
        int previewOnePos = headerPos + photonFileHeader.getByteSize();
        int previewTwoPos = previewOnePos + previewOne.getByteSize();
        int layerDefinitionPos = previewTwoPos + previewTwo.getByteSize();
        int dataPosition = layerDefinitionPos + PhotonFileLayer.getByteSize() * photonFileHeader.getNumberOfLayers();

        PhotonOutputStream os = new PhotonOutputStream(outputStream);

        photonFileHeader.save(os, previewOnePos, previewTwoPos, layerDefinitionPos);
        previewOne.save(os, previewOnePos);
        previewTwo.save(os, previewTwoPos);

        for(int i = 0; i < photonFileHeader.getNumberOfLayers(); i++) {
            dataPosition = layers.get(i).save(os, dataPosition);
        }

        for(int i = 0; i < photonFileHeader.getNumberOfLayers(); i++) {
            layers.get(i).saveData(os);
        }
    }


    private byte[] getBinaryData(File entry) throws Exception {
        if (entry.isFile()) {
            int fileSize = (int) entry.length();
            byte[] fileData = new byte[fileSize];

            InputStream stream = new FileInputStream(entry);
            int bytesRead = 0;
            while (bytesRead < fileSize) {
                int readCount = stream.read(fileData, bytesRead, fileSize - bytesRead);
                if (readCount < 0) {
                    throw new IOException("Could not read all bytes of the file");
                }
                bytesRead += readCount;
            }

            return fileData;
        }
        return null;
    }

    public String getInformation() {
        return String.format("T: %.3f", photonFileHeader.getLayerHeight()) +
                ", E: " + formatSeconds(photonFileHeader.getNormalExposure()) +
                ", O: " + formatSeconds(photonFileHeader.getOffTime()) +
                ", BE: " + formatSeconds(photonFileHeader.getBottomExposureTimeSeconds()) +
                String.format(", BL: %d", photonFileHeader.getBottomLayers());
    }

    public String formatSeconds(float time) {
        if (time % 1 == 0) {
            return String.format("%.0fs", time);
        } else {
            return String.format("%.1fs", time);
        }
    }

    public int getIslandLayerCount() {
        if (islandList==null) {
            findIslands();
        }
        return islandLayerCount;
    }

    public ArrayList<Integer> getIslandLayers() {
        if (islandList==null) {
            findIslands();
        }
        return islandLayers;
    }

    public ArrayList<Integer> getMarginLayers() {
        if (marginLayers==null) {
            return new ArrayList<>();
        }
        return marginLayers;
    }

    public String getMarginInformation() {
        if (marginLayers==null) {
            return "No safty margin set, printing to the boarder.";
        } else {
            if (marginLayers.size()==0) {
                return "The model is within the defined safty margin.";
            } else if (marginLayers.size()==1) {
                return "The layer " + marginLayers.get(0) + " contains model parts that extend beyond the margin.";
            }
            StringBuilder marginList = new StringBuilder();
            int count = 0;
            for(int layer : marginLayers) {
                if (count>10) {
                    marginList.append(", ...");
                    break;
                } else {
                    if (marginList.length() > 0) marginList.append(", ");
                    marginList.append(layer);
                }
                count++;
            }
            return "The layers " + marginList.toString() + " contains model parts that extend beyond the margin.";
        }
    }

    public String getLayerInformation() {
        if (islandList==null) {
            findIslands();
        }
        if (islandLayerCount==0) {
            return "No layers have islands :-)";
        } else if (islandLayerCount==1) {
            return "Don't print this file, layer " + islandList.toString() + " has one or more unsupported islands.";
        }
        return "Don't print this file, layers " + islandList.toString() + " have unsupported islands.";
    }

    private void findIslands() {
        islandList = new StringBuilder();
        islandLayerCount = 0;
        for(int i=0; i<photonFileHeader.getNumberOfLayers(); i++) {
            PhotonFileLayer layer = layers.get(i);
            if (layer.getIsLandsCount()>0) {
                if (islandLayerCount<11) {
                    if (islandLayerCount==10) {
                        islandList.append(", ...");
                    } else {
                        if (islandList.length() > 0) islandList.append(", ");
                        islandList.append(i);
                    }
                }
                islandLayerCount++;
                islandLayers.add(i);
            }
        }
    }

    public int getWidth() {
        return photonFileHeader.getResolutionY();
    }

    public int getHeight() {
        return photonFileHeader.getResolutionX();
    }

    public int getLayerCount() {
        return photonFileHeader.getNumberOfLayers();
    }

    public PhotonFileLayer getLayer(int i) {
        if (layers!=null && layers.size()>i) {
            return layers.get(i);
        }
        return null;
    }

    public long getPixels() {
        long total = 0;
        if (layers!=null) {
            for(PhotonFileLayer layer : layers) {
                total += layer.getPixels();
            }
        }
        return total;
    }

    public PhotonFileHeader getPhotonFileHeader() {
        return photonFileHeader;
    }

    public PhotonFilePreview getPreviewOne() {
        return previewOne;
    }

    public PhotonFilePreview getPreviewTwo() {
        return previewTwo;
    }


    public void unLink() {
        while (!layers.isEmpty()) {
            PhotonFileLayer layer = layers.remove(0);
            layer.unLink();
        }
        if (islandLayers!=null) {
            islandLayers.clear();
        }
        if (marginLayers!=null) {
            marginLayers.clear();
        }
        photonFileHeader.unLink();
        photonFileHeader = null;
        previewOne.unLink();
        previewOne = null;
        previewTwo.unLink();
        previewTwo = null;
        System.gc();
    }

    public void checkMargin(int margin) {
        marginLayers = new ArrayList<>();
        if (layers!=null) {
            int i = 0;
            for(PhotonFileLayer layer : layers) {
                if (layer.checkMagin(margin)) {
                    marginLayers.add(i);
                }
                i++;
            }
        }
    }

    public void adjustLayerSettings() {
        for(int i = 0; i<layers.size(); i++) {
            PhotonFileLayer layer = layers.get(i);
            if (i<photonFileHeader.getBottomLayers()) {
                layer.setLayerExposure(photonFileHeader.getBottomExposureTimeSeconds());
            } else {
                layer.setLayerExposure(photonFileHeader.getNormalExposure());
            }
            layer.setLayerOffTimeSeconds(photonFileHeader.getOffTimeSeconds());
        }
    }
}

