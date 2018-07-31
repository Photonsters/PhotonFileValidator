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

package photon.application.render.utils;

import java.awt.*;

public class ZBufferUtil {
    /**
     * Fills a triangle using the scan line algorithm and the z-buffer algorithm
     *
     * @param gr      graphic object
     * @param x       x coordinates for all 3 vertexes
     * @param y       y coordinates for all 3 vertexes
     * @param z       z coordinates for all 3 vertexes
     * @param zbuffer z depth buffer
     */
    public static void fillTriangle(Graphics gr, int[] x, int[] y, int[] z, int[][] zbuffer) {
        /**
         * sort the arrays according the y value
         * y[0]=min
         * y[2]=max
         */
        if (y[0] > y[1]) {
            //swap values using xor
            y[0] ^= y[1];
            y[1] ^= y[0];
            y[0] ^= y[1];
            //swap values using xor
            x[0] ^= x[1];
            x[1] ^= x[0];
            x[0] ^= x[1];
            //swap values using xor
            z[0] ^= z[1];
            z[1] ^= z[0];
            z[0] ^= z[1];
        }
        if (y[1] > y[2]) {
            //swap values using xor
            y[1] ^= y[2];
            y[2] ^= y[1];
            y[1] ^= y[2];
            //swap values using xor
            x[1] ^= x[2];
            x[2] ^= x[1];
            x[1] ^= x[2];
            //swap values using xor
            z[1] ^= z[2];
            z[2] ^= z[1];
            z[1] ^= z[2];
        }
        if (y[0] > y[1]) {
            //swap values using xor
            y[0] ^= y[1];
            y[1] ^= y[0];
            y[0] ^= y[1];
            //swap values using xor
            x[0] ^= x[1];
            x[1] ^= x[0];
            x[0] ^= x[1];
            //swap values using xor
            z[0] ^= z[1];
            z[1] ^= z[0];
            z[0] ^= z[1];
        }

        int p1x, p2x, p1z, p2z; //intersection points with the triangle edges
        int py; //y and z coordinates of the current scan line

        int dif1X, dif2X, dif1Y, dif2Y, dif1Z, dif2Z;

        if (y[0] == y[1]) {
            //pre-calculate some values that don't change during the cycle
            //for optimization purposes
            dif1X = x[2] - x[0];
            dif2X = x[2] - x[1];
            dif1Y = y[2] - y[0];
            dif1Z = z[2] - z[0];
            dif2Z = z[2] - z[1];
            for (py = y[1] + 1; py <= y[2]; py++) {
                //calculate intersection points
                dif2Y = y[2] - py;
                p1x = x[2] - (dif1X * dif2Y) / dif1Y;
                p2x = x[2] - (dif2X * dif2Y) / dif1Y;
                p1z = z[2] - (dif1Z * dif2Y) / dif1Y;
                p2z = z[2] - (dif2Z * dif2Y) / dif1Y;
                //start z-buffer
                drawHorizontalLine(gr, py, p1x, p1z, p2x, p2z, zbuffer);
                //end z-buffer

            }
        } else if (y[1] == y[2]) {
            //pre-calculate some values that don't change during the cycle
            //for optimization purposes
            dif1X = x[2] - x[0];
            dif2X = x[1] - x[0];
            dif1Y = y[1] - y[0];
            dif1Z = z[2] - z[0];
            dif2Z = z[1] - z[0];
            for (py = y[0]; py <= y[1]; py++) {
                //calculate intersection points
                p1x = x[2] - (dif1X * (y[2] - py)) / dif1Y;
                p2x = x[1] - (dif2X * (y[1] - py)) / dif1Y;
                p1z = z[2] - (dif1Z * (y[2] - py)) / dif1Y;
                p2z = z[1] - (dif2Z * (y[1] - py)) / dif1Y;
                //start z-buffer
                drawHorizontalLine(gr, py, p1x, p1z, p2x, p2z, zbuffer);
                //end z-buffer
            }

        } else {
            //pre-calculate some values that don't change during the cycle
            //for optimization purposes
            dif1X = x[2] - x[0];
            dif2X = x[1] - x[0];
            dif1Y = y[2] - y[0];
            dif2Y = y[1] - y[0];
            dif1Z = z[2] - z[0];
            dif2Z = z[1] - z[0];
            for (py = y[0]; py <= y[1]; py++) {
                //calculate intersection points
                p1x = x[2] - (dif1X * (y[2] - py)) / dif1Y;
                p2x = x[1] - (dif2X * (y[1] - py)) / dif2Y;
                p1z = z[2] - (dif1Z * (y[2] - py)) / dif1Y;
                p2z = z[1] - (dif2Z * (y[1] - py)) / dif2Y;
                //start z-buffer
                drawHorizontalLine(gr, py, p1x, p1z, p2x, p2z, zbuffer);
                //end z-buffer
            }

            //re-calculate what changes in the second cycle
            dif2X = x[2] - x[1];
            dif2Y = y[2] - y[1];
            dif2Z = z[2] - z[1];
            for (py = y[1] + 1; py <= y[2]; py++) {
                //calculate intersection points
                p1x = x[2] - (dif1X * (y[2] - py)) / dif1Y;
                p2x = x[2] - (dif2X * (y[2] - py)) / dif2Y;
                p1z = z[2] - (dif1Z * (y[2] - py)) / dif1Y;
                p2z = z[2] - (dif2Z * (y[2] - py)) / dif2Y;
                //start z-buffer
                drawHorizontalLine(gr, py, p1x, p1z, p2x, p2z, zbuffer);
                //end z-buffer
            }

        }

    }

