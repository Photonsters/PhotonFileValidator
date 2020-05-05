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

package photon.application.dialogs;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import photon.application.MainForm;
import photon.application.utilities.PhotonFixAllWorker;
import photon.application.utilities.PhotonFixWorker;
import photon.application.utilities.PhotonRemoveAllIslandsWorker;
import photon.file.SlicedFile;
import photon.file.parts.PhotonFileLayer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FixDialog extends JDialog {
    private JPanel contentPane;
    public JButton buttonOK;
    private JScrollPane textPane;
    public JButton startButton;
    public JButton fixAllButton;
    private JLabel progressInfo;
    private JButton removeUnsupportedButton;

    private FixDialog me;
    private MainForm mainForm;
    private SlicedFile photonFile;
    private StringBuilder information;

    public FixDialog(final MainForm mainForm) {
        super(mainForm.frame);
        this.mainForm = mainForm;
        this.me = this;

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                startButton.setEnabled(false);
                fixAllButton.setEnabled(false);
                removeUnsupportedButton.setEnabled(false);
                buttonOK.setEnabled(false);
                PhotonFixWorker photonFixWorker = new PhotonFixWorker(me, mainForm.photonFile, mainForm);
                photonFixWorker.execute();
            }
        });

        fixAllButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                startButton.setEnabled(false);
                fixAllButton.setEnabled(false);
                removeUnsupportedButton.setEnabled(false);
                buttonOK.setEnabled(false);
                PhotonFixAllWorker photonFixWorker = new PhotonFixAllWorker(me, mainForm.photonFile, mainForm);
                photonFixWorker.execute();
            }
        });

        removeUnsupportedButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                startButton.setEnabled(false);
                fixAllButton.setEnabled(false);
                removeUnsupportedButton.setEnabled(false);
                buttonOK.setEnabled(false);
                PhotonRemoveAllIslandsWorker photonFixWorker = new PhotonRemoveAllIslandsWorker(me, mainForm.photonFile, mainForm);
                photonFixWorker.execute();
            }
        });

    }

    public void enableButtons() {
        buttonOK.setEnabled(true);
        startButton.setEnabled(true);
        fixAllButton.setEnabled(true);
        removeUnsupportedButton.setEnabled(true);
    }

    private void onOK() {
// add your code here
        dispose();
    }

    public void setInformation(SlicedFile photonFile) {
        information = null;
        this.photonFile = photonFile;
        setTitle("Fix pixels errors");

        StringBuilder builder = new StringBuilder("<h4>Information:</h4>");
        builder.append("<p>Layers with islands: ").append(photonFile.getIslandLayerCount()).append("</p><br>");

        for (int layerNo : photonFile.getIslandLayers()) {
            PhotonFileLayer layer = photonFile.getLayer(layerNo);
            builder.append(String.format("<p>Layer %6d have %9d island pixels</p>", layerNo, layer.getIsLandsCount()));
        }

        showProgressHtml(builder.toString());

    }

    public void appendInformation(String str) {
        if (information == null) {
            information = new StringBuilder();
            information.append("<h4>Fixing progress:</h4>");
        }
        information.append(str);
        showProgressHtml(information.toString());
    }

    public void showProgress(String progress) {
        progressInfo.setText("<html>" + progress.replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\n", "<br/>") + "</html>");
    }

    public void showProgressHtml(String progress) {
        progressInfo.setText("<html>" + progress + "</html>");
    }

}
