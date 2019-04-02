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

package photon.application.utilities;

import photon.application.MainForm;
import photon.file.PhotonFile;
import photon.file.parts.IPhotonProgress;
import photon.file.parts.PhotonFileLayer;
import photon.file.ui.PhotonLayerImage;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class PhotonPlayWorker extends SwingWorker<Integer, String> implements IPhotonProgress {
    private MainForm mainForm;
    private int layerNo = 0;
    private int aaNo = 0;
    private PhotonFileLayer photonFileLayer = null;
    private int miliseconds;

    Integer[] speeds = new Integer[] { 1000, 500, 400, 300, 200, 100, 60, 10 };

    public PhotonPlayWorker(MainForm mainForm, int speedSelection) {
        this.mainForm = mainForm;
        this.miliseconds = speeds[speedSelection];

        mainForm.playButton.setText("Stop");
        mainForm.playing = true;

    }

    @Override
    protected void process(java.util.List<String> chunks) {
        if (photonFileLayer != null) {
            mainForm.playLayerInformation(layerNo, aaNo, photonFileLayer);
            ((PhotonLayerImage) mainForm.layerImage).drawLayer(photonFileLayer, mainForm.margin);
            mainForm.layerImage.repaint();
        }
    }

    @Override
    protected void done() {
        mainForm.playButton.setEnabled(true);
        mainForm.playButton.setText("Play");
        mainForm.playing = false;
        if (mainForm.photonFile != null) {
            mainForm.viewLayerInfo();;
        }
    }

    @Override
    protected Integer doInBackground() throws Exception {
        try {
            PhotonFile photonFile = mainForm.photonFile;

            int totalLayers = photonFile.getLayerCount() * photonFile.getAALevels();

            for (int i = 0; i < totalLayers; i++) {

                layerNo = i / photonFile.getAALevels();
                aaNo = i % photonFile.getAALevels();

                if (aaNo == 0) {
                    photonFileLayer = photonFile.getLayer(layerNo);
                } else {
                    photonFileLayer = photonFile.getLayer(layerNo).getAntiAlias(aaNo - 1);
                }

                publish("Show info");

                try {
                    Thread.sleep(miliseconds);
                } catch (Exception e) {
                    // ignore...
                }
                if (!mainForm.playing) {
                    break;
                }
            }

        } catch (Exception e) {
            return 0;
        }
        return 1;
    }

    @Override
    public void showInfo(String str) {
        publish(str);
    }
}
