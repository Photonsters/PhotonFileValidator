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
import photon.file.parts.photon.PhotonFile;
import photon.file.parts.sl1.PhotonSFile;
import photon.file.parts.sl1.Sl1File;
import photon.file.parts.zip.ZipFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


public abstract class SlicedFile {
    protected SlicedFileHeader fileHeader;
    // TODO:: Should we push these down to those filetypes which have previews?
    protected PhotonFilePreview previewOne;
    protected PhotonFilePreview previewTwo;
    protected List<PhotonFileLayer> layers;

    protected StringBuilder islandList;
    protected int islandLayerCount;
    protected ArrayList<Integer> islandLayers;

    protected int margin;
    protected ArrayList<Integer> marginLayers;

    /**
     * Load a file of a supported type, delegating down to the correct implementation
     * @param file to load
     * @param iPhotonProgress to report progress back
     * @param margin the margin size to use
     * @return a loaded object on success
     * @throws Exception on failure.
     */
    static public SlicedFile readFile(File file, IPhotonProgress iPhotonProgress, int margin) throws Exception {
        EFileType type = EFileType.identifyFile(file.getName());
        SlicedFile result;
        switch(type) {
            case Photon:
            case Cbddlp: // actually the same
                result =  new PhotonFile();
                break;
            case PhotonS:
                result = new PhotonSFile();
                break;
            case Sl1:
                result = new Sl1File();
                break;
            case Zip:
                result = new ZipFile();
                break;
            default:
                throw new UnsupportedOperationException("Unknown file type passed to readFile. This is a bug.");
        }

        result.setMargin(margin);
        result.readFromFile(file, iPhotonProgress);
        return result;
    }
    /**
     * Load a file of the given type.
     * @param file File object to load
     * @param iPhotonProgress For reporting progress back to the UI
     * @throws Exception on error
     * @return a loaded object
     */
    abstract public SlicedFile readFromFile(File file, IPhotonProgress iPhotonProgress) throws Exception;

    /**
     * Actually write a file of the given type out.
     * @param outputStream to write to.
     * @throws Exception on failure.
     */
    abstract protected void writeFile(OutputStream outputStream) throws Exception;

    /**
     * Convert from one file type to another.
     * @param input to convert from.
     * @return self (for builder pattern)
     */
    abstract public SlicedFile fromSlicedFile(SlicedFile input);

    /**
     * Whether this sliced file has associated large preview image
     * @return true iff it has previews.
     */
    public boolean hasPreviewLarge() {
        return previewOne != null;
    }

    /**
     * Whether this sliced file has associated small preview image
     * @return true iff it has previews.
     */
    public boolean hasPreviewSmall() {
        return previewTwo != null;
    }

    /**
     * What type of sliced file this is
     * @return the type of file this is.
     */
    public abstract EFileType getType();

    public void saveFile(File file) throws Exception {
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        writeFile(fileOutputStream);
        fileOutputStream.flush();
        fileOutputStream.close();
    }

    protected static byte[] getBinaryData(File entry) throws Exception {
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
        if (fileHeader == null) return "";
        return fileHeader.getInformation();
    }

    public SlicedFileHeader getHeader() {
        return fileHeader;
    }

    public List<PhotonFileLayer> getLayers() {
        return layers;
    }

    public StringBuilder getIslandList() {
        return islandList;
    }

    public int getMargin() {
        return margin;
    }

    public int getIslandLayerCount() {
        if (islandList == null) {
            findIslands();
        }
        return islandLayerCount;
    }

    public ArrayList<Integer> getIslandLayers() {
        if (islandList == null) {
            findIslands();
        }
        return islandLayers;
    }

    public void setMargin(int margin) {
        this.margin = margin;
    }

    public ArrayList<Integer> getMarginLayers() {
        if (marginLayers == null) {
            return new ArrayList<>();
        }
        return marginLayers;
    }