    /**
     * Draws a line using the z-buffer algorithm
     *
     * @param gr      graphic object
     * @param x1      x coordinate of the first vertex
     * @param y1      y coordinate of the first vertex
     * @param z1      z coordinate of the first vertex
     * @param x2      x coordinate of the second vertex
     * @param y2      y coordinate of the second vertex
     * @param z2      z coordinate of the second vertex
     * @param zbuffer z depth buffer
     */
    public static void drawLine(Graphics gr, int x1, int y1, int z1, int x2, int y2, int z2, int[][] zbuffer) {
        int dimX = zbuffer.length;
        int dimY = zbuffer[0].length;
        int difX, difY, difZ;
        int px, pz, py;

        if (y1 > y2) {
            y1 ^= y2;
            y2 ^= y1;
            y1 ^= y2;
            x1 ^= x2;
            x2 ^= x1;
            x1 ^= x2;
            z1 ^= z2;
            z2 ^= z1;
            z1 ^= z2;
        }

        if (x1 > 0 && x1 < dimX && y1 > 0 && y1 < dimY && zbuffer[x1][y1] >= z1) {
            zbuffer[x1][y1] = z1;
            gr.fillRect(x1, y1, 1, 1);
        }
        difX = x2 - x1;
        difY = y2 - y1;
        difZ = z2 - z1;
        int compDifX;
        if (x2 > x1) { compDifX = x2 - x1; } else { compDifX = x1 - x2; }

        if ((compDifX < y2 - y1)) {
            for (py = y1 + 1; py < y2; py++) {
                px = x1 + ((py - y1) * difX / difY);
                pz = z1 + ((py - y1) * difZ / difY);
                if (px > 0 && px < dimX && py > 0 && py < dimY && zbuffer[px][py] >= pz) {
                    zbuffer[px][py] = pz;
                    gr.fillRect(px, py, 1, 1);
                }
            }
        } else if (x1 > x2) {
            for (px = x2 + 1; px < x1; px++) {
                py = y1 + ((px - x1) * difY / difX);
                pz = z1 + ((px - x1) * difZ / difX);
                if (px > 0 && px < dimX && py > 0 && py < dimY && zbuffer[px][py] >= pz) {
                    zbuffer[px][py] = pz;
                    gr.fillRect(px, py, 1, 1);
                }
            }
        } else {
            for (px = x1 + 1; px < x2; px++) {
                py = y1 + ((px - x1) * difY / difX);
                pz = z1 + ((px - x1) * difZ / difX);
                if (px > 0 && px < dimX && py > 0 && py < dimY && zbuffer[px][py] >= pz) {
                    zbuffer[px][py] = pz;
                    gr.fillRect(px, py, 1, 1);
                }
            }
        }

        if (x2 > 0 && x2 < dimX && y2 > 0 && y2 < dimY && zbuffer[x2][y2] >= z2) {
            zbuffer[x2][y2] = z2;
            gr.fillRect(x2, y2, 1, 1);
        }
    }

    /**
     * Draws a line using the z-buffer algorithm
     *
     * @param gr      graphic object
     * @param y       y coordinate
     * @param x1      x coordinate of the first vertex
     * @param z1      z coordinate of the first vertex
     * @param x2      x coordinate of the second vertex
     * @param z2      z coordinate of the second vertex
     * @param zbuffer z depth buffer
     */
    public static void drawHorizontalLine(Graphics gr, int y, int x1, int z1, int x2, int z2, int[][] zbuffer) {
        int dimX = zbuffer.length;
        int dimY = zbuffer[0].length;
        int difX, difZ;
        int px, pz;

        if (x1 > 0 && x1 < dimX && y > 0 && y < dimY && zbuffer[x1][y] >= z1) {
            zbuffer[x1][y] = z1;
            gr.fillRect(x1, y, 1, 1);
        }
        difX = x2 - x1;
        difZ = z2 - z1;

        if (x1 > x2) {
            for (px = x2 + 1; px < x1; px++) {
                pz = z1 + ((px - x1) * difZ / difX);
                if (px > 0 && px < dimX && y > 0 && y < dimY && zbuffer[px][y] >= pz) {
                    zbuffer[px][y] = pz;
                    gr.fillRect(px, y, 1, 1);
                }
            }
        } else {
            for (px = x1 + 1; px < x2; px++) {
                pz = z1 + ((px - x1) * difZ / difX);
                if (px > 0 && px < dimX && y > 0 && y < dimY && zbuffer[px][y] >= pz) {
                    zbuffer[px][y] = pz;
                    gr.fillRect(px, y, 1, 1);
                }
            }
        }

        if (x2 > 0 && x2 < dimX && y > 0 && y < dimY && zbuffer[x2][y] >= z2) {
            zbuffer[x2][y] = z2;
            gr.fillRect(x2, y, 1, 1);
        }
    }
}
