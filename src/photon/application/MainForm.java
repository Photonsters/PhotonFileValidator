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

package photon.application;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import photon.application.base.BaseForm;
import photon.application.base.BaseFrame;
import photon.application.dialogs.*;
import photon.application.render.OnionMousePanel;
import photon.application.render.OnionPanel;
import photon.application.utilities.MainUtils;
import photon.application.utilities.PhotonLoadWorker;
import photon.file.ui.PhotonLayerImage;
import photon.file.ui.PhotonPreviewImage;
import photon.file.ui.ScrollPosition;
import photon.file.ui.ScrollUtil;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

/**
 * by bn on 29/06/2018.
 */
public class MainForm extends BaseForm implements ActionListener, ItemListener {
    private JPanel mainPanel;
    public JButton openBtn;
    public JPanel layerImage;
    public JScrollPane imageScrollPane;
    public JSpinner layerSpinner;
    public JLabel layerInfo;
    public JLabel layerNo;
    public JLabel layerZ;
    public JLabel layerExposure;
    public JLabel layerOfftime;
    public JButton islandNextBtn;
    public JLabel marginInfo;
    public JButton marginNextBtn;
    public JButton saveBtn;
    public JButton informationBtn;
    private JPanel infoPanel;
    private JLabel logoLabel;
    public JSlider zoomSlider;
    public JSlider layerSlider;
    public JButton marginPrevBtn;
    public JButton islandPrevBtn;
    public JButton fixBtn;
    public JTabbedPane tabbedPane;
    public JPanel previewSmallPanel;
    public JPanel tabPreviewSmall;
    public JPanel tabPreviewLarge;
    public JPanel previewLargePanel;
    public JScrollPane previewLargeScrollPane;
    public JScrollPane previewSmallScrollPane;
    private JPanel tab3D;
    private JPanel layer3D;
    public JPanel render3D;

    public JButton playButton;
    public JButton convertBtn;
    public boolean playing;

    public InformationDialog informationDialog;
    public PreviewDialog previewDialog;
    public SaveDialog saveDialog;
    public FixDialog fixDialog;
    public EditDialog editDialog;
    public AntiAliaseDialog antiAliaseDialog;
    public PlayDialog playDialog;
    public ConvertDialog convertDialog;

    public MainForm() {
        me = this;

        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                play();
            }
        });

        openBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openFile();
            }
        });

        layerSpinner.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                changeLayer();
            }
        });

        islandNextBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gotoNextLayer(photonFile.getIslandLayers());
            }
        });

        islandPrevBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gotoPrevLayer(photonFile.getIslandLayers());
            }
        });

        marginNextBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gotoNextLayer(photonFile.getMarginLayers());
            }
        });

        marginPrevBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gotoPrevLayer(photonFile.getMarginLayers());
            }
        });

        saveBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showSave();
            }
        });

        fixBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showFix();
            }
        });

        informationBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showPrint();
            }
        });

        convertBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showConvert();
            }
        });

        zoomSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int newZoom = ((JSlider) e.getSource()).getValue();
                if (newZoom != zoom) {
                    zoom = newZoom;
                    if (zoom == 0) {
                        ((PhotonLayerImage) layerImage).reScale(1, photonFile.getWidth(), photonFile.getHeight());
                    } else if (zoom > 0) {
                        ((PhotonLayerImage) layerImage).reScale(1f + (zoom / 2f), photonFile.getWidth(), photonFile.getHeight());
                    } else {
                        ((PhotonLayerImage) layerImage).reScale(1f + (zoom / 4f), photonFile.getWidth(), photonFile.getHeight());
                    }
                    changeLayer();
                    ScrollUtil.scrollTo(me.imageScrollPane, ScrollPosition.HorizontalCenter);
                    ScrollUtil.scrollTo(me.imageScrollPane, ScrollPosition.VerticalCenter);
                }
            }
        });

        layerSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int pos = ((JSlider) e.getSource()).getValue();
                if (pos != (int) layerSpinner.getValue()) {
                    layerSpinner.setValue(pos);
                }
            }
        });

        layerImage.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (photonFile != null) {
                    if (e.isShiftDown()) {
                        showAA(e.getX(), e.getY());
                    } else if (e.isAltDown()) {
                        removeAllIslands();
                    } else {
                        showEdit(e.getX(), e.getY());
                    }
                }
            }
        });

        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                changeLayer();
            }
        });

        tabbedPane.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                changeLayer();
            }
        });

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (photonFile != null) {
            switch (e.getActionCommand()) {
                case MainUtils.PRINT_MENU:
                    showPrint();
                    break;

                case MainUtils.SMALL_MENU:
                    showPreview(false);
                    break;

                case MainUtils.LARGE_MENU:
                    showPreview(true);
                    break;
            }
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {

    }

    public static void main(String[] args) {
        BaseFrame frame = new BaseFrame("Photon File Validator 2.1");
        MainUtils.setIcon(frame);
        MainForm mainForm = new MainForm();
        JPanel panel = mainForm.mainPanel;
        frame.setMainForm(mainForm);
        frame.setContentPane(panel);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        MainUtils.setWindowPosition(frame, 1);
        frame.setVisible(true);

        mainForm.getSystemInformation();

        ImageIcon icon = new ImageIcon(MainUtils.getLogo());
        mainForm.logoLabel.setIcon(icon);

        if (args != null) {
            for (String arg : args) {
                File file = new File(arg);
                if (MainUtils.isPhotonFile(file)) {
                    mainForm.setFileName(file);
                    PhotonLoadWorker loadWorker = new PhotonLoadWorker(mainForm, file);
                    loadWorker.execute();
                    break;
                }
            }
        }
    }


    private void createUIComponents() {
        layerImage = new PhotonLayerImage(2560, 1440);
        render3D = new OnionPanel();
        layer3D = new OnionMousePanel((OnionPanel) render3D);
        previewSmallPanel = new PhotonPreviewImage(2000, 2000);
        previewLargePanel = new PhotonPreviewImage(500, 500);

    }

}
