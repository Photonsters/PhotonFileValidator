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

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Hashtable;

/**
 * by bn on 02/07/2018.
 */
public class PhotonLayer {
    public final static byte OFF = 0x00;
    public final static byte SUPPORTED = 0x01;
    public final static byte ISLAND = 0x02;
    public final static byte CONNECTED = 0x03;

    private int width;
    private int height;
    private int islandCount = 0;

    private byte[][] iArray;
    private int[] pixels;
    private int[] rowIslands;

    private static byte[] emptyRow;
    private static int[] emptyCol;
    
    private static byte[] scratchPad;

    public PhotonLayer(int width, int height) {
        this.width = width;
        this.height = height;

        iArray = new byte[height][width];
        pixels = new int[height];
        rowIslands = new int[height];

        if (emptyRow == null || emptyRow.length < width) {
            emptyRow = new byte[width];
        }

        if (emptyCol == null || emptyCol.length < height) {
            emptyCol = new int[height];
        }
        
        if (scratchPad == null || scratchPad.length < width * height) {
        	scratchPad = new byte[width * height];
        }
    }

    public void clear() {
        for (int y = 0; y < height; y++) {
        	System.arraycopy(emptyRow, 0, iArray[y], 0, width);
        }
        System.arraycopy(emptyCol, 0, pixels, 0, height);
        System.arraycopy(emptyCol, 0, rowIslands, 0, height);
    }

    public void supported(int x, int y) {
        iArray[y][x] = SUPPORTED;
        pixels[y]++;
    }

    public void unSupported(int x, int y) {
        iArray[y][x] = CONNECTED;
        pixels[y]++;
    }

    public void island(int x, int y) {
        iArray[y][x] = ISLAND;
        rowIslands[y]++;
        islandCount++;
        pixels[y]++;
    }

    public void remove(int x, int y, byte type) {
        iArray[y][x] = OFF;
        switch (type) {
            case ISLAND:
                rowIslands[y]--;
                islandCount--;
                break;
        }
        pixels[y]--;
    }


