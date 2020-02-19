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

import photon.application.render.OnionPanel;
import photon.application.MainForm;
import photon.application.dialogs.*;
import photon.application.render.storage.RotationBaseMatrix;
import photon.application.utilities.MainUtils;
import photon.application.utilities.PhotonCalcWorker;
import photon.application.utilities.PhotonLoadWorker;
import photon.application.utilities.PhotonPlayWorker;
import photon.file.PhotonFile;
import photon.file.parts.PhotonFileLayer;
import photon.file.parts.PhotonFilePreview;
import photon.file.parts.PhotonLayer;
import photon.file.ui.PhotonLayerImage;
import photon.file.ui.PhotonPreviewImage;
import photon.file.ui.ScrollPosition;
import photon.file.ui.ScrollUtil;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.KeyEvent;
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
    public JFrame frame;
    protected MainForm me;

    protected String loadedPath;
    protected String loadedFileName;
    public PhotonFile photonFile;
    public int margin = 0;
    protected int zoom = 0;

    private PhotonCalcWorker calcWorker;

    protected void openFile() {
        FileDialog d = new FileDialog(me.frame);
        d.setFilenameFilter(new FilenameFilter() {
            @Override
            public boolean accept(File file, String s) {
                return s.contains(".photon") || s.contains(".cbddlp") || s.contains(".photons");
            }
        });
        d.setVisible(true);
        String fileName = d.getDirectory() + d.getFile();
        if (fileName != null && fileName.length() > 0) {
            File file = new File(fileName);
            if (MainUtils.isPhotonFile(file)) {
                if (calcWorker!=null) {
                    if (!calcWorker.isDone()) {
                        calcWorker.cancel(true);
                    }
                    calcWorker = null;
                }

                me.saveBtn.setEnabled(false);
                me.informationBtn.setEnabled(false);
                me.tabPreviewLarge.setEnabled(false);
                me.tabPreviewSmall.setEnabled(false);
                try {
                    if (photonFile != null) {
                        photonFile.unLink();
                    }
                    setFileName(file);

                    PhotonLoadWorker loadWorker = new PhotonLoadWorker(me, file);
                    loadWorker.execute();

                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
                me.frame.setTitle(file.getName());
            }
        }

    }

    protected void showSave() {
        if (me.saveDialog == null) {
            me.saveDialog = new SaveDialog(me);
        }
        me.saveDialog.setInformation(photonFile, loadedPath, loadedFileName);
        me.saveDialog.setSize(new Dimension(800, 360));
        me.saveDialog.setLocationRelativeTo(me.frame);
        me.saveDialog.setVisible(true);
    }

    protected void showFix() {
        if (me.fixDialog == null) {
            me.fixDialog = new FixDialog(me);
        }
        me.fixDialog.setInformation(photonFile);
        me.fixDialog.setSize(new Dimension(500, 240));
        me.fixDialog.pack();
        me.fixDialog.setLocationRelativeTo(me.frame);
        me.fixDialog.setVisible(true);
    }


    protected void showPrint() {
        if (me.informationDialog == null) {
            me.informationDialog = new InformationDialog(me.frame);
        }
        me.informationDialog.setInformation(photonFile);
        me.informationDialog.setSize(new Dimension(650, 320));
        me.informationDialog.setLocationRelativeTo(me.frame);
        me.informationDialog.setVisible(true);
        me.showFileInformation();

    }

    protected void showConvert() {
        if (me.convertDialog == null) {
            me.convertDialog = new ConvertDialog(me);
        }
        me.convertDialog.setInformation(photonFile);
        me.convertDialog.setSize(new Dimension(650, 430));
        me.convertDialog.setLocationRelativeTo(me.frame);
        me.convertDialog.setVisible(true);

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

    public void showLayerInformation(int layer, PhotonFileLayer fileLayer) {
        me.layerNo.setForeground(fileLayer.getIsLandsCount() > 0 ? Color.red : Color.black);
        me.layerNo.setText("Layer " + layer + "/" + (me.photonFile.getLayerCount() - 1) + (me.photonFile.hasAA()?" AA("+me.photonFile.getPhotonFileHeader().getAALevels()+")" : ""));
        me.layerZ.setText(String.format("Z: %.4f mm", fileLayer.getLayerPositionZ()));
        me.layerExposure.setText(String.format("Exposure: %.1fs", fileLayer.getLayerExposure()));
        me.layerOfftime.setText(String.format("Off Time: %.1fs", fileLayer.getLayerOffTime()));
    }

    public void playLayerInformation(int layer, int aaLevel, PhotonFileLayer fileLayer) {
        me.layerNo.setForeground(fileLayer.getIsLandsCount() > 0 ? Color.red : Color.black);
        me.layerNo.setText("Layer " + layer + "/" + (me.photonFile.getLayerCount() - 1) + (me.photonFile.hasAA()?" AA("+aaLevel+"/"+me.photonFile.getPhotonFileHeader().getAALevels()+")" : ""));
        me.layerZ.setText(String.format("Z: %.4f mm", fileLayer.getLayerPositionZ()));
        me.layerExposure.setText(String.format("Exposure: %.1fs", fileLayer.getLayerExposure()));
        me.layerOfftime.setText(String.format("Off Time: %.1fs", fileLayer.getLayerOffTime()));
    }

    public void showFileInformation() {
        if (loadedFileName != null) {
            String information = loadedFileName + " (" + photonFile.getInformation() + ")";
            me.zoomSlider.setValue(0);
            ((PhotonLayerImage) me.layerImage).reScale(1, photonFile.getWidth(), photonFile.getHeight());
            if (photonFile.getLayerCount() > 0) {
                PhotonFileLayer fileLayer = photonFile.getLayer(0);
                if (fileLayer != null) {
                    Border border = BorderFactory.createEmptyBorder(0, 0, 0, 0);

                    showLayerInformation(0, fileLayer);
                    if (me.tabbedPane.getSelectedIndex()==0) {
                        ((PhotonLayerImage) me.layerImage).drawLayer(fileLayer, margin);
                        me.layerImage.repaint();
                    }
                    me.imageScrollPane.setBorder(border);

                    if (me.tabbedPane.getSelectedIndex()==1) {
                        ((OnionPanel) me.render3D).drawLayer(0, fileLayer, photonFile, me.render3D.getHeight());
                    }

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


                    PhotonFilePreview preview = me.photonFile.getPreviewOne();
                    ((PhotonPreviewImage) me.previewLargePanel).reInit(preview.getResolutionX(), preview.getResolutionY());
                    ((PhotonPreviewImage) me.previewLargePanel).drawImage(preview);
                    me.tabbedPane.setEnabledAt(2, true);
                    me.previewLargeScrollPane.setBorder(border);
                    me.previewLargeScrollPane.setBackground(Color.decode("#ececec"));

                    ScrollUtil.scrollTo(me.previewLargeScrollPane, ScrollPosition.HorizontalCenter);
                    ScrollUtil.scrollTo(me.previewLargeScrollPane, ScrollPosition.VerticalCenter);

                    preview = me.photonFile.getPreviewTwo();
                    ((PhotonPreviewImage) me.previewSmallPanel).reInit(preview.getResolutionX(), preview.getResolutionY());
                    ((PhotonPreviewImage) me.previewSmallPanel).drawImage(preview);
                    me.tabbedPane.setEnabledAt(3, true);
                    me.previewSmallScrollPane.setBorder(border);
                    me.previewSmallScrollPane.setBackground(Color.decode("#ececec"));

                    me.layerSpinner.requestFocus();
                }
            }

            showMarginAndIslandInformation();

            me.frame.setTitle(information);
        }
    }

    public void showMarginAndIslandInformation() {
        boolean hasIslands = photonFile.getIslandLayerCount() > 0;
        me.layerInfo.setForeground(hasIslands ? Color.red : Color.decode("#006600"));
        me.layerInfo.setText(photonFile.getLayerInformation());
        me.islandNextBtn.setEnabled(hasIslands);
        me.islandPrevBtn.setEnabled(hasIslands);
        me.fixBtn.setEnabled(hasIslands);

        boolean hasMargins = photonFile.getMarginLayers().size() > 0;
        me.marginInfo.setForeground(hasMargins ? Color.red : Color.decode("#006600"));
        me.marginInfo.setText(photonFile.getMarginInformation());
        me.marginNextBtn.setEnabled(hasMargins);
        me.marginPrevBtn.setEnabled(hasMargins);
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
                me.layerSpinner.setValue(layers.get(layers.size() - 1));
            } else {
                me.layerSpinner.setValue(nextLayer);
            }
        }
    }

    public void viewLayerInfo() {
        if (me.layerSpinner.isEnabled()) {
            int layer = getLayer();
            PhotonFileLayer fileLayer = photonFile.getLayer(layer);
            showLayerInformation(layer, fileLayer);
        }
    }

    public void changeLayer() {
        if (me.layerSpinner.isEnabled()) {
            int layer = getLayer();
            PhotonFileLayer fileLayer = photonFile.getLayer(layer);
            showLayerInformation(layer, fileLayer);

            if (me.tabbedPane.getSelectedIndex()==0) {
                ((PhotonLayerImage) me.layerImage).drawLayer(fileLayer, margin);
                me.layerImage.repaint();
            }

            if (me.tabbedPane.getSelectedIndex()==1) {
                ((OnionPanel) me.render3D).drawLayer(layer, fileLayer, photonFile, me.render3D.getHeight());
            }

            me.layerSlider.setEnabled(false);
            me.layerSlider.setValue(layer);
            me.layerSlider.setEnabled(true);
        }
    }

    private int getLayer() {
        int layer = 0;
        Object o = me.layerSpinner.getValue();
        if (o instanceof String) {
            layer = Integer.parseInt((String) o);
        } else if (o instanceof Integer) {
            layer = (Integer) o;
        }
        return layer;
    }

    public void calc() {
        if (me.photonFile != null) {
            ((PhotonLayerImage) me.layerImage).setMirrored(me.photonFile.getPhotonFileHeader().isMirrored());
            calcWorker = new PhotonCalcWorker(me);
            calcWorker.execute();
        }
    }

    protected void showEdit(int x, int y) {
        if (me.editDialog == null) {
            me.editDialog = new EditDialog(me);
        }

        float zoomFactor = 1f;
        if (zoom > 0) {
            zoomFactor = 1f + (zoom / 2f);
        } else if (zoom < 0){
            zoomFactor = 1f + (zoom / 4f);
        }

        me.editDialog.setInformation(photonFile, getLayer(), (int) (x / zoomFactor), (int) (y / zoomFactor));
        me.editDialog.setSize(new Dimension(800, 600));
        me.editDialog.setLocationRelativeTo(me.frame);
        me.editDialog.setVisible(true);

    }

    protected void showAA(int x, int y) {
        if (me.antiAliaseDialog == null) {
            me.antiAliaseDialog = new AntiAliaseDialog(me);
        }

        float zoomFactor = 1f;
        if (zoom > 0) {
            zoomFactor = 1f + (zoom / 2f);
        } else if (zoom < 0){
            zoomFactor = 1f + (zoom / 4f);
        }

        me.antiAliaseDialog.setInformation(photonFile, getLayer(), (int) (x / zoomFactor), (int) (y / zoomFactor));
        me.antiAliaseDialog.setSize(new Dimension(800, 600));
        me.antiAliaseDialog.setLocationRelativeTo(me.frame);
        me.antiAliaseDialog.setVisible(true);

    }

    public void removeAllIslands() {
        int layerNo = getLayer();
        PhotonFileLayer fileLayer = photonFile.getLayer(layerNo);
        PhotonLayer layer = fileLayer.getLayer();

        if (layer.removeIslands() > 0) {
            try {
                fileLayer.saveLayer(layer);
                photonFile.calculate(layerNo);

                if (layerNo < photonFile.getLayerCount() - 1) {
                    photonFile.calculate(layerNo + 1);
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            changeLayer();
            showMarginAndIslandInformation();
        }
    }

    public void handleKeyEvent(KeyEvent key) {
        if (me.tabbedPane.getSelectedIndex()==0) {
            switch (key.getKeyCode()) {
                case KeyEvent.VK_DOWN:
                    ScrollUtil.scroll(me.imageScrollPane, ScrollPosition.Bottom);
                    break;
                case KeyEvent.VK_UP:
                    ScrollUtil.scroll(me.imageScrollPane, ScrollPosition.Top);
                    break;
                case KeyEvent.VK_LEFT:
                    ScrollUtil.scroll(me.imageScrollPane, ScrollPosition.Left);
                    break;
                case KeyEvent.VK_RIGHT:
                    ScrollUtil.scroll(me.imageScrollPane, ScrollPosition.Right);
                    break;
            }
        } else if (me.tabbedPane.getSelectedIndex()==1) {
            RotationBaseMatrix rotationMatrix = null;
            switch (key.getKeyCode()) {
                case KeyEvent.VK_DOWN:
                    rotationMatrix = new RotationBaseMatrix(-0.1, 0, 0);
                    break;
                case KeyEvent.VK_UP:
                    rotationMatrix = new RotationBaseMatrix(0.1, 0, 0);
                    break;
                case KeyEvent.VK_LEFT:
                    rotationMatrix = new RotationBaseMatrix(0, -0.1, 0);
                    break;
                case KeyEvent.VK_RIGHT:
                    rotationMatrix = new RotationBaseMatrix(0, 0.1, 0);
                    break;
            }
            if (rotationMatrix!=null) {
                ((OnionPanel) me.render3D).rotate(rotationMatrix);
            }
        }
        // key.consume();
    }

    public void play() {
        if (me.playing) {
            me.playButton.setEnabled(false);
            me.playing = false;
        } else {
            if (me.playDialog == null) {
                me.playDialog = new PlayDialog(me);
            }
            me.playDialog.setSize(new Dimension(400, 150));
            me.playDialog.setLocationRelativeTo(me.frame);
            me.playDialog.setVisible(true);
        }
    }

}
