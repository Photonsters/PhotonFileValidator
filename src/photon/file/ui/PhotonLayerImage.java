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

package photon.file.ui;

import photon.file.parts.PhotonFileLayer;
import photon.file.parts.PhotonLine;
import photon.file.parts.PhotonRow;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.BitSet;

/**
 * by bn on 02/07/2018.
 */
public class PhotonLayerImage extends JPanel {
    private int width;
    private int height;
    private float scale = 1f;
    private BufferedImage image;
    private boolean mirrored;

    public PhotonLayerImage(int width, int height) {
        this.width = width;
        this.height = height;
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        setPreferredSize(new Dimension(width, height));
    }

    public void setMirrored(boolean mirrored) {
        this.mirrored = mirrored;
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.drawImage(image, 0, 0, null);
    }

    public void reScale(int width, int height) {
        reScale(scale, width, height);
    }

    public void reScale(float scale, int width, int height) {
        this.scale = scale;
        this.width = width;
        this.height = height;

        image = new BufferedImage((int) (width * scale), (int) (height * scale), BufferedImage.TYPE_INT_RGB);
        setPreferredSize(new Dimension((int) (width * scale), (int) (height * scale)));
    }

    public void drawLayer(PhotonFileLayer layer, int margin) {
        if (layer != null) {
            Graphics2D g = image.createGraphics();

            if (mirrored) {
                AffineTransform transform = new AffineTransform();
                transform.setToScale(1, -1);
                transform.translate(0, -image.getHeight());
                g.setTransform(transform);
            }

            g.scale(scale, scale);

            g.clearRect(0, 0, width, height);

            if (margin > 0) {
                int x2 = (width - 1) - margin;
                int y2 = (height - 1) - margin;
                g.setColor(Color.decode("#009999"));
                g.drawLine(margin, margin, x2, margin);
                g.drawLine(margin, margin, margin, y2);
                g.drawLine(margin, y2, x2, y2);
                g.drawLine(x2, margin, x2, y2);
            }

            if (layer.isCalculated) {
                if (layer.getIsLandsCount() < 100) {
                    int columnNumber = 0;
                    g.setColor(Color.decode("#550000"));
                    for (BitSet column : layer.getIslandRows()) {
                        drawCross(g, columnNumber, column);
                        columnNumber++;
                    }

                }

                ArrayList<PhotonRow> rows = layer.getRows();
                if (rows != null) {
                    int columnNumber = 0;
                    for (PhotonRow row : rows) {
                        int i = 0;
                        for (PhotonLine line : row.lines) {
                            int end = i + line.length;
                            if (line.color != Color.black) {
                                g.setColor(line.color);
                                g.drawLine(columnNumber, i, columnNumber, end);
                            }
                            i = end;
                        }
                        columnNumber++;
                    }
                }

            } else {
                g.setColor(Color.decode("#008888"));
                int columnNumber = 0;
                for (BitSet column : layer.getUnknownRows()) {
                    drawDot(g, columnNumber, column);
                    columnNumber++;
                }
            }

/*
            if (rootLayer) {
                g.setColor(Color.decode("#008800"));
                int columnNumber = 0;
                for (BitSet column : layer.getSupportedRows()) {
                    drawDot(g, columnNumber, column);
                    columnNumber++;
                }
            } else {
                int columnNumber = 0;
                g.setColor(Color.decode("#008800"));
                for (BitSet column : layer.getSupportedRows()) {
                    drawDot(g, columnNumber, column);
                    columnNumber++;
                }

                columnNumber = 0;
                g.setColor(Color.decode("#FFFF00"));
                for (BitSet column : layer.getUnSupportedRows()) {
                    drawDot(g, columnNumber, column);
                    columnNumber++;
                }

                columnNumber = 0;
                g.setColor(Color.decode("#FF0000"));
                for (BitSet column : layer.getIslandRows()) {
                    drawDot(g, columnNumber, column);
                    columnNumber++;
                }
            }
*/
            g.dispose();
        }
    }

    private void drawDot(Graphics2D g, int columnNumber, BitSet column) {
        if (!column.isEmpty()) {
            for (int i = column.nextSetBit(0); i >= 0; i = column.nextSetBit(i + 1)) {
                g.drawLine(columnNumber, i, columnNumber, i);
            }
        }
    }

    private void drawCross(Graphics2D g, int columnNumber, BitSet column) {
        if (!column.isEmpty()) {
            for (int i = column.nextSetBit(0); i >= 0; i = column.nextSetBit(i + 1)) {
                g.drawLine(columnNumber, 0, columnNumber, height - 1);
                g.drawLine(0, i, width - 1, i);
            }
        }
    }

}
