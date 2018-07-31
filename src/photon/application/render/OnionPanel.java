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

package photon.application.render;

import photon.application.render.interfaces.IRenderer;
import photon.application.render.storage.Renderer;
import photon.application.render.storage.RotationBaseMatrix;
import photon.application.render.elements.BaseElement;
import photon.application.render.elements.World;
import photon.file.PhotonFile;
import photon.file.parts.PhotonFileLayer;
import photon.file.parts.PhotonLayer;

import javax.swing.*;
import java.awt.*;

public class OnionPanel extends JPanel {

    private World world; //world object to put 3d objects here before rendering
    private IRenderer renderer; //the renderer

    public OnionPanel() {
        super();

        BaseElement baseElement;

        renderer = new Renderer(); //create a new Z-buffer renderer
        world = new World(); //create a new world object


        baseElement = new PhotonBuildPlateTop();
        baseElement.setColor(0, 255, 255);
        world.add(baseElement);

        baseElement = new PhotonBuildPlate(2560, 1440, 50, -1000);
        baseElement.setColor(0, 255, 255);
        baseElement.scale(0.1);
        world.add(baseElement);

        world.rotate(new RotationBaseMatrix(45, 0, 0));
    }

    @Override
    public void paint(Graphics gr) {
        Image view = this.createImage(this.getWidth(), this.getHeight());
        Graphics g = view.getGraphics();
        g.setColor(Color.black);
        g.fillRect(this.getX(), this.getY(), this.getWidth(), this.getHeight());
        renderer.render(world, view, this);

        gr.drawImage(view, 0, 0, this);
    }


    private static final int MAX_SHOW =  500;

    public void drawLayer(int layerNo, PhotonFileLayer fileLayer, PhotonFile photonFile, int screenHeight) {
        double scaleFactor = screenHeight * 1f / (photonFile.getHeight() * 2.6f);

        world.clear();

        int showMax = photonFile.getLayerCount();
        // showMax = Integer.min(showMax, MAX_SHOW);


        int layerOffset = 0; // showMax / 2;

        BaseElement baseElement;

        int offset = layerOffset - 1;

        if (layerNo > 1) {
            int showLayers = layerNo - 1;
            if (showLayers > showMax) showLayers = showMax;


            int b = (showLayers / 40);
            double a = b / (showLayers - 10d);

//            boolean colorUp = true;
//            int colorIndex = 0;

            long pixels = 0;

            for (int i = showLayers; i > 0; i--) {
                int prevNo = layerNo - showLayers + i;
                PhotonFileLayer photonFileLayer = photonFile.getLayer(prevNo);

                pixels += photonFileLayer.getPixels();
                if (pixels>30000000L) {
                    showMax = 0;
                    break;
                }

                baseElement = new ResinLayer(2560, 1440, photonFileLayer, PhotonLayer.OFF, offset);
                baseElement.scale(scaleFactor);
                if (prevNo < photonFile.getPhotonFileHeader().getBottomLayers()) {
                    baseElement.setColor(0, 95, 63);
                } else {
//                    if (colorUp) {
//                        colorIndex+=5;
//                        colorUp = colorIndex<=30;
//                    } else {
//                        colorIndex-=5;
//                        colorUp = colorIndex<=0;
//                    }
//                    baseElement.setColor(63, 110 + colorIndex, 63);
                    int col;
                    if (i < (showLayers - 20)) {
                        col = 64 + (i * 100 / showLayers);
                    } else {
                        col = 204 - (showLayers-i)*2;
                    }
                    baseElement.setColor(0, col, col / 2);
                    ///baseElement.setColor(0, 64 + (i * 100 / showLayers), 0);
                }
                world.add(baseElement);

                if (i < (showLayers - 20)) {
                    int skipLayers = ((Double) (-1 * a * i + b)).intValue();
                    i -= skipLayers;
                    offset -= (1 + skipLayers);
                } else {
                    offset -= 1;
                }
            }
        }


        if (layerNo < showMax) {
            baseElement = new PhotonBuildPlate(2560, 1440, 0, offset);
            baseElement.setColor(0, 255, 255);
            baseElement.scale(scaleFactor);
            world.add(baseElement);
        }

        baseElement = new ResinLayer(2560, 1440, fileLayer, PhotonLayer.SUPPORTED, layerOffset);
        baseElement.scale(scaleFactor);
        baseElement.setColor(0, 255, 0);
        world.add(baseElement);

        if (fileLayer.getIsLandsCount() > 0) {
            baseElement = new ResinLayer(2560, 1440, fileLayer, PhotonLayer.ISLAND, layerOffset);
            baseElement.scale(scaleFactor);
            baseElement.setColor(255, 0, 0);
            world.add(baseElement);
        }


        world.rotate(new RotationBaseMatrix(-1.8, 0, 0));

        repaint();
    }

    public void scale(double factor) {
        world.scale(factor);
        repaint();
    }

    public void rotate(RotationBaseMatrix rotationMatrix) {
        world.rotate(rotationMatrix);
        repaint();
    }

}
