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

import photon.application.MainForm;
import photon.file.SlicedFile;
import photon.file.parts.PhotonFileLayer;
import photon.file.ui.PhotonAaPanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;

public class AntiAliaseDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JLabel infoText;
    private JPanel aaArea;
    private JSlider aaLevel;
    private JLabel aaLevelName;


    private MainForm mainForm;
    private SlicedFile photonFile;
    private PhotonFileLayer fileLayer;
    private int layerX;
    private int layerY;


    public AntiAliaseDialog(MainForm mainForm) {
        super(mainForm.frame);
        this.mainForm = mainForm;

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        aaLevel.setMinimum(0);
        aaLevel.setMaximum(2);
        aaLevel.setMinorTickSpacing(1);
        aaLevel.setMajorTickSpacing(1);
        aaLevel.setSnapToTicks(true);


        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {onOK();}
        });

        aaLevel.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int aaSelect = ((JSlider) e.getSource()).getValue();
                if (aaSelect == 0) {
                    aaLevelName.setText("Combined AA");
                } else {
                    aaLevelName.setText("Layer AA " + aaSelect);
                }
                ((PhotonAaPanel) aaArea).drawLayer(layerX, layerY, fileLayer, aaLevel.getValue());
                aaArea.repaint();
            }
        });

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onOK();
            }
        });


    }

    private void onOK() {
        // add your code here
        dispose();
    }


    private void createUIComponents() {
        aaArea = new PhotonAaPanel(780, 480);
    }

    public void setInformation(SlicedFile photonFile, int layerNo, int mouseX, int mouseY) {
        this.photonFile = photonFile;
        this.fileLayer = photonFile.getLayer(layerNo);

        boolean mirrored = photonFile.getPhotonFileHeader().isMirrored();

        int indexX = (mouseX < 38) ? 1 : mouseX - 38;
        int indexY = (mouseY < 23) ? 1 : mouseY - 23;

        if (indexX + 74 >= photonFile.getWidth()) {
            indexX = photonFile.getWidth() - 74;
        }
        if (indexY + 44 >= photonFile.getHeight()) {
            indexY = photonFile.getHeight() - 44;
        }

        if (mirrored) {
            indexY = photonFile.getHeight() - indexY - 44;
        }

        ((PhotonAaPanel) aaArea).setMirrored(mirrored);

        layerX = indexX - 1;
        layerY = indexY - 1;

        aaLevel.setMaximum(mainForm.photonFile.getPhotonFileHeader().getAALevels());

        if (aaLevel.getValue() == 0) {
            aaLevelName.setText("Combined AA");
            ((PhotonAaPanel) aaArea).drawLayer(layerX, layerY, fileLayer, aaLevel.getValue());
            aaArea.repaint();
        } else {
            aaLevel.setValue(0);
        }
        aaLevel.requestFocus();
        aaLevel.grabFocus();
    }


}
