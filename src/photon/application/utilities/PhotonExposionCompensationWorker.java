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
 * by bn on 16/07/2018.
 */
public class PhotonCalcWorker extends SwingWorker<Integer, String> implements IPhotonProgress {
    private MainForm mainForm;
    private File file;

    public PhotonCalcWorker(MainForm mainForm) {
        this.mainForm = mainForm;
        mainForm.marginInfo.setText("");
    }

    @Override
    protected void process(java.util.List<String> chunks) {
        for (String str : chunks) {
            mainForm.layerInfo.setText(str);
        }
    }

    @Override
    protected void done() {
        mainForm.openBtn.setEnabled(true);
        if (mainForm.photonFile!=null) {
            mainForm.showFileInformation();
        }
    }

    @Override
    protected Integer doInBackground() throws Exception {
        publish("Calculating layers...");
        try {
            mainForm.photonFile.setMargin(mainForm.margin);
            mainForm.photonFile.calculate(this);
            publish("Calculation Complete...");
        } catch (Exception e) {
            mainForm.marginInfo.setForeground(Color.red);
            mainForm.marginInfo.setText("Could not calculate the file.");
            return 0;
        }
        return 1;
    }

    @Override
    public void showInfo(String str) {
        publish(str);
    }
}