    public String getMarginInformation() {
        if (marginLayers == null) {
            return "No safety margin set, printing to the border.";
        } else {
            if (marginLayers.size() == 0) {
                return "The model is within the defined safety margin (" + this.margin + " pixels).";
            } else if (marginLayers.size() == 1) {
                return "The layer " + marginLayers.get(0) + " contains model parts that extend beyond the margin.";
            }
            StringBuilder marginList = new StringBuilder();
            int count = 0;
            for (int layer : marginLayers) {
                if (count > 10) {
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
        if (islandList == null) {
            findIslands();
        }
        if (islandLayerCount == 0) {
            return "Whoopee, all is good, no unsupported areas";
        } else if (islandLayerCount == 1) {
            return "Unsupported islands found in layer " + islandList.toString();
        }
        return "Unsupported islands found in layers " + islandList.toString();
    }

    private void findIslands() {
        if (islandLayers != null) {
            islandLayers.clear();
            islandList = new StringBuilder();
            islandLayerCount = 0;
            if (layers != null) {
                for (int i = 0; i < fileHeader.getNumberOfLayers(); i++) {
                    PhotonFileLayer layer = layers.get(i);
                    if (layer.getIsLandsCount() > 0) {
                        if (islandLayerCount < 11) {
                            if (islandLayerCount == 10) {
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
        }
    }

    public int getWidth() {
        return fileHeader.getResolutionY();
    }

    public int getHeight() {
        return fileHeader.getResolutionX();
    }

    public int getLayerCount() {
        return fileHeader.getNumberOfLayers();
    }

    public PhotonFileLayer getLayer(int i) {
        if (layers != null && layers.size() > i) {
            return layers.get(i);
        }
        return null;
    }

    public long getPixels() {
        long total = 0;
        if (layers != null) {
            for (PhotonFileLayer layer : layers) {
                total += layer.getPixels();
            }
        }
        return total;
    }

    public SlicedFileHeader getPhotonFileHeader() {
        return fileHeader;
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
        if (islandLayers != null) {
            islandLayers.clear();
        }
        if (marginLayers != null) {
            marginLayers.clear();
        }
        fileHeader.unLink();
        fileHeader = null;
        if (previewOne != null) {
            previewOne.unLink();
            previewOne = null;
        }
        if (previewTwo != null) {
            previewTwo.unLink();
            previewTwo = null;
        }
        System.gc();
    }


    public void adjustLayerSettings() {
        for (int i = 0; i < layers.size(); i++) {
            PhotonFileLayer layer = layers.get(i);
            if (i < fileHeader.getBottomLayers()) {
                layer.setLayerExposure(fileHeader.getBottomExposureTimeSeconds());
            } else {
                layer.setLayerExposure(fileHeader.getExposureTimeSeconds());
            }
            layer.setLayerOffTimeSeconds(fileHeader.getOffTimeSeconds());
        }
    }

    public void fixAll(IPhotonProgress progres) throws Exception {
        boolean layerWasFixed = false;
        do {
            do {
                // Repeatedly fix layers until none are possible to fix
                // Fixing some layers can make other layers auto-fixable
                layerWasFixed = fixLayers(progres);
            } while(layerWasFixed);
            if(islandLayers.size() > 0) {
                // Nothing can be done further, just remove all layers left
                layerWasFixed = removeAllIslands(progres) || layerWasFixed;
            }
            if(layerWasFixed && islandLayers.size() > 0) {
                // We could've created new islands by removing islands, repeat fixing process
                // until everything is fixed or nothing can be done
                progres.showInfo("<br>Some layers were fixed, but " + islandLayers.size() + " still unsupported, repeating...<br>");
            }
        } while(layerWasFixed);
    }

    public boolean removeAllIslands(IPhotonProgress progres) throws Exception {
        boolean layersFixed = false;
        progres.showInfo("Removing islands from " + islandLayers.size() + " layers...<br>");
        PhotonLayer layer = null;
        for (int layerNo : islandLayers) {
            PhotonFileLayer fileLayer = layers.get(layerNo);
            if (layer == null) {
                layer = fileLayer.getLayer();
            } else {
                fileLayer.getUpdateLayer(layer);
            }
            progres.showInfo("Removing islands from layer " + layerNo);

            int removed = layer.removeIslands();
            if(removed == 0) {
                progres.showInfo(", but nothing could be done.");
            } else {
                progres.showInfo(", " + removed + " islands removed");
                fileLayer.saveLayer(layer);
                calculate(layerNo);
                if (layerNo < getLayerCount() - 1) {
                    calculate(layerNo + 1);
                }
                layersFixed = true;
            }
            progres.showInfo("<br>");
        }
        findIslands();
        return layersFixed;
    }

    public boolean fixLayers(IPhotonProgress progres) throws Exception {
        boolean layersFixed = false;
        PhotonLayer layer = null;
        for (int layerNo : islandLayers) {
            progres.showInfo("Checking layer " + layerNo);

            // Unpack the layer data to the layer utility class
            PhotonFileLayer fileLayer = layers.get(layerNo);
            if (layer == null) {
                layer = fileLayer.getLayer();
            } else {
                fileLayer.getUpdateLayer(layer);
            }

            int changed = fixit(progres, layer, fileLayer, 10);
            if (changed == 0) {
                progres.showInfo(", but nothing could be done.");
            } else {
                fileLayer.saveLayer(layer);
                calculate(layerNo);
                if (layerNo < getLayerCount() - 1) {
                    calculate(layerNo + 1);
                }
                layersFixed = true;
            }

            progres.showInfo("<br>");

        }
        findIslands();
        return layersFixed;
    }

    private int fixit(IPhotonProgress progres, PhotonLayer layer, PhotonFileLayer fileLayer, int loops) throws Exception {
        int changed = layer.fixlayer();
        if (changed > 0) {
            layer.reduce();
            fileLayer.updateLayerIslands(layer);
            progres.showInfo(", " + changed + " pixels changed");
            if (loops > 0) {
                changed += fixit(progres, layer, fileLayer, loops - 1);
            }
        }
        return changed;
    }

    public void calculateAaLayers(IPhotonProgress progres, PhotonAaMatrix photonAaMatrix) throws Exception {
        PhotonFileLayer.calculateAALayers(fileHeader, layers, photonAaMatrix, progres);
    }

    public void calculate(IPhotonProgress progres) throws Exception {
        PhotonFileLayer.calculateLayers(fileHeader, layers, margin, progres);
        resetMarginAndIslandInfo();
    }

    public void calculate(int layerNo) throws Exception {
        PhotonFileLayer.calculateLayers(fileHeader, layers, margin, layerNo);
        resetMarginAndIslandInfo();
    }

    protected void resetMarginAndIslandInfo() {
        islandList = null;
        islandLayerCount = 0;
        islandLayers = new ArrayList<>();

        if (margin > 0) {
            marginLayers = new ArrayList<>();
            int i = 0;
            for (PhotonFileLayer layer : layers) {
                if (layer.doExtendMargin()) {
                    marginLayers.add(i);
                }
                i++;
            }
        }
    }

    public float getZdrift() {
        float expectedHeight = fileHeader.getLayerHeight() * (fileHeader.getNumberOfLayers() - 1);
        float actualHeight = layers.get(layers.size() - 1).getLayerPositionZ();
        return expectedHeight - actualHeight;

    }

    public void fixLayerHeights() {
        int index = 0;
        for (PhotonFileLayer layer : layers) {
            layer.setLayerPositionZ(index * fileHeader.getLayerHeight());
            index++;
        }
    }

    public int getVersion() {
        return fileHeader.getInt(EParameter.version);
    }

    public boolean hasAA() {
        return fileHeader.hasAA();
    }

    public int getAALevels() {
        return fileHeader.getAALevels();
    }

    public void changeToVersion2() {
        fileHeader.put(EParameter.version, 2);
    }

    // only call this when recalculating AA levels
    public void setAALevels(int levels) {
        fileHeader.setAALevels(levels, layers);
    }

}

