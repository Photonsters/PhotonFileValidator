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

import images.BinaryImage;
import photon.application.MainForm;
import photon.application.dialogs.ExposureCompensationDialog;
import photon.file.PhotonFile;
import photon.file.parts.IPhotonProgress;
import photon.file.parts.IPhotonProgressBar;
import photon.file.parts.PhotonFileLayer;

import javax.swing.*;
import java.util.stream.IntStream;

/**
 * by bn on 16/07/2018.
 */
public class PhotonExposionCompensationWorker extends SwingWorker<Integer, Integer> {
    private MainForm mainForm;
    private IPhotonProgressBar progress;
    private PhotonFile file;
    private ExposureCompensationDialog dlg;
    private float percentageDone;

    public PhotonExposionCompensationWorker(ExposureCompensationDialog dlg, PhotonFile file, MainForm mainForm) {
        this.mainForm = mainForm;
        this.progress = dlg;
        this.file = file;
        this.dlg = dlg;
    }

    @Override
    protected void process(java.util.List<Integer> chunks) {
        for (Integer p : chunks) {
            progress.onProgress(p);
        }
    }

    @Override
    protected void done() {
        publish(100);
        dlg.btnOk.setEnabled(true);
    }

    private void partialDone(float percentage) {
        percentageDone += percentage;
        publish(Math.round(percentageDone));
    }
    @Override
    protected Integer doInBackground() throws Exception {
        publish(0);
        try {
            int numLayers = file.getLayerCount();
            final float progressStep = 100.0f / (numLayers + 10);
            percentageDone = 0f;
            boolean square = dlg.chkboxUseSquare.getModel().isSelected();

            long start = System.currentTimeMillis();
            IntStream layerNumbers = IntStream.range(0, numLayers);
            layerNumbers.parallel().forEach(layerNo -> {
                PhotonFileLayer layer = file.getLayer(layerNo);
                BinaryImage image = BinaryImage.from(layer);
                int size = layerNo < file.getPhotonFileHeader().getBottomLayers() ? dlg.getBottomCompenstation() : dlg.getDefaultCompensation();
                image.erode(size, square);
                image.to(layer);
                partialDone(progressStep);
            });

            System.out.println(String.format("Compensation done in %s ms", System.currentTimeMillis() - start));

            file.calculate(dlg);
            mainForm.changeLayer();
            mainForm.showMarginAndIslandInformation();

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }

}
