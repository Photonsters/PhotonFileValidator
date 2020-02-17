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

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 *  by bn on 09/07/2018.
 */
public class PhotonLoadWorker extends SwingWorker<Integer, String> implements IPhotonProgress {
    private MainForm mainForm;
    private File file;

    public PhotonLoadWorker(MainForm mainForm, File file) {
        this.mainForm = mainForm;
        this.file = file;

        mainForm.layerInfo.setForeground(Color.decode("#000099"));
        mainForm.marginInfo.setText("");

        mainForm.openBtn.setEnabled(false);
        mainForm.saveBtn.setEnabled(false);
        mainForm.fixBtn.setEnabled(false);
        mainForm.islandNextBtn.setEnabled(false);
        mainForm.islandPrevBtn.setEnabled(false);
        mainForm.marginNextBtn.setEnabled(false);
        mainForm.marginPrevBtn.setEnabled(false);
        mainForm.layerSpinner.setEnabled(false);
        mainForm.zoomSlider.setEnabled(false);
        mainForm.layerSlider.setEnabled(false);
        mainForm.tabPreviewLarge.setEnabled(false);
        mainForm.tabPreviewSmall.setEnabled(false);
        mainForm.playButton.setEnabled(false);
        mainForm.convertBtn.setEnabled(false);
    }

    @Override
    protected void process(java.util.List<String> chunks) {
        for (String str : chunks) {
            mainForm.layerInfo.setText(str);
        }
    }

    @Override
    protected void done() {
        // mainForm.openBtn.setEnabled(true);
        if (mainForm.photonFile!=null) {
            mainForm.openBtn.setEnabled(true);
            mainForm.saveBtn.setEnabled(true);
            mainForm.informationBtn.setEnabled(true);
            mainForm.tabPreviewLarge.setEnabled(true);
            mainForm.tabPreviewSmall.setEnabled(true);
            mainForm.showFileInformation();
            mainForm.calc();
            mainForm.playButton.setEnabled(true);
            if (mainForm.photonFile.getVersion()>1) {
                mainForm.convertBtn.setEnabled(true);
            }
        }
    }

    @Override
    protected Integer doInBackground() throws Exception {
        publish("Loading file...");
        try {
            mainForm.photonFile = new PhotonFile();
            mainForm.photonFile.setMargin(mainForm.margin);
            mainForm.photonFile.readFile(file, this);
            publish("Complete...");
        } catch (Exception e) {
            mainForm.photonFile = null;
            mainForm.marginInfo.setForeground(Color.red);
            mainForm.marginInfo.setText("Could not read the file, file is corrupted or in an unsupported format.");
            return 0;
        }
        return 1;
    }

    @Override
    public void showInfo(String str) {
        publish(str);
    }
}
