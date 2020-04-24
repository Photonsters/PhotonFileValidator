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
import photon.file.parts.PhotonFilePreview;
import photon.file.ui.PhotonPreviewImage;
import photon.file.ui.ScrollPosition;
import photon.file.ui.ScrollUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class PreviewDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JPanel imagePanel;
    private JScrollPane imageScrollPane;

    private PhotonFilePreview preview;

    public PreviewDialog(Frame frame, PhotonFilePreview preview) {
        super(frame);
        this.preview = preview;

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {onOK();}
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
// add your code here
        dispose();
    }

    private void onCancel() {
// add your code here if necessary
        dispose();
    }

    public void setInformation(PhotonFilePreview preview, boolean large) {
        this.preview = preview;
        ((PhotonPreviewImage) imagePanel).reInit(preview.getResolutionX(), preview.getResolutionY());
        ((PhotonPreviewImage) imagePanel).drawImage(preview);

        setTitle(large ? "Large preview" : "Small preview");
    }

    public void update() {
        if (imageScrollPane != null) {
            ScrollUtil.scrollTo(imageScrollPane, ScrollPosition.HorizontalCenter);
            ScrollUtil.scrollTo(imageScrollPane, ScrollPosition.VerticalCenter);
        }
    }

    private void createUIComponents() {
        imagePanel = new PhotonPreviewImage(preview.getResolutionX(), preview.getResolutionY());
    }

}
