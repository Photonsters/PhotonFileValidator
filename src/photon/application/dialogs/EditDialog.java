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
import photon.file.parts.PhotonDot;
import photon.file.parts.PhotonFileLayer;
import photon.file.parts.PhotonLayer;
import photon.file.ui.PhotonEditPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.security.PublicKey;
import java.util.HashSet;

public class EditDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JLabel infoText;
    private JPanel editArea;
    private JButton editModeBtn;

    private EditDialog me;
    private MainForm mainForm;
    private PhotonFile photonFile;
    private PhotonFileLayer fileLayer;
    private PhotonLayer layer;
    private int layerNo;
    private int layerX;
    private int layerY;
    private HashSet<PhotonDot> dots;

    private PhotonDot pressedDot;
    private boolean editModeSwap = true;


    public EditDialog(MainForm mainForm) {
        super(mainForm.frame);
        this.me = this;
        this.mainForm = mainForm;

        $$$setupUI$$$();

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {onOK();}
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {onCancel();}
        });

        editModeBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editModeSwap = !editModeSwap;
                editModeBtn.setText(editModeSwap ? "Uses Swap" : "Uses ON/OFF");
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

        editArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                pressedDot = getPosition(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                PhotonDot releasedDot = getPosition(e);
                if (pressedDot != null && releasedDot != null) {
                    boolean onOff = false;
                    if (!editModeSwap) {
                        byte original = layer.get(layerY + pressedDot.y, layerX + pressedDot.x);
                        onOff = original != PhotonLayer.OFF;
                        if (dots.contains(new PhotonDot(layerY + pressedDot.y, layerX + pressedDot.x))) {
                            onOff = !onOff;
                        }
                    }

                    int x1 = Integer.min(pressedDot.x, releasedDot.x);
                    int x2 = Integer.max(pressedDot.x, releasedDot.x);
                    int y1 = Integer.min(pressedDot.y, releasedDot.y);
                    int y2 = Integer.max(pressedDot.y, releasedDot.y);

                    for (int x = x1; x <= x2; x++) {
                        for (int y = y1; y <= y2; y++) {
                            if (editModeSwap) {
                                me.handleClick(x, y);
                            } else {
                                me.handleClick(x, y, onOff);
                            }
                        }
                    }
                }
            }
        });
    }

    private void handleClick(int x, int y, boolean onOff) {
        byte original = layer.get(layerY + y, layerX + x);
        boolean isOriginalOn = original != PhotonLayer.OFF;

        PhotonDot dot = new PhotonDot(layerY + y, layerX + x);

        if (dots.contains(dot)) {
            dots.remove(dot);
        }
        if ((onOff && !isOriginalOn) || (!onOff && isOriginalOn)) {
            dots.add(dot);
        }

        Color color = onOff ? Color.darkGray : Color.cyan;
        ;
        ((PhotonEditPanel) editArea).drawDot(x, y, layer, color);
        editArea.repaint();
    }

    private void handleClick(int x, int y) {
        Color color = isSet(x, y);
        ((PhotonEditPanel) editArea).drawDot(x, y, layer, color);
        editArea.repaint();
    }

    private Color isSet(int x, int y) {
        byte original = layer.get(layerY + y, layerX + x);
        boolean result = original != PhotonLayer.OFF;

        PhotonDot dot = new PhotonDot(layerY + y, layerX + x);
        if (dots.contains(dot)) {
            dots.remove(dot);

            switch (layer.get(layerY + y, layerX + x)) {
                case PhotonLayer.SUPPORTED:
                    return Color.decode("#008800");

                case PhotonLayer.CONNECTED:
                    return Color.decode("#FFFF00");

                case PhotonLayer.ISLAND:
                    return Color.decode("#FF0000");

                default:
                    return Color.black;

            }

        } else {
            dots.add(dot);
            return result ? Color.darkGray : Color.cyan;
        }
    }

    private void onOK() {
        if (dots.size() > 0) {
            for (PhotonDot dot : dots) {
                byte type = layer.get(dot.x, dot.y);
                if (type == PhotonLayer.OFF) {
                    layer.island(dot.x, dot.y);
                } else {
                    layer.remove(dot.x, dot.y, type);
                }
            }
            try {
                fileLayer.saveLayer(layer);
                photonFile.calculate(layerNo);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            mainForm.changeLayer();
            mainForm.showMarginAndIslandInformation();
        }
        dispose();
    }

    private void onCancel() {
        dispose();
    }


    public void setInformation(PhotonFile photonFile, int layerNo, int mouseX, int mouseY) {
        this.dots = new HashSet<>();
        this.layerNo = layerNo;
        this.photonFile = photonFile;
        this.fileLayer = photonFile.getLayer(layerNo);
        this.layer = fileLayer.getLayer();
        int indexX = (mouseX < 38) ? 1 : mouseX - 38;
        int indexY = (mouseY < 23) ? 1 : mouseY - 23;

        if (indexX + 74 >= photonFile.getWidth()) {
            indexX = photonFile.getWidth() - 74;
        }
        if (indexY + 44 >= photonFile.getHeight()) {
            indexY = photonFile.getHeight() - 44;
        }

        layerX = indexX - 1;
        layerY = indexY - 1;

        infoText.setText("Showing column " + indexX + " to " + (indexX + 74) + ", in row " + indexY + " to " + (indexY + 44) + ")");
        ((PhotonEditPanel) editArea).drawLayer(layerX, layerY, layer);
        editArea.repaint();
    }


    private void createUIComponents() {
        editArea = new PhotonEditPanel(780, 480);
    }

    private static PhotonDot getPosition(MouseEvent e) {
        if (e.getX() > 15 && e.getY() > 15) {
            int x = (e.getX() - 15) / 10;
            int y = (e.getY() - 15) / 10;
            if (x < 75 && y < 45) {
                return new PhotonDot(x, y);
            }
        }
        return null;
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
        contentPane = new JPanel();
        contentPane.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(3, 1, new Insets(10, 10, 10, 10), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel1, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer1 = new com.intellij.uiDesigner.core.Spacer();
        panel1.add(spacer1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel2, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        buttonOK = new JButton();
        buttonOK.setText("OK");
        panel2.add(buttonOK, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonCancel = new JButton();
        buttonCancel.setText("Cancel");
        panel2.add(buttonCancel, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel3, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, new Dimension(-1, 30), new Dimension(-1, 30), new Dimension(-1, 30), 0, false));
        infoText = new JLabel();
        infoText.setText("");
        panel3.add(infoText, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        editModeBtn = new JButton();
        editModeBtn.setText("Using Swap");
        panel3.add(editModeBtn, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel4, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        editArea.setBackground(new Color(-1250068));
        editArea.setEnabled(true);
        panel4.add(editArea, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(300, 100), new Dimension(300, 100), null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() { return contentPane; }

}
