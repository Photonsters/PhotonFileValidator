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

import javax.swing.*;
import java.awt.*;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

/**
 * by bn on 31/07/2018.
 */
public class BaseFrame extends JFrame implements AWTEventListener {
    public BaseForm baseForm;

    public BaseFrame(String title) throws HeadlessException {
        super(title);
        this.getToolkit().addAWTEventListener(this, AWTEvent.KEY_EVENT_MASK);
    }

    private void initMenu() {
        JMenuBar menuBar = new JMenuBar();

        // file
        JMenu fileMenu = new JMenu();
        fileMenu.setText("File");
        menuBar.add(fileMenu);

        // file - open
        JMenuItem openItem = new JMenuItem();
        openItem.setAction(((MainForm)baseForm).actionOpen);
        fileMenu.add(openItem);

        // file - save
        JMenuItem saveItem = new JMenuItem();
        saveItem.setAction(((MainForm)baseForm).actionSave);
        fileMenu.add(saveItem);

        JSeparator s0 = new JSeparator();
        fileMenu.add(s0);

        // file - info
        JMenuItem infoItem = new JMenuItem();
        infoItem.setAction(((MainForm)baseForm).actionInfo);
        fileMenu.add(infoItem);

        JSeparator s1 = new JSeparator();
        fileMenu.add(s1);

        // file - close
        JMenuItem closeItem = new JMenuItem();
        closeItem.setText("Close");
        closeItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        fileMenu.add(closeItem);


        // tools
        JMenu toolsMenu = new JMenu("Tools");
        menuBar.add(toolsMenu);

        // tools - convert
        JMenuItem convertItem = new JMenuItem();
        convertItem.setAction(((MainForm)baseForm).actionConvert);
        toolsMenu.add(convertItem);

        JSeparator s2 = new JSeparator();
        toolsMenu.add(s2);

        // tools - fix
        JMenuItem fixItem = new JMenuItem();
        fixItem.setAction(((MainForm)baseForm).actionFix);
        toolsMenu.add(fixItem);

        // tools - exposure
        JMenuItem exposureItem = new JMenuItem();
        exposureItem.setAction(((MainForm)baseForm).actionExposureCompensation);
        toolsMenu.add(exposureItem);



        setJMenuBar(menuBar);
    }

    @Override
    public void eventDispatched(AWTEvent event) {
        if(event instanceof KeyEvent){
            KeyEvent key = (KeyEvent)event;
            if(key.getID()== KeyEvent.KEY_PRESSED){ //Handle key presses

                if ((key.getModifiers() & Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()) == Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()) {
                    baseForm.handleKeyEvent(key);
                }
            }
        }
    }

    public void setMainForm(MainForm mainForm) {
        baseForm = mainForm;
        mainForm.frame = this;

        initMenu();
    }
}
