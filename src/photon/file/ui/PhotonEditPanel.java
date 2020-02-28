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

import photon.file.parts.PhotonLayer;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * by bn on 16/07/2018.
 */
public class PhotonEditPanel extends JPanel {
    private int width;
    private int height;

    private BufferedImage image;

    private boolean mirrored;

    public PhotonEditPanel(int width, int height) {
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

    private Graphics2D createGraphics() {
        Graphics2D g = image.createGraphics();
        if (mirrored) {
            AffineTransform transform = new AffineTransform();
            transform.setToScale(1, -1);
            transform.translate(0, -image.getHeight());
            g.setTransform(transform);
        }
        return g;
    }

    public void drawLayer(int layerX, int layerY, PhotonLayer layer) {
        Graphics2D g = createGraphics();
        g.setBackground(Color.decode("#999999"));

        g.clearRect(0, 0, width, height);


        for (int y = 0; y < 45; y++) {
            for (int x = 0; x < 75; x++) {
                int x1 = 15 + x * 10;
                int y1 = 15 + y * 10;

                switch (layer.get(layerY + y, layerX + x)) {
                    case PhotonLayer.SUPPORTED:
                        g.setColor(Color.decode("#008800"));
                        break;

                    case PhotonLayer.CONNECTED:
                        g.setColor(Color.decode("#FFFF00"));
                        break;

                    case PhotonLayer.ISLAND:
                        g.setColor(Color.decode("#FF0000"));
                        break;

                    default:
                        g.setColor(Color.black);

                }
                g.fillRect(x1, y1, 9, 9);
            }
        }

        g.dispose();
    }

    public void drawDot(int layerX, int layerY, PhotonLayer layer, Color color) {
        Graphics2D g = createGraphics();
        g.setColor(color);
        g.fillRect(15+layerX*10, 15+layerY*10, 9, 9);
        g.dispose();
    }

    public void drawRect(Rectangle r, Color color) {
        Graphics2D g = createGraphics();
        g.setColor(color);
        g.drawRect(15+r.x*10, 15+r.y*10, (r.width + 1) * 10-1, (r.height + 1) * 10-1);
        g.dispose();
    }
}
