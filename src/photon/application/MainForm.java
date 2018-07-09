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
import photon.application.dialogs.InformationDialog;
import photon.application.dialogs.PreviewDialog;
import photon.application.dialogs.SaveDialog;
import photon.application.utilities.MainUtils;
import photon.application.utilities.PhotonLoadWorker;
import photon.file.ui.PhotonLayerImage;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * by bn on 29/06/2018.
 */
public class MainForm extends BaseForm implements ActionListener, ItemListener {
    public JFrame frame;
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
    public JButton previewLargeBtn;
    public JButton previewSmallBtn;
    private JPanel infoPanel;
    private JLabel logoLabel;

    public final JFileChooser fc;
    public InformationDialog informationDialog;
    public PreviewDialog previewDialog;
    public SaveDialog saveDialog;

    public MainForm() {

        $$$setupUI$$$();

        me = this;

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

        fc = new JFileChooser();

        islandNextBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gotoNextLeayer(photonFile.getIslandLayers());
            }
        });

        marginNextBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gotoNextLeayer(photonFile.getMarginLayers());
            }
        });

        saveBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showSave();
            }
        });

        informationBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showPrint();
            }
        });

        previewLargeBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showPreview(true);
            }
        });

        previewSmallBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showPreview(false);
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
        // Place the name and menubar correctly on a macOS

        System.setProperty("apple.awt.application.name", "Photon File Check");
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Photon File Check");
        System.setProperty("apple.laf.useScreenMenuBar", "true");

        JFrame frame = new JFrame("Photon File Check");
        MainForm mainForm = new MainForm();
        JPanel panel = mainForm.mainPanel;
        mainForm.frame = frame;
        // MainUtils.makeMenuBar(frame, mainForm);
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
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayoutManager(4, 1, new Insets(5, 5, 5, 5), -1, -1));
        imageScrollPane = new JScrollPane();
        imageScrollPane.setHorizontalScrollBarPolicy(32);
        imageScrollPane.setVerticalScrollBarPolicy(22);
        mainPanel.add(imageScrollPane, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(600, 400), new Dimension(800, 600), new Dimension(2570, 1450), 0, false));
        layerImage.setBackground(new Color(-1));
        layerImage.setEnabled(true);
        imageScrollPane.setViewportView(layerImage);
        layerImage.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 6, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(panel1, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        layerNo = new JLabel();
        layerNo.setText("Layer 0/0");
        panel1.add(layerNo, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(150, -1), new Dimension(150, -1), new Dimension(150, -1), 1, false));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        layerZ = new JLabel();
        layerZ.setHorizontalAlignment(4);
        layerZ.setText("");
        panel1.add(layerZ, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(120, -1), new Dimension(120, -1), new Dimension(120, -1), 0, false));
        layerExposure = new JLabel();
        layerExposure.setHorizontalAlignment(4);
        layerExposure.setText("");
        panel1.add(layerExposure, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(150, -1), new Dimension(150, -1), new Dimension(150, -1), 0, false));
        layerOfftime = new JLabel();
        layerOfftime.setHorizontalAlignment(4);
        layerOfftime.setText("");
        panel1.add(layerOfftime, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(150, -1), new Dimension(150, -1), new Dimension(150, -1), 0, false));
        layerSpinner = new JSpinner();
        layerSpinner.setEnabled(false);
        panel1.add(layerSpinner, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(80, -1), new Dimension(80, -1), new Dimension(80, -1), 1, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(panel2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 1, new Insets(10, 0, 0, 0), -1, -1));
        panel3.setBackground(new Color(-1250068));
        panel3.setEnabled(false);
        panel2.add(panel3, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(70, 70), new Dimension(70, 70), new Dimension(70, 70), 0, false));
        logoLabel = new JLabel();
        logoLabel.setText("");
        panel3.add(logoLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        infoPanel = new JPanel();
        infoPanel.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel2.add(infoPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        infoPanel.add(panel4, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(-1, 20), new Dimension(-1, 20), new Dimension(-1, 20), 0, false));
        marginInfo = new JLabel();
        marginInfo.setText("");
        panel4.add(marginInfo, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        final Spacer spacer2 = new Spacer();
        panel4.add(spacer2, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        marginNextBtn = new JButton();
        marginNextBtn.setEnabled(false);
        marginNextBtn.setText(">>");
        panel4.add(marginNextBtn, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(50, 18), new Dimension(50, 18), new Dimension(50, 18), 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        infoPanel.add(panel5, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(-1, 30), new Dimension(-1, 30), new Dimension(-1, 30), 0, false));
        layerInfo = new JLabel();
        layerInfo.setText("");
        panel5.add(layerInfo, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        final Spacer spacer3 = new Spacer();
        panel5.add(spacer3, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        islandNextBtn = new JButton();
        islandNextBtn.setEnabled(false);
        islandNextBtn.setText(">>");
        panel5.add(islandNextBtn, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(50, 18), new Dimension(50, 18), new Dimension(50, 18), 0, false));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(1, 7, new Insets(0, 0, 0, 0), -1, -1));
        infoPanel.add(panel6, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(-1, 30), new Dimension(-1, 30), new Dimension(-1, 30), 0, false));
        openBtn = new JButton();
        openBtn.setText("Open File");
        panel6.add(openBtn, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(110, 20), new Dimension(110, 20), new Dimension(110, 20), 0, false));
        final Spacer spacer4 = new Spacer();
        panel6.add(spacer4, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        saveBtn = new JButton();
        saveBtn.setEnabled(false);
        saveBtn.setText("Save");
        panel6.add(saveBtn, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(110, 20), new Dimension(110, 20), new Dimension(110, 20), 0, false));
        informationBtn = new JButton();
        informationBtn.setEnabled(false);
        informationBtn.setText("Information");
        panel6.add(informationBtn, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(110, 20), new Dimension(110, 20), new Dimension(110, 20), 0, false));
        previewLargeBtn = new JButton();
        previewLargeBtn.setEnabled(false);
        previewLargeBtn.setText("Preview Large");
        panel6.add(previewLargeBtn, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(130, 20), new Dimension(130, 20), new Dimension(130, 20), 0, false));
        previewSmallBtn = new JButton();
        previewSmallBtn.setEnabled(false);
        previewSmallBtn.setText("Preview Small");
        panel6.add(previewSmallBtn, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(130, 20), new Dimension(130, 20), new Dimension(130, 20), 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() { return mainPanel; }
}
