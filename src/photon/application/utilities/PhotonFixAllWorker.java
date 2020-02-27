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
import photon.application.dialogs.FixDialog;
import photon.file.PhotonFile;
import photon.file.parts.IPhotonProgress;

import javax.swing.*;

/**
 * by bn on 14/07/2018.
 */
public class PhotonFixAllWorker extends SwingWorker<Integer, String> implements IPhotonProgress {
    private FixDialog fixDialog;
    private PhotonFile photonFile;
    private MainForm mainForm;

    public PhotonFixAllWorker(FixDialog fixDialog, PhotonFile photonFile, MainForm mainForm) {
        this.fixDialog = fixDialog;
        this.photonFile = photonFile;
        this.mainForm = mainForm;
    }

    @Override
    protected void process(java.util.List<String> chunks) {
        for (String str : chunks) {
            fixDialog.appendInformation(str);
        }
    }

    @Override
    protected void done() {
        fixDialog.enableButtons();
        fixDialog.appendInformation("<p>Done.</p>");
        mainForm.showFileInformation();
    }

    @Override
    public void showInfo(String str) {
        publish(str);
    }

    @Override
    protected Integer doInBackground() throws Exception {
        try {
            photonFile.fixAll(this);
        } catch (Exception e) {
            publish("<br><p>" + e.getMessage()+ "</p>");
            return 0;
        }
        return 1;
    }
}
