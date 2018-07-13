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

package photon.application.base;

import photon.application.MainForm;
import photon.application.dialogs.InformationDialog;
import photon.application.dialogs.PreviewDialog;
import photon.application.dialogs.SaveDialog;
import photon.application.utilities.MainUtils;
import photon.application.utilities.PhotonLoadWorker;
import photon.file.PhotonFile;
import photon.file.parts.PhotonFileLayer;
import photon.file.parts.PhotonFilePreview;
import photon.file.ui.PhotonLayerImage;
import photon.file.ui.ScrollPosition;
import photon.file.ui.ScrollUtil;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

/**
 * by bn on 09/07/2018.
 */
public class BaseForm {
    protected MainForm me;

    protected String loadedPath;
    protected String loadedFileName;
    public PhotonFile photonFile;
    public int margin = 0;

    protected void openFile() {
        FileDialog d = new FileDialog(me.frame);
        d.setFilenameFilter(new FilenameFilter()
        {
            @Override
            public boolean accept(File file, String s)
            {
                // enter code to return TRUE or FALSE here
                return s.contains(".photon") || s.contains(".cbddlp");
            }
        });
        d.setVisible(true);
        String fileName = d.getDirectory() + d.getFile();
        if (fileName!=null && fileName.length()>0) {
            File file = new File(fileName);
//        int returnVal = me.fc.showOpenDialog(me.openBtn);
//        if (returnVal == JFileChooser.APPROVE_OPTION) {
//            File file = me.fc.getSelectedFile();
            if (MainUtils.isPhotonFile(file)) {
                me.saveBtn.setEnabled(false);
                me.informationBtn.setEnabled(false);
                me.previewLargeBtn.setEnabled(false);
                me.previewSmallBtn.setEnabled(false);
                try {
                    if (photonFile != null) {
                        photonFile.unLink();
                    }
                    setFileName(file);

                    PhotonLoadWorker loadWorker = new PhotonLoadWorker(me, file);
                    loadWorker.execute();

                    me.saveBtn.setEnabled(true);
                    me.informationBtn.setEnabled(true);
                    me.previewLargeBtn.setEnabled(true);
                    me.previewSmallBtn.setEnabled(true);
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            }
            me.frame.setTitle(file.getName());
        }

    }

    protected void showSave() {
        if (me.saveDialog == null) {
            me.saveDialog = new SaveDialog(me);
        }
        me.saveDialog.setInformation(photonFile, loadedPath, loadedFileName);
        me.saveDialog.setSize(new Dimension(500, 240));
        me.saveDialog.setLocationRelativeTo(me.frame);
        me.saveDialog.setVisible(true);
    }

    protected void showPrint() {
        if (me.informationDialog == null) {
            me.informationDialog = new InformationDialog(me.frame);
        }
        me.informationDialog.setInformation(photonFile);
        me.informationDialog.setSize(new Dimension(300, 360));
        me.informationDialog.setLocationRelativeTo(me.frame);
        me.informationDialog.setVisible(true);

    }

    protected void showPreview(boolean large) {
        PhotonFilePreview preview = large ? me.photonFile.getPreviewOne() : me.photonFile.getPreviewTwo();
        if (me.previewDialog == null) {
            me.previewDialog = new PreviewDialog(me.frame, preview);
        }
        me.previewDialog.setInformation(preview, large);
        me.previewDialog.setSize(new Dimension(24 + preview.getResolutionX(), 82 + preview.getResolutionY()));
        me.previewDialog.setLocationRelativeTo(me.frame);
        me.previewDialog.setVisible(true);
        me.previewDialog.update();

    }

    protected void showLayerInformation(int layer, PhotonFileLayer fileLayer) {
        me.layerNo.setForeground(fileLayer.getIsLandsCount() > 0 ? Color.red : Color.black);
        me.layerNo.setText("Layer " + layer + "/" + (me.photonFile.getLayerCount()-1));
        me.layerZ.setText(String.format("Z: %.4f mm", fileLayer.getLayerPositionZ()));
        me.layerExposure.setText(String.format("Exposure: %.1fs", fileLayer.getLayerExposure()));
        me.layerOfftime.setText(String.format("Off Time: %.1fs", fileLayer.getLayerOffTime()));
    }

    public void showFileInformation() {
        String information = loadedFileName + " (" + photonFile.getInformation() + ")";
        me.zoomSlider.setValue(0);
        ((PhotonLayerImage) me.layerImage).reScale(1, photonFile.getWidth(), photonFile.getHeight());
        if (photonFile.getLayerCount() > 0) {
            PhotonFileLayer fileLayer = photonFile.getLayer(0);
            if (fileLayer != null) {
                showLayerInformation(0, fileLayer);
                ((PhotonLayerImage) me.layerImage).drawLayer(true, fileLayer, margin);
                me.layerImage.repaint();

                ScrollUtil.scrollTo(me.imageScrollPane, ScrollPosition.HorizontalCenter);
                ScrollUtil.scrollTo(me.imageScrollPane, ScrollPosition.VerticalCenter);

                if (photonFile.getLayerCount() > 0) {
                    me.layerSlider.setEnabled(false);
                    me.layerSlider.setValue(0);
                    me.layerSlider.setMaximum(photonFile.getLayerCount() - 1);

                    me.layerSpinner.setEnabled(false);
                    me.layerSpinner.setValue(0);
                    SpinnerNumberModel spinnerNumberModel = (SpinnerNumberModel) me.layerSpinner.getModel();
                    spinnerNumberModel.setMinimum(0);
                    spinnerNumberModel.setMaximum(photonFile.getLayerCount() - 1);
                    me.layerSpinner.setEnabled(true);
                    me.layerSlider.setEnabled(true);
                    me.zoomSlider.setEnabled(true);
                } else {
                    me.layerSlider.setEnabled(false);
                    me.layerSpinner.setEnabled(false);
                    me.zoomSlider.setEnabled(false);
                }
            }
        }
        me.layerInfo.setForeground(photonFile.getIslandLayerCount() > 0 ? Color.red : Color.decode("#006600"));
        me.layerInfo.setText(photonFile.getLayerInformation());
        me.islandNextBtn.setEnabled(photonFile.getIslandLayerCount() > 0);
        me.islandPrevBtn.setEnabled(photonFile.getIslandLayerCount() > 0);

        me.marginInfo.setForeground(photonFile.getMarginLayers().size() > 0 ? Color.red : Color.decode("#006600"));
        me.marginInfo.setText(photonFile.getMarginInformation());
        me.marginNextBtn.setEnabled(photonFile.getMarginLayers().size() > 0);
        me.marginPrevBtn.setEnabled(photonFile.getMarginLayers().size() > 0);

        me.frame.setTitle(information);
    }


    public void getSystemInformation() {
        try {
            File file = new File("photon.properties");
            if (!file.exists() || !file.isFile()) {
                String userDir = System.getProperty("user.home");
                file = new File(userDir + File.separatorChar + "photon.properties");
            }
            if (file.exists() && file.isFile()) {
                    Properties prop = new Properties();
                    prop.load(new FileInputStream(file));
                    try {
                        me.margin = Integer.parseInt(prop.getProperty("margin"));
                        me.marginInfo.setText("Margin set to: " + me.margin);
                    } catch (Exception e) {
                        me.margin = 0;
                    }

                    float peel;
                    try {
                        peel = Float.parseFloat(prop.getProperty("peel"));
                    } catch (Exception ex) {
                        peel = 5.5f;
                    }
                    if (me.informationDialog == null) {
                        me.informationDialog = new InformationDialog(me.frame);
                    }
                    me.informationDialog.setPeel(peel);
            }
        } catch (IOException e) {
            me.marginInfo.setText(e.getMessage());
        }
    }

    public void setFileName(File file) {
        loadedPath = file.getParentFile().getAbsolutePath();
        loadedFileName = file.getName();
    }

    protected void gotoNextLayer(ArrayList<Integer> layers) {
        if (me.layerSpinner.isEnabled() && photonFile != null) {
            int currentLayer = (Integer) me.layerSpinner.getValue();
            Integer nextLayer = null;
            for (int i : layers) {
                if (i > currentLayer) {
                    nextLayer = i;
                    break;
                }
            }
            if (nextLayer == null) {
                // current layer is higher than all layers, select the first to allow a new cycle.
                me.layerSpinner.setValue(layers.get(0));
            } else {
                me.layerSpinner.setValue(nextLayer);
            }
        }
    }

    protected void gotoPrevLayer(ArrayList<Integer> layers) {
        if (me.layerSpinner.isEnabled() && photonFile != null) {
            int currentLayer = (Integer) me.layerSpinner.getValue();
            Integer nextLayer = null;
            for (int i : layers) {
                if (i < currentLayer) {
                    nextLayer = i;
                } else {
                    break;
                }
            }
            if (nextLayer == null) {
                // current layer is lower than all layers, select the last to allow a new cycle.
                me.layerSpinner.setValue(layers.get(layers.size()-1));
            } else {
                me.layerSpinner.setValue(nextLayer);
            }
        }
    }

    protected void changeLayer() {
        if (me.layerSpinner.isEnabled()) {
            int layer = 0;
            Object o = me.layerSpinner.getValue();
            if (o instanceof String) {
                layer = Integer.parseInt((String) o);
            } else if (o instanceof Integer) {
                layer = (Integer) o;
            }
            PhotonFileLayer fileLayer = photonFile.getLayer(layer);
            showLayerInformation(layer, fileLayer);
            ((PhotonLayerImage) me.layerImage).drawLayer(layer == 0, fileLayer, margin);
            me.layerImage.repaint();

            me.layerSlider.setEnabled(false);
            me.layerSlider.setValue(layer);
            me.layerSlider.setEnabled(true);
        }
    }
}
