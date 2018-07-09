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

import java.util.ArrayList;
import java.util.BitSet;

/**
 *  by bn on 02/07/2018.
 */
public class PhotonIslands {
    private int width;
    private int height;
    private int unsupportedCount = 0;

    private byte[][] iArray;
    private int[] rowUnsupported;
    private int[] rowSupported;

    public PhotonIslands(int width, int height) {
        this.width = width;
        this.height = height;

        iArray = new byte[height][width];
        rowUnsupported = new int[height];
        rowSupported = new int[height];

    }

    public void supported(int x, int y) {
        iArray[y][x] = 0x01;
        rowSupported[y]++;
    }

    public void unSupported(int x, int y) {
        iArray[y][x] = 0x02;
        rowUnsupported[y]++;
        unsupportedCount++;
    }

    public void reduce() {
        if (unsupportedCount>0) {
            for(int y=0; y<height; y++) {
                if (rowUnsupported[y]>0) {
                    for(int x=0; x<width; x++) {
                        if (iArray[y][x]==0x02) {
                            if (connected(x,y)) {
                                makeConnected(x,y);
                                checkBackUp(x,y);
                                if (rowUnsupported[y]==0) {
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void checkBackUp(int x, int y) {
        if (y > 0 && rowUnsupported[y-1] > 0 && iArray[y - 1][x]==0x02) {
            makeConnected(x,y-1);
            checkBackUp(x,y-1);
        }
        if (x > 0 && rowUnsupported[y] > 0 && iArray[y][x - 1]==0x02) {
            makeConnected(x-1,y);
            checkBackUp(x-1,y);
        }
    }

    private void makeConnected(int x, int y) {
        iArray[y][x]=0x01;
        rowSupported[y]++;
        rowUnsupported[y]--;
        unsupportedCount--;
    }

    private boolean connected(int x, int y) {
        return x > 0 && iArray[y][x - 1] == 0x01
                || x < (width - 1) && iArray[y][x + 1] == 0x01
                || y > 0 && iArray[y - 1][x] == 0x01
                || (y < (height - 1) && iArray[y + 1][x] == 0x01);
    }

    public int setIslands(ArrayList<BitSet> islandRows) {
        int islands = 0;
        for(int y=0; y<height; y++) {
            BitSet bitSet = new BitSet();
            if (rowUnsupported[y]>0) {
                for(int x=0; x<width; x++) {
                    if (iArray[y][x]==0x02) {
                        bitSet.set(x);
                    }
                }
            }
            islandRows.add(bitSet);
            islands += rowUnsupported[y];
        }
        return islands;
    }
}
