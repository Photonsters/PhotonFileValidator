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
import photon.application.utilities.PhotonAaWorker;
import photon.file.PhotonFile;
import photon.file.parts.PhotonAaMatrix;
import photon.file.ui.PhotonAALevel;
import photon.file.ui.PhotonAaPanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;

public class ConvertDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JSlider aaLevel;
    private JLabel aaLevelValue;
    private JButton gb3xBtn;
    private JTextField text11;
    private JTextField text21;
    private JTextField text31;
    private JTextField text41;
    private JTextField text51;
    private JTextField text12;
    private JTextField text22;
    private JTextField text32;
    private JTextField text42;
    private JTextField text52;
    private JTextField text13;
    private JTextField text23;
    private JTextField text33;
    private JTextField text43;
    private JTextField text53;
    private JTextField text14;
    private JTextField text24;
    private JTextField text34;
    private JTextField text44;
    private JTextField text54;
    private JTextField text15;
    private JTextField text25;
    private JTextField text35;
    private JTextField text45;
    private JTextField text55;
    private JButton gb5xBtn;
    private JButton b3xBtn;
    private JButton b5xBtn;
    private JButton s3xBtn;
    private JButton s5xBtn;

    private String[] gb3 = new String[]{"", "", "", "", "", "", "1", "2", "1", "", "", "2", "4", "2", "", "", "1", "2", "1", "", "", "", "", "", ""};
    private String[] gb5 = new String[]{"1", "4", "6", "4", "1", "4", "16", "24", "16", "4", "6", "24", "36", "24", "6", "4", "16", "24", "16", "4", "1", "4", "6", "4", "1"};

    private String[] b3 = new String[]{"", "", "", "", "", "", "0", "1", "0", "", "", "1", "2", "1", "", "", "0", "1", "0", "", "", "", "", "", ""};
    private String[] b5 = new String[]{"0", "0", "1", "0", "0", "0", "1", "1", "1", "0", "1", "1", "2", "1", "1", "0", "1", "1", "1", "0", "0", "0", "1", "0", "0"};

    private String[] s3 = new String[]{"", "", "", "", "", "", "1", "1", "1", "", "", "1", "1", "1", "", "", "1", "1", "1", "", "", "", "", "", ""};
    private String[] s5 = new String[]{"1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1"};

    private MainForm mainForm;


    public ConvertDialog(MainForm mainForm) {
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

        gb3xBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setMatrix(gb3);
            }
        });

        gb5xBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setMatrix(gb5);
            }
        });

        b3xBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setMatrix(b3);
            }
        });

        b5xBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setMatrix(b5);
            }
        });

        s3xBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setMatrix(s3);
            }
        });

        s5xBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setMatrix(s5);
            }
        });

        aaLevel.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int aaSelect = ((JSlider) e.getSource()).getValue();
                PhotonAALevel photonAALevel = PhotonAALevel.find(aaSelect);
                aaLevelValue.setText(photonAALevel.name);
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        int aaSelect = aaLevel.getValue();
        PhotonAALevel photonAALevel = PhotonAALevel.find(aaSelect);

        PhotonAaMatrix matrix = new PhotonAaMatrix();
        matrix.set(1, 1, getInt(text11));
        matrix.set(2, 1, getInt(text12));
        matrix.set(3, 1, getInt(text13));
        matrix.set(4, 1, getInt(text14));
        matrix.set(5, 1, getInt(text15));

        matrix.set(1, 2, getInt(text21));
        matrix.set(2, 2, getInt(text22));
        matrix.set(3, 2, getInt(text23));
        matrix.set(4, 2, getInt(text24));
        matrix.set(5, 2, getInt(text25));

        matrix.set(1, 3, getInt(text31));
        matrix.set(2, 3, getInt(text32));
        matrix.set(3, 3, getInt(text33));
        matrix.set(4, 3, getInt(text34));
        matrix.set(5, 3, getInt(text35));

        matrix.set(1, 4, getInt(text41));
        matrix.set(2, 4, getInt(text42));
        matrix.set(3, 4, getInt(text43));
        matrix.set(4, 4, getInt(text44));
        matrix.set(5, 4, getInt(text45));

        matrix.set(1, 5, getInt(text51));
        matrix.set(2, 5, getInt(text52));
        matrix.set(3, 5, getInt(text53));
        matrix.set(4, 5, getInt(text54));
        matrix.set(5, 5, getInt(text55));

        PhotonAaWorker photonAaWorker = new PhotonAaWorker(mainForm, photonAALevel.levels, matrix);
        photonAaWorker.execute();

        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public void setInformation(PhotonFile photonFile) {

        aaLevel.setValue(PhotonAALevel.findByLevel(photonFile.getAALevels()).index);
        setMatrix(gb3);

        aaLevel.requestFocus();
        aaLevel.grabFocus();

    }

    private int getInt(JTextField jTextField) {
        String str = jTextField.getText();
        if (str != null && str.length() > 0) {
            try {
                return Integer.parseInt(str);
            } catch (Exception e) {
                // ignore
            }
        }
        return 0;
    }

    private void setMatrix(String[] val) {
        text11.setText(val[0]);
        text12.setText(val[1]);
        text13.setText(val[2]);
        text14.setText(val[3]);
        text15.setText(val[4]);

        text21.setText(val[5]);
        text22.setText(val[6]);
        text23.setText(val[7]);
        text24.setText(val[8]);
        text25.setText(val[9]);

        text31.setText(val[10]);
        text32.setText(val[11]);
        text33.setText(val[12]);
        text34.setText(val[13]);
        text35.setText(val[14]);

        text41.setText(val[15]);
        text42.setText(val[16]);
        text43.setText(val[17]);
        text44.setText(val[18]);
        text45.setText(val[19]);

        text51.setText(val[20]);
        text52.setText(val[21]);
        text53.setText(val[22]);
        text54.setText(val[23]);
        text55.setText(val[24]);

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
        contentPane.setLayout(new GridLayoutManager(3, 1, new Insets(10, 10, 10, 10), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel1, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1, true, false));
        panel1.add(panel2, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        buttonOK = new JButton();
        buttonOK.setText("OK");
        panel2.add(buttonOK, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonCancel = new JButton();
        buttonCancel.setText("Cancel");
        panel2.add(buttonCancel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(8, 7, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel3, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Anti Aliase Method");
        panel3.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel3.add(spacer2, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        panel3.add(spacer3, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Martix");
        panel3.add(label2, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        gb3xBtn = new JButton();
        gb3xBtn.setText("Gaussian Blur 3x");
        panel3.add(gb3xBtn, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        gb5xBtn = new JButton();
        gb5xBtn.setText("Gaussian Blur 5x");
        panel3.add(gb5xBtn, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        b3xBtn = new JButton();
        b3xBtn.setText("Blur 3x");
        panel3.add(b3xBtn, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        b5xBtn = new JButton();
        b5xBtn.setText("Blur 5x");
        panel3.add(b5xBtn, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        s3xBtn = new JButton();
        s3xBtn.setText("Smooth 3x");
        panel3.add(s3xBtn, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        s5xBtn = new JButton();
        s5xBtn.setText("Smooth 5x");
        panel3.add(s5xBtn, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        text11 = new JTextField();
        text11.setHorizontalAlignment(0);
        panel3.add(text11, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(50, -1), null, 0, false));
        text21 = new JTextField();
        text21.setHorizontalAlignment(0);
        panel3.add(text21, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(50, -1), null, 0, false));
        text31 = new JTextField();
        text31.setHorizontalAlignment(0);
        panel3.add(text31, new GridConstraints(4, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(50, -1), null, 0, false));
        text41 = new JTextField();
        text41.setHorizontalAlignment(0);
        panel3.add(text41, new GridConstraints(5, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(50, -1), null, 0, false));
        text51 = new JTextField();
        text51.setHorizontalAlignment(0);
        panel3.add(text51, new GridConstraints(6, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(50, -1), null, 0, false));
        text12 = new JTextField();
        text12.setHorizontalAlignment(0);
        panel3.add(text12, new GridConstraints(2, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(50, -1), null, 0, false));
        text22 = new JTextField();
        text22.setHorizontalAlignment(0);
        panel3.add(text22, new GridConstraints(3, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(50, -1), null, 0, false));
        text32 = new JTextField();
        text32.setHorizontalAlignment(0);
        panel3.add(text32, new GridConstraints(4, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(50, -1), null, 0, false));
        text42 = new JTextField();
        text42.setHorizontalAlignment(0);
        panel3.add(text42, new GridConstraints(5, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(50, -1), null, 0, false));
        text52 = new JTextField();
        text52.setHorizontalAlignment(0);
        panel3.add(text52, new GridConstraints(6, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(50, -1), null, 0, false));
        text13 = new JTextField();
        text13.setHorizontalAlignment(0);
        panel3.add(text13, new GridConstraints(2, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(50, -1), null, 0, false));
        text23 = new JTextField();
        text23.setHorizontalAlignment(0);
        panel3.add(text23, new GridConstraints(3, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(50, -1), null, 0, false));
        text33 = new JTextField();
        text33.setHorizontalAlignment(0);
        panel3.add(text33, new GridConstraints(4, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(50, -1), null, 0, false));
        text43 = new JTextField();
        text43.setHorizontalAlignment(0);
        panel3.add(text43, new GridConstraints(5, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(50, -1), null, 0, false));
        text53 = new JTextField();
        text53.setHorizontalAlignment(0);
        panel3.add(text53, new GridConstraints(6, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(50, -1), null, 0, false));
        text14 = new JTextField();
        text14.setHorizontalAlignment(0);
        panel3.add(text14, new GridConstraints(2, 5, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(50, -1), null, 0, false));
        text24 = new JTextField();
        text24.setHorizontalAlignment(0);
        panel3.add(text24, new GridConstraints(3, 5, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(50, -1), null, 0, false));
        text34 = new JTextField();
        text34.setHorizontalAlignment(0);
        panel3.add(text34, new GridConstraints(4, 5, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(50, -1), null, 0, false));
        text44 = new JTextField();
        text44.setHorizontalAlignment(0);
        panel3.add(text44, new GridConstraints(5, 5, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(50, -1), null, 0, false));
        text54 = new JTextField();
        text54.setHorizontalAlignment(0);
        panel3.add(text54, new GridConstraints(6, 5, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(50, -1), null, 0, false));
        text15 = new JTextField();
        text15.setHorizontalAlignment(0);
        panel3.add(text15, new GridConstraints(2, 6, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(50, -1), null, 0, false));
        text25 = new JTextField();
        text25.setHorizontalAlignment(0);
        panel3.add(text25, new GridConstraints(3, 6, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(50, -1), null, 0, false));
        text35 = new JTextField();
        text35.setHorizontalAlignment(0);
        panel3.add(text35, new GridConstraints(4, 6, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(50, -1), null, 0, false));
        text45 = new JTextField();
        text45.setHorizontalAlignment(0);
        panel3.add(text45, new GridConstraints(5, 6, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(50, -1), null, 0, false));
        text55 = new JTextField();
        text55.setHorizontalAlignment(0);
        panel3.add(text55, new GridConstraints(6, 6, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(50, -1), null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(2, 3, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel4, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Anti Aliase Level");
        panel4.add(label3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        aaLevel = new JSlider();
        aaLevel.setMajorTickSpacing(1);
        aaLevel.setMaximum(5);
        aaLevel.setMinimum(1);
        aaLevel.setMinorTickSpacing(1);
        aaLevel.setPaintLabels(false);
        aaLevel.setPaintTicks(true);
        aaLevel.setSnapToTicks(true);
        aaLevel.setValue(1);
        aaLevel.setValueIsAdjusting(false);
        panel4.add(aaLevel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        aaLevelValue = new JLabel();
        aaLevelValue.setText("No Anti Aliasing");
        panel4.add(aaLevelValue, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }

}
