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

    public static boolean isPhotonFile(File file) {return file.exists() && file.isFile() && (file.getName().toLowerCase().endsWith("photon") || (file.getName().toLowerCase().endsWith("cbddlp")));}

    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
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
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
    public static byte[] getLogo() {
        return hexToBytes(logo);
    }

//    try {
//        byte[] bytes = getBinaryData(new File("install/img/photon60x60.png"));
//        String hex = MainUtils.bytesToHex(bytes);
//        while (hex.length() > 0) {
//            if (hex.length() < 100) {
//                System.out.println("\"" + hex + "\";");
//                hex = "";
//            } else {
//                System.out.println("\"" + hex.substring(0, 100) + "\" +");
//                hex = hex.substring(100);
//            }
//        }
//        System.out.println();
//    } catch (Exception e) {
//        System.out.println(e);
//    }



    private static String logo =
            "89504E470D0A1A0A0000000D494844520000003C0000003C08060000003AFCD9720000001974455874536F66747761726500" +
            "41646F626520496D616765526561647971C9653C0000038669545874584D4C3A636F6D2E61646F62652E786D700000000000" +
            "3C3F787061636B657420626567696E3D22EFBBBF222069643D2257354D304D7043656869487A7265537A4E54637A6B633964" +
            "223F3E203C783A786D706D65746120786D6C6E733A783D2261646F62653A6E733A6D6574612F2220783A786D70746B3D2241" +
            "646F626520584D5020436F726520352E362D633134302037392E3136303435312C20323031372F30352F30362D30313A3038" +
            "3A32312020202020202020223E203C7264663A52444620786D6C6E733A7264663D22687474703A2F2F7777772E77332E6F72" +
            "672F313939392F30322F32322D7264662D73796E7461782D6E7323223E203C7264663A4465736372697074696F6E20726466" +
            "3A61626F75743D222220786D6C6E733A786D704D4D3D22687474703A2F2F6E732E61646F62652E636F6D2F7861702F312E30" +
            "2F6D6D2F2220786D6C6E733A73745265663D22687474703A2F2F6E732E61646F62652E636F6D2F7861702F312E302F735479" +
            "70652F5265736F75726365526566232220786D6C6E733A786D703D22687474703A2F2F6E732E61646F62652E636F6D2F7861" +
            "702F312E302F2220786D704D4D3A4F726967696E616C446F63756D656E7449443D22786D702E6469643A3766336532623631" +
            "2D646263652D343064642D393966302D3036656462313331663337392220786D704D4D3A446F63756D656E7449443D22786D" +
            "702E6469643A33383144434537373742373731314538393444433846363546354430444245422220786D704D4D3A496E7374" +
            "616E636549443D22786D702E6969643A33383144434537363742373731314538393444433846363546354430444245422220" +
            "786D703A43726561746F72546F6F6C3D2241646F62652050686F746F73686F70204343203230313820284D6163696E746F73" +
            "6829223E203C786D704D4D3A4465726976656446726F6D2073745265663A696E7374616E636549443D22786D702E6969643A" +
            "37663365326236312D646263652D343064642D393966302D303665646231333166333739222073745265663A646F63756D65" +
            "6E7449443D2261646F62653A646F6369643A70686F746F73686F703A61393263396262372D346536362D366434342D613166" +
            "652D336336613036366466366633222F3E203C2F7264663A4465736372697074696F6E3E203C2F7264663A5244463E203C2F" +
            "783A786D706D6574613E203C3F787061636B657420656E643D2272223F3E7661D0DB000004AC4944415478DAEC9B696C4D41" +
            "14C7A755546B09525B52A212111282DAD75A4A9B12C507442CA920692C21D41621F62D0809899D0F95D8A2A4D6D829D19236" +
            "C407D416628912E4D556CFFFE49E2652F76967EECC7BF7F599E49726F7DD99B9FF7B673973CE6998D7EB15A15422EC2E6667" +
            "678BC2C24293FDB60135C12D531DC4C5C589E4E4E4BF7FA02F5C96C4C44453CF510F2C075F4009D8099A99E88834D8690BB7" +
            "BB393A3A5A77FF55C05870072CA42E00F59DC6D766815A3A3BF4A521DC0FD326019C03077C7C4DFAEA1BC03530D2F4C39814" +
            "DC1CEC0117587479A52D3804B240C760121C0516F3823441A1FE10FEDA5B4103B70BA621990796821807ED4482747097FF46" +
            "BA4D7057709A87642B8D2FB0097FE9EB60B01B04C7824D3C0407195C0F3A80532013B40B84E06A6036CFD319BCEDC894B3E0" +
            "A042BFA3F8E5AE0175FD259816951CB01E3496ACFB1C8C074960342053285FB20DB2D0E682DB6092AC06999BDBF09691C543" +
            "4CA67C06ABB8DE7EF08BAFD330ED06668237926DB6003BC055D047A7E0466035C8E5AF2B5B0E83CE6001786FF37B31D80CDA" +
            "836DE0BB64FBDDC125B017B42CF76E3B7B333535B5F4654CE161E85580867D8AC20BEA024E2AF6F9894DD7DAA4C14E9B2FC1" +
            "BD51E98A62A7AFC03450D5E1CA4C0BD43DC567B80F0DC32B7C784019077A493EE037B01174025BC00F87820FF270CD001F25" +
            "EBB6E6D159E139FC55B283333C14E9D4F352E3FE4B43742DCFEF7DE0A7445D8F09C3A3008C602B285F982B4FD92EA743C8E5" +
            "40181E6F78A891497954A17E0D45C3818C8EBEBCFF3EF49760DA3686F2502B56A89FC27B672E3B0054CA2E16FED61F824B14" +
            "E7291D2A8E80137CDE8D6317CF45D053A1BD2285B5467948CB6C39F5D970215370B8CDEF7DFF301C9A4BB44B0EC83037793C" +
            "E84031917D56196C03FFEBDEF17CFE253BB976B0B9781278A8EE064D25EAD5E193500EAFFE4123789982E152D670581B4C82" +
            "BF6968C3134C825D1BBF09172156FE0BFE2F3870252CD404478692E07B6C9D692F112E134A4E3E72E86D30B50FBB49F04E36" +
            "2B1F99ECC40D82C959B888CFC832E5A78A811368C17384E5F82B51A84BC7CEEAC1B668E528882D4D9FA011D130D8BEB0ECD6" +
            "93C0C3BF5F6559A57D15F284504421CD69436E174CE913E4EB9E2E9C6514048560F27FAD107A330A7C2E5A35022894423514" +
            "463DE2506C948CE06CF0C2CF4263D9CABA219CE773502CEA988C607ABB14D35D279C07C5FE55BEF0A988D2276EF25C753ACD" +
            "28064541B8EDB2FBF06B61B94C299C72DC8058CA0248E5FD94D2279A68D8D3070A2B06F5C089E1417EE561C20AAFDCE7F91D" +
            "AE69FDA013510F87ED3C03938595F6705EE72A4D2112CA999C6A7898CB4C07CAF1A0ECDC2253DB12C57236B9402C452C29DB" +
            "AFA0B25A5AA5A534ADF14465372DDF8125C20ADD7C75D29029C1BA0C178A60502C78A5D0944A61EA787855388F3E9CE2153C" +
            "5D68CC1B3125781E1820AC08A26CA185688CB0D212F3743F9849070065C2F717563EC6930ADCFF01CC17563640A6A98732ED" +
            "F1F0F21CECCA271F8F0F8B6B171F1A2853E0B3C907B215ECF168F790BE654F457C19A39EB2727AF12878ACB3435F1A6C57E9" +
            "A4A42411131363E2053FE0732E99AAF5789B3152E2E3E36DAF8785DABFE2FD1660005C9376CA62436F710000000049454E44" +
            "AE426082";

}
