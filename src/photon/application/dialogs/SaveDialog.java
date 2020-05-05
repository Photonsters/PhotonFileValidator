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
import photon.file.PhotonFile;
import photon.file.parts.IFileHeader;
import photon.file.parts.photon.PhotonFileHeader;
import photon.file.parts.PhotonFilePrintParameters;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SaveDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField textName;
    private JTextField textExposure;
    private JTextField textBottomLayers;
    private JTextField textBottomExposure;
    private JTextField textOffTime;
    private JCheckBox fixZcheck;
    private JCheckBox version2FormatCheckBox;
    private JTextField bottomLiftDistance;
    private JTextField bottomLiftSpeed;
    private JTextField liftingDistance;
    private JTextField liftingSpeed;
    private JTextField retractSpeed;
    private JTextField bottomLightOffDelay;
    private JTextField lightOffDelay;
    private JLabel botomLiftDistanceLabel;
    private JLabel bottomLiftSpeedLabel;
    private JLabel liftingDistanceLabel;
    private JLabel liftingSpeedLabel;
    private JLabel retractSpeedLabel;
    private JLabel bottomLightOffDelayLabel;
    private JLabel lightOffDelayLabel;

    private MainForm mainForm;
    private PhotonFile photonFile;
    private String path;

    public SaveDialog(MainForm mainForm) {
        super(mainForm.frame);
        this.mainForm = mainForm;

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        setTitle("Save");
        version2FormatCheckBox.addItemListener(new ItemListener() {

            public void itemStateChanged(ItemEvent e) {
                version2Click();
            }
        });
    }

    private void version2Click() {
        botomLiftDistanceLabel.setEnabled(version2FormatCheckBox.isSelected());
        bottomLiftSpeedLabel.setEnabled(version2FormatCheckBox.isSelected());
        liftingDistanceLabel.setEnabled(version2FormatCheckBox.isSelected());
        liftingSpeedLabel.setEnabled(version2FormatCheckBox.isSelected());
        retractSpeedLabel.setEnabled(version2FormatCheckBox.isSelected());
        bottomLightOffDelayLabel.setEnabled(version2FormatCheckBox.isSelected());
        lightOffDelayLabel.setEnabled(version2FormatCheckBox.isSelected());

        bottomLiftDistance.setEnabled(version2FormatCheckBox.isSelected());
        bottomLiftSpeed.setEnabled(version2FormatCheckBox.isSelected());
        liftingDistance.setEnabled(version2FormatCheckBox.isSelected());
        liftingSpeed.setEnabled(version2FormatCheckBox.isSelected());
        retractSpeed.setEnabled(version2FormatCheckBox.isSelected());
        bottomLightOffDelay.setEnabled(version2FormatCheckBox.isSelected());
        lightOffDelay.setEnabled(version2FormatCheckBox.isSelected());

    }

    private void onOK() {
        File file = new File(path + File.separatorChar + textName.getText());
        try {
            IFileHeader header = photonFile.getPhotonFileHeader();
            header.setExposureTimeSeconds(getFloat(textExposure.getText()));
            header.setOffTimeSeconds(getFloat(textOffTime.getText()));
            header.setExposureBottomTimeSeconds(getFloat(textBottomExposure.getText()));
            header.setBottomLayers(Integer.parseInt(textBottomLayers.getText()));

            if (version2FormatCheckBox.isSelected()) {
                if (photonFile.getVersion() == 1) {
                    photonFile.changeToVersion2();
                }
                PhotonFilePrintParameters parameters = ((PhotonFileHeader) photonFile.getPhotonFileHeader()).photonFilePrintParameters;
                parameters.bottomLiftDistance = getFloat(bottomLiftDistance.getText());
                parameters.bottomLiftSpeed = getFloat(bottomLiftSpeed.getText());
                parameters.liftingDistance = getFloat(liftingDistance.getText());
                parameters.liftingSpeed = getFloat(liftingSpeed.getText());
                parameters.retractSpeed = getFloat(retractSpeed.getText());
                parameters.bottomLightOffDelay = getFloat(bottomLightOffDelay.getText());
                parameters.lightOffDelay = getFloat(lightOffDelay.getText());
            }

            photonFile.adjustLayerSettings();
            if (fixZcheck.isSelected()) {
                photonFile.fixLayerHeights();
            }
            photonFile.saveFile(file);
            mainForm.setFileName(file);
            mainForm.showFileInformation();
            mainForm.marginInfo.setForeground(Color.decode("#008800"));
            mainForm.marginInfo.setText("Saved: " + file.getAbsolutePath());
        } catch (Exception e) {
            mainForm.marginInfo.setForeground(Color.red);
            mainForm.marginInfo.setText(e.getMessage());
        }
        dispose();
    }

    private void onCancel() {
        dispose();
    }

    public void setInformation(PhotonFile photonFile, String path, String name) {
        this.photonFile = photonFile;
        this.path = path;
        if (name.toLowerCase().endsWith(".photon")) {
            name = makeFileName(path, name.substring(0, name.length() - 7), ".photon");
        } else if (name.toLowerCase().endsWith(".cbddlp")) {
            name = makeFileName(path, name.substring(0, name.length() - 7), ".cbddlp");

        } else {
            name = name + "1.photon";
        }
        textName.setText(name);
        textExposure.setText(String.format("%.1f", photonFile.getPhotonFileHeader().getExposureTimeSeconds()));
        textOffTime.setText(String.format("%.1f", photonFile.getPhotonFileHeader().getOffTimeSeconds()));
        textBottomLayers.setText(String.format("%d", photonFile.getPhotonFileHeader().getBottomLayers()));
        textBottomExposure.setText(String.format("%.1f", photonFile.getPhotonFileHeader().getBottomExposureTimeSeconds()));

        fixZcheck.setSelected(false);
        float drift = photonFile.getZdrift();
        if (drift > 0.001f) {
            fixZcheck.setEnabled(true);
            fixZcheck.setText(String.format("Total Z error is %f mm", drift));
        }

        if (photonFile.getVersion() == 1) {
            bottomLiftDistance.setText("5.0");
            bottomLiftSpeed.setText("300.0");
            liftingDistance.setText("5.0");
            liftingSpeed.setText("300.0");
            retractSpeed.setText("300.0");
            bottomLightOffDelay.setText("0.0");
            lightOffDelay.setText("0.0");
        } else {
            version2FormatCheckBox.setSelected(true);
            version2FormatCheckBox.setEnabled(false);
            PhotonFilePrintParameters parameters = ((PhotonFileHeader) photonFile.getPhotonFileHeader()).photonFilePrintParameters;

            bottomLiftDistance.setText(String.format("%.1f", parameters.bottomLiftDistance));
            bottomLiftSpeed.setText(String.format("%.1f", parameters.bottomLiftSpeed));
            liftingDistance.setText(String.format("%.1f", parameters.liftingDistance));
            liftingSpeed.setText(String.format("%.1f", parameters.liftingSpeed));
            retractSpeed.setText(String.format("%.1f", parameters.retractSpeed));
            bottomLightOffDelay.setText(String.format("%.1f", parameters.bottomLightOffDelay));
            lightOffDelay.setText(String.format("%.1f", parameters.lightOffDelay));
        }

    }

    private String makeFileName(String path, String name, String ext) {
        Pattern pattern = Pattern.compile("(.+)-[0-9]+");
        Matcher matcher = pattern.matcher(name);
        if (matcher.matches()) {
            name = matcher.group(1);
        }
        int i = 1;
        while ((new File(path + File.separatorChar + name + "-" + i + ext)).exists()) i++;
        return name + "-" + i + ext;
    }

    private float getFloat(String str) {
        return Float.parseFloat(str.replace(',', '.'));
    }


    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        contentPane = new JPanel();
        contentPane.setLayout(new GridLayoutManager(2, 1, new Insets(10, 10, 10, 10), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel2, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        buttonOK = new JButton();
        buttonOK.setText("OK");
        panel2.add(buttonOK, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonCancel = new JButton();
        buttonCancel.setText("Cancel");
        panel2.add(buttonCancel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(9, 4, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Name");
        panel3.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel3.add(spacer2, new GridConstraints(8, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        textName = new JTextField();
        panel3.add(textName, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Exposure");
        panel3.add(label2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Bottom Exposure");
        panel3.add(label3, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        textExposure = new JTextField();
        panel3.add(textExposure, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(150, -1), new Dimension(150, -1), new Dimension(150, -1), 0, false));
        textBottomExposure = new JTextField();
        panel3.add(textBottomExposure, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(150, -1), new Dimension(150, -1), new Dimension(150, -1), 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Off Time");
        panel3.add(label4, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        textOffTime = new JTextField();
        panel3.add(textOffTime, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(150, -1), new Dimension(150, -1), new Dimension(150, -1), 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("Bottom Layers");
        panel3.add(label5, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        textBottomLayers = new JTextField();
        panel3.add(textBottomLayers, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(150, -1), new Dimension(150, -1), new Dimension(150, -1), 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("Fix Z settings");
        panel3.add(label6, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fixZcheck = new JCheckBox();
        fixZcheck.setEnabled(false);
        fixZcheck.setText("Noting to fix");
        panel3.add(fixZcheck, new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        botomLiftDistanceLabel = new JLabel();
        botomLiftDistanceLabel.setEnabled(false);
        botomLiftDistanceLabel.setText("Bottom Lift Distance");
        panel3.add(botomLiftDistanceLabel, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        bottomLiftSpeedLabel = new JLabel();
        bottomLiftSpeedLabel.setEnabled(false);
        bottomLiftSpeedLabel.setText("Bottom Lift Speed");
        panel3.add(bottomLiftSpeedLabel, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        liftingDistanceLabel = new JLabel();
        liftingDistanceLabel.setEnabled(false);
        liftingDistanceLabel.setText("Lifting Distance");
        panel3.add(liftingDistanceLabel, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        liftingSpeedLabel = new JLabel();
        liftingSpeedLabel.setEnabled(false);
        liftingSpeedLabel.setText("Lifting Speed");
        panel3.add(liftingSpeedLabel, new GridConstraints(4, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        retractSpeedLabel = new JLabel();
        retractSpeedLabel.setEnabled(false);
        retractSpeedLabel.setText("Retract Speed");
        panel3.add(retractSpeedLabel, new GridConstraints(5, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        bottomLightOffDelayLabel = new JLabel();
        bottomLightOffDelayLabel.setEnabled(false);
        bottomLightOffDelayLabel.setText("Bottom Light Off Delay");
        panel3.add(bottomLightOffDelayLabel, new GridConstraints(6, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lightOffDelayLabel = new JLabel();
        lightOffDelayLabel.setEnabled(false);
        lightOffDelayLabel.setText("Light Off Delay");
        panel3.add(lightOffDelayLabel, new GridConstraints(7, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        version2FormatCheckBox = new JCheckBox();
        version2FormatCheckBox.setSelected(false);
        version2FormatCheckBox.setText("Version 2 format");
        panel3.add(version2FormatCheckBox, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        bottomLiftDistance = new JTextField();
        bottomLiftDistance.setEnabled(false);
        panel3.add(bottomLiftDistance, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(150, -1), new Dimension(150, -1), new Dimension(150, -1), 0, false));
        bottomLiftSpeed = new JTextField();
        bottomLiftSpeed.setEnabled(false);
        panel3.add(bottomLiftSpeed, new GridConstraints(2, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(150, -1), new Dimension(150, -1), new Dimension(150, -1), 0, false));
        liftingDistance = new JTextField();
        liftingDistance.setEnabled(false);
        panel3.add(liftingDistance, new GridConstraints(3, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(150, -1), new Dimension(150, -1), new Dimension(150, -1), 0, false));
        liftingSpeed = new JTextField();
        liftingSpeed.setEnabled(false);
        panel3.add(liftingSpeed, new GridConstraints(4, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(150, -1), new Dimension(150, -1), new Dimension(150, -1), 0, false));
        retractSpeed = new JTextField();
        retractSpeed.setEnabled(false);
        panel3.add(retractSpeed, new GridConstraints(5, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(150, -1), new Dimension(150, -1), new Dimension(150, -1), 0, false));
        bottomLightOffDelay = new JTextField();
        bottomLightOffDelay.setEnabled(false);
        panel3.add(bottomLightOffDelay, new GridConstraints(6, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(150, -1), new Dimension(150, -1), new Dimension(150, -1), 0, false));
        lightOffDelay = new JTextField();
        lightOffDelay.setEnabled(false);
        panel3.add(lightOffDelay, new GridConstraints(7, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(150, -1), new Dimension(150, -1), new Dimension(150, -1), 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }

}
