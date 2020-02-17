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

package photon.file.ui;

import photon.file.parts.PhotonFilePreview;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 *  by bn on 03/07/2018.
 */
public class PhotonPreviewImage extends JPanel {
    private BufferedImage image;

    public PhotonPreviewImage(int width, int height) {
        if (width>0 && height>0) {
            image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            setPreferredSize(new Dimension(width, height));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (image!=null) {
            g.drawImage(image, 0, 0, null);
        }
    }

    public void reInit(int width, int height) {
        if (width>0 && height>0) {
            image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            setPreferredSize(new Dimension(width, height));
        }
    }

    public void drawImage(PhotonFilePreview preview) {
        if (image!=null && preview.getResolutionX()>0 && preview.getResolutionY()>0) {
            image.getRaster().setDataElements(0, 0, preview.getResolutionX(), preview.getResolutionY(), preview.getImageData());
        }
    }

}