    public void reduce() {
        // Double reduce to handle single line connections.
        for (int i = 0; i < 2; i++) {
            if (islandCount > 0) {
                for (int y = 0; y < height; y++) {
                    if (rowIslands[y] > 0) {
                        for (int x = 0; x < width; x++) {
                            if (iArray[y][x] == ISLAND) {
                                if (connected(x, y)) {
                                    makeConnected(x, y);
                                    checkUp(x, y);
                                    if (rowIslands[y] == 0) {
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void checkUp(int x, int y) {
        if (y > 0 && rowIslands[y - 1] > 0 && iArray[y - 1][x] == ISLAND) {
            makeConnected(x, y - 1);
            checkUp(x, y - 1);
        }
        if (x > 0 && rowIslands[y] > 0 && iArray[y][x - 1] == ISLAND) {
            makeConnected(x - 1, y);
            checkBackUp(x - 1, y);
        }
        if (x < (width-1) && rowIslands[y] > 0 && iArray[y][x + 1] == ISLAND) {
            makeConnected(x + 1, y);
            checkFrontUp(x + 1, y);
        }
    }

    private void checkBackUp(int x, int y) {
        if (y > 0 && rowIslands[y - 1] > 0 && iArray[y - 1][x] == ISLAND) {
            makeConnected(x, y - 1);
            checkBackUp(x, y - 1);
        }
        if (x > 0 && rowIslands[y] > 0 && iArray[y][x - 1] == ISLAND) {
            makeConnected(x - 1, y);
            checkBackUp(x - 1, y);
        }
    }

    private void checkFrontUp(int x, int y) {
        if (y > 0 && rowIslands[y - 1] > 0 && iArray[y - 1][x] == ISLAND) {
            makeConnected(x, y - 1);
            checkFrontUp(x, y - 1);
        }
        if (x < (width-1) && rowIslands[y] > 0 && iArray[y][x + 1] == ISLAND) {
            makeConnected(x + 1, y);
            checkFrontUp(x + 1, y);
        }
    }

    private void makeConnected(int x, int y) {
        iArray[y][x] = CONNECTED;
        rowIslands[y]--;
        islandCount--;
    }

    private boolean connected(int x, int y) {
        return x > 0 && (iArray[y][x - 1] & 0x01) == SUPPORTED
                || x < (width - 1) && (iArray[y][x + 1] & 0x01) == SUPPORTED
                || y > 0 && (iArray[y - 1][x] & 0x01) == SUPPORTED
                || (y < (height - 1) && (iArray[y + 1][x] & 0x01) == SUPPORTED);
    }

    public int setIslands(ArrayList<BitSet> islandRows) {
        int islands = 0;
        for (int y = 0; y < height; y++) {
            BitSet bitSet = new BitSet();
            if (rowIslands[y] > 0) {
                for (int x = 0; x < width; x++) {
                    if (iArray[y][x] == ISLAND) {
                        bitSet.set(x);
                    }
                }
            }
            islandRows.add(bitSet);
            islands += rowIslands[y];
        }
        return islands;
    }

    public void unLink() {
        iArray = null;
        pixels = null;
        rowIslands = null;
    }

    public byte[] packLayerImage() {
    	int ptr = 0;
        for (int y = 0; y < height; y++) {
            if (pixels[y] == 0) {
                ptr = add(ptr, OFF, width);
            } else {
                byte current = OFF;
                int length = 0;
                for (int x = 0; x < width; x++) {
                    byte next = iArray[y][x];
                    if (next != current) {
                        if (length > 0) {
                            ptr = add(ptr, current, length);
                        }
                        current = next;
                        length = 1;
                    } else {
                        length++;
                    }
                }
                if (length > 0) {
                    ptr = add(ptr, current, length);
                }
            }
        }
        byte[] img = new byte[ptr];
        System.arraycopy(scratchPad, 0, img, 0, ptr);
        return img;
    }

    public void unpackLayerImage(byte[] packedLayerImage) {
        clear();
        int x = 0;
        int y = 0;
        int imageLength = packedLayerImage.length;
        for (int i = 0; i < imageLength; i++) {
            byte rle = packedLayerImage[i];
            byte colorCode = (byte) ((rle & 0x60) >> 5);

            boolean extended = (rle & 0x80) == 0x80;
            int length = rle & 0x1F;
            if (extended) {
                i++;
                length = (length << 8) | packedLayerImage[i] & 0x00ff;
            }

            Arrays.fill(iArray[y], x, x + length, colorCode);

            switch (colorCode) {
            case SUPPORTED:
                pixels[y]+=length;
                break;
            case CONNECTED:
                pixels[y]+=length;
                break;
            case ISLAND:
                rowIslands[y]+= length;
                islandCount+=length;
                pixels[y]+=length;
                break;

            }
            

            x += length;
            if (x >= width) {
                y++;
                x = 0;
            }
        }

    }
    
    
    private int add(int ptr, byte current, int length) {
        if (length < 32) {
            scratchPad[ptr++] = (byte) ((current << 5) | (length & 0x1f));
        } else {
            scratchPad[ptr++] = (byte) (0x80 | (current << 5) | (length >> 8 & 0x00FF));
            scratchPad[ptr++] = (byte) (length & 0x00FF);
        }
        return ptr;
    }

    /**
     * Get a layer image for drawing.
     * <p/>
     * This will decode the RLE packed layer information and return a list of rows, with color and length information
     *
     * @param packedLayerImage The packed layer image information
     * @param width            The width of the current layer, used to change rows
     * @return A list with the
     */
    public static ArrayList<PhotonRow> getRows(byte[] packedLayerImage, int width, boolean isCalculated) {
        Hashtable<Byte, Color> colors = new Hashtable<>();
        colors.put(OFF, Color.black);
        if (isCalculated) {
            colors.put(SUPPORTED, Color.decode("#008800"));
        } else {
            colors.put(SUPPORTED, Color.decode("#000088"));
        }
        colors.put(CONNECTED, Color.decode("#FFFF00"));
        colors.put(ISLAND, Color.decode("#FF0000"));
        ArrayList<PhotonRow> rows = new ArrayList<>();
        int resolutionX = width - 1;
        PhotonRow currentRow = new PhotonRow();
        rows.add(currentRow);
        int x = 0;
        if (packedLayerImage!=null) { // when user tries to show a layer before its calculated
            for (int i = 0; i < packedLayerImage.length; i++) {
                byte rle = packedLayerImage[i];
                byte colorCode = (byte) ((rle & 0x60) >> 5);
                Color color = colors.get(colorCode);
                boolean extended = (rle & 0x80) == 0x80;
                int length = rle & 0x1F;
                if (extended) {
                    i++;
                    length = (length << 8) | packedLayerImage[i] & 0x00ff;
                }
                currentRow.lines.add(new PhotonLine(color, length));
                x += length;

                if (x >= resolutionX) {
                    currentRow = new PhotonRow();
                    rows.add(currentRow);
                    x = 0;
                }
            }
        }
        return rows;
    }

    public int removeIslands() {
        int count = 0;
        if (islandCount > 0) {
            for (int y = 0; y < height; y++) {
                if (rowIslands[y] > 0) {
                    for (int x = 0; x < width; x++) {
                        if (iArray[y][x] == ISLAND) {
                            remove(x, y, ISLAND);
                            ++count;
                        }
                    }
                }
            }
        }
        return count;
    }

    public int fixlayer() {
        PhotonMatix photonMatix = new PhotonMatix();
        ArrayList<PhotonDot> dots = new ArrayList<>();
        if (islandCount > 0) {
            for (int y = 0; y < height; y++) {
                if (rowIslands[y] > 0) {
                    for (int x = 0; x < width; x++) {
                        if (iArray[y][x] == ISLAND) {
                            photonMatix.clear();
                            int blanks = photonMatix.set(x, y, iArray, width, height);
                            if (blanks>0) { // one or more neighbour pixels are OFF
                                photonMatix.calc();
                                photonMatix.level();
                                photonMatix.calc();

                                for(int ry=0; ry<3; ry++) {
                                    for (int rx = 0; rx < 3; rx++) {
                                        int iy = y-1+ry;
                                        int ix = x-1+rx;
                                        if (iArray[iy][ix] == OFF) {
                                            if (photonMatix.calcMatrix[1+ry][1+rx]>3) {
                                                dots.add(new PhotonDot(ix, iy));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        for(PhotonDot dot : dots) {
            island(dot.x, dot.y);
        }
        return dots.size();
    }

    public byte[] packImageData() {
    	
    	int ptr = 0;
    
        for (int y = 0; y < height; y++) {
            if (pixels[y] == 0) {
                ptr = addPhotonRLE(ptr, true, width);
            } else {
                byte current = OFF;
                int length = 0;
                for (int x = 0; x < width; x++) {
                    byte next = iArray[y][x];
                    if (next != current) {
                        if (length > 0) {
                            ptr = addPhotonRLE(ptr, current==OFF, length);
                        }
                        current = next;
                        length = 1;
                    } else {
                        length++;
                    }
                }
                if (length > 0) {
                    ptr = addPhotonRLE(ptr, current==OFF, length);
                }
            }
        }
        byte[] img = new byte[ptr];
        System.arraycopy(scratchPad, 0, img, 0, ptr);
        return img;
    }

    private int addPhotonRLE(int ptr, boolean off, int length) {
    	
        while (length > 0) {
            int lineLength = length < 125 ? length : 125; // max storage length of 0x7D (125) ?? Why not 127?
            scratchPad[ptr++] = (byte) ((off ? 0x00: 0x80) | (lineLength & 0x7f));
            length -= lineLength;
        }
        
        return ptr;
    }

    public byte get(int x, int y) {
        return iArray[y][x];
    }

}
