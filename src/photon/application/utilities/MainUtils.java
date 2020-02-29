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

package photon.application.utilities;

import photon.application.MainForm;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * by bn on 09/07/2018.
 */
public class MainUtils {

    public static final String PRINT_MENU = "PRINTINFO";
    public static final String SMALL_MENU = "SMALLIMG";
    public static final String LARGE_MENU = "LARGEIMG";


    public static void makeMenuBar(JFrame frame, MainForm mainForm) {
        JMenuBar menuBar = new JMenuBar();

        JMenu infoMenu = new JMenu("Information");
        infoMenu.setMnemonic(KeyEvent.VK_I);
        infoMenu.getAccessibleContext().setAccessibleDescription("Information about the Photon file");
        menuBar.add(infoMenu);

        JMenuItem menuItem = new JMenuItem("Print Information");
        menuItem.setActionCommand(PRINT_MENU);
        menuItem.addActionListener(mainForm);
        infoMenu.add(menuItem);

        menuItem = new JMenuItem("Small Preview");
        menuItem.setActionCommand(SMALL_MENU);
        menuItem.addActionListener(mainForm);
        infoMenu.add(menuItem);

        menuItem = new JMenuItem("Large Preview");
        menuItem.setActionCommand(LARGE_MENU);
        menuItem.addActionListener(mainForm);
        infoMenu.add(menuItem);

        frame.setJMenuBar(menuBar);
    }

    public static void setWindowPosition(JFrame window, int screen) {
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] allDevices = env.getScreenDevices();
        int topLeftX, topLeftY, screenX, screenY, windowPosX, windowPosY;

        if (screen < allDevices.length && screen > -1) {
            topLeftX = allDevices[screen].getDefaultConfiguration().getBounds().x;
            topLeftY = allDevices[screen].getDefaultConfiguration().getBounds().y;

            screenX = allDevices[screen].getDefaultConfiguration().getBounds().width;
            screenY = allDevices[screen].getDefaultConfiguration().getBounds().height;
        } else {
            topLeftX = allDevices[0].getDefaultConfiguration().getBounds().x;
            topLeftY = allDevices[0].getDefaultConfiguration().getBounds().y;

            screenX = allDevices[0].getDefaultConfiguration().getBounds().width;
            screenY = allDevices[0].getDefaultConfiguration().getBounds().height;
        }

        windowPosX = ((screenX - window.getWidth()) / 2) + topLeftX;
        windowPosY = ((screenY - window.getHeight()) / 2) + topLeftY;

        window.setLocation(windowPosX, windowPosY);
    }

    public static void setIcon(Frame f) {
        ArrayList<Image> icons = new ArrayList<>(); // 16, 20, 32, 40
        icons.add(new ImageIcon(hexToBytes(IPhotonImages.icon16x16)).getImage());
        icons.add(new ImageIcon(hexToBytes(IPhotonImages.icon20x20)).getImage());
        icons.add(new ImageIcon(hexToBytes(IPhotonImages.icon32x32)).getImage());
        icons.add(new ImageIcon(hexToBytes(IPhotonImages.icon40x40)).getImage());
        icons.add(new ImageIcon(hexToBytes(IPhotonImages.icon64x64)).getImage());
        f.setIconImages(icons);
    }

    public static boolean isPhotonFile(File file) {return file.exists() && file.isFile() && (file.getName().toLowerCase().endsWith("photon") || (file.getName().toLowerCase().endsWith("cbddlp")) || (file.getName().toLowerCase().endsWith("photons")));}

    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static byte[] hexToBytes(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public static byte[] getLogo() {
        return hexToBytes(IPhotonImages.logo);
    }

    private static byte[] getBinaryData(File entry) throws Exception {
        if (entry.isFile()) {
            int fileSize = (int) entry.length();
            byte[] fileData = new byte[fileSize];

            InputStream stream = new FileInputStream(entry);
            int bytesRead = 0;
            while (bytesRead < fileSize) {
                int readCount = stream.read(fileData, bytesRead, fileSize - bytesRead);
                if (readCount < 0) {
                    throw new IOException("Could not read all bytes of the file");
                }
                bytesRead += readCount;
            }

            return fileData;
        }
        return null;
    }

    public static void boot() {
        try {

            String[] files = new String[] {
                    "install/img/win/validator16.png",
                    "install/img/win/validator20.png",
                    "install/img/win/validator32.png",
                    "install/img/win/validator40.png",
                    "install/img/win/validator64.png",
                    "install/img/logos/validatorlogo.png",
            };

            for(String fileName : files) {
                File file = new File(fileName);
                System.out.println(file.getName() + " = ");
                byte[] bytes = getBinaryData(file);
                String hex = MainUtils.bytesToHex(bytes);
                while (hex.length() > 0) {
                    if (hex.length() < 100) {
                        System.out.println("\"" + hex + "\";");
                        hex = "";
                    } else {
                        System.out.println("\"" + hex.substring(0, 100) + "\" +");
                        hex = hex.substring(100);
                    }
                }
                System.out.println();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static int getSystemDefaultModifierMask() {
        int mask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
        if (mask == Event.META_MASK) {
            return KeyEvent.META_DOWN_MASK;
        } else if (mask == Event.ALT_MASK) {
            return KeyEvent.ALT_DOWN_MASK;
        }
        return KeyEvent.CTRL_DOWN_MASK;
    }
}
