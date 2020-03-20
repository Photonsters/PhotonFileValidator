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

package photon.file.parts;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * by bn on 01/07/2018.
 */
public class PhotonFilePreview {
    //private static final Logger LOGGER = Logger.getLogger(PhotonFilePreview.class.getName());
    private static final int MAX_RUN_LENGTH = 4095;
    private static final int MAX_RUN_BYTE1 = -2;
    private static final int MAX_RUN_BYTE2 = 63;
    private int resolutionX;
    private int resolutionY;
    private int imageAddress;
    private int dataSize;

    private byte[] rawImageData;

    private int[] imageData;

    private int p1;
    private int p2;
    private int p3;
    private int p4;

    /*
    static public void setupLogging() throws IOException {
        if( logging_setup ) return;
        LOGGER.setLevel(Level.INFO);
        FileHandler fh = new FileHandler("output.log");
        SimpleFormatter sf = new SimpleFormatter();
        fh.setFormatter(sf);
        LOGGER.addHandler(fh);
        logging_setup = true;
    }*/

    public PhotonFilePreview(int previewAddress, byte[] file) throws Exception {
        byte[] data = Arrays.copyOfRange(file, previewAddress, previewAddress + 32);
        PhotonInputStream ds = new PhotonInputStream(new ByteArrayInputStream(data));
        resolutionX = ds.readInt();
        resolutionY = ds.readInt();
        imageAddress = ds.readInt();
        dataSize = ds.readInt();
        p1 = ds.readInt();
        p2 = ds.readInt();
        p3 = ds.readInt();
        p4 = ds.readInt();

        rawImageData = Arrays.copyOfRange(file, imageAddress, imageAddress + dataSize);
        decodeImageData();
    }

    public PhotonFilePreview(InputStream input) throws IOException {
        BufferedImage img = ImageIO.read(input);
        resolutionX = img.getWidth();
        resolutionY = img.getHeight();
        BufferedImage rgbImg;
        if(img.getType() == BufferedImage.TYPE_INT_RGB)
        {
            rgbImg = img;
        } else {
            // wrong image format, convert it.
            rgbImg = new BufferedImage(resolutionX, resolutionY, BufferedImage.TYPE_INT_RGB);
            Graphics graphics = rgbImg.getGraphics();
            graphics.drawImage(img, 0, 0, null);
            graphics.dispose();
        }
        imageData = ((DataBufferInt)rgbImg.getRaster().getDataBuffer()).getData();
        encodeImageData();
        dataSize = rawImageData.length;

    }

    public PhotonFilePreview(BufferedImage image) {
        resolutionX = image.getWidth();
        resolutionY = image.getHeight();
        imageData = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();
        encodeImageData();
        dataSize = rawImageData.length;
    }

    /**
     * Expand the preview image back out to a buffered image.
     * @return the image
     */
    public BufferedImage getImage() {
       if( imageData == null ) decodeImageData();
       BufferedImage result = new BufferedImage(resolutionX, resolutionY, BufferedImage.TYPE_INT_RGB);
       WritableRaster raster = result.getRaster();
       raster.setDataElements(0,0,resolutionX,resolutionY, imageData);
       imageData = null;
       return result;
    }

    public void save(PhotonOutputStream os, int startAddress) throws Exception {
        os.writeInt(resolutionX);
        os.writeInt(resolutionY);
        os.writeInt(startAddress + 4+4+4+4 + 4+4+4+4);
        os.writeInt(dataSize);
        os.writeInt(p1);
        os.writeInt(p2);
        os.writeInt(p3);
        os.writeInt(p4);
        os.write(rawImageData, 0, dataSize);
    }

    public int getByteSize() {
        return 4+4+4+4 + 4+4+4+4 + dataSize;
    }

    private void decodeImageData() {
        imageData = new int[resolutionX * resolutionY];
        int d = 0;
        for (int i = 0; i < dataSize; i++) {
            int dot = rawImageData[i] & 0xFF | ((rawImageData[++i] & 0xFF) << 8);

            int color =   ((dot & 0xF800) << 8) | ((dot & 0x07C0) << 5) | ((dot & 0x001F) << 3);

//            int red = ((dot >> 11) & 0x1F) << 3;
//            int green = ((dot >> 6) & 0x1F) << 3;
//            int blue = (dot & 0x1F) << 3;
//            color = red<<16 | green<<8 | blue;

            int repeat = 1;
            if ((dot & 0x0020) == 0x0020) {
                repeat += rawImageData[++i] & 0xFF | ((rawImageData[++i] & 0x0F) << 8);
            }
            while (repeat > 0) {
                try {
                    imageData[d++] = color;
                } catch (ArrayIndexOutOfBoundsException e) {
                    throw new IllegalArgumentException("moo");
                }
                repeat--;
            }
        }
    }

    /**
     * Calculates a RLE block to add to a preview
     * @param colour The colour to add in standard 8bit RGB
     * @param run_Length how many pixels of this colour to add
     * @param index the index into the byte array of where to add the first value
     * @param output the byte array to write to
     * @return the index of the next pixel in the byte array.
     */
    private int addRun(int colour, int run_Length, int index, byte[] output) {
        // scheme is as follows: (yes, 5bit colour)
        // RRRRRGGGGGXBBBBB
        // If X is high, then next word is a run length with a max of 4094, stored like this:
        // 56789ABC....1234
        // If X is low, this is a single pixel.
        int blue = (colour & 0xF8) >> 3;
        int green = (colour & 0xF800 ) >> 5;
        int red = (colour & 0xF80000 ) >> 8;
        int dot = red | green | blue| (run_Length > 2 ? 0x20 : 0);
        byte dotL = (byte)(dot & 0xFF);
        byte dotR = (byte)((dot & 0xFF00) >> 8);

        if( run_Length == 1 ) {
            // just one
            output[index++] = dotL;
            output[index++] = dotR;
            return index;
        }

        if( run_Length == 2) {
            // weird edge case time! the photon doesn't bother doing RLE for runs of 2
            // it just outputs two runs of 1. idk why either.
            output[index++] = dotL;
            output[index++] = dotR;
            output[index++] = dotL;
            output[index++] = dotR;
            return index;
        }

        // as we have already implicitly got 1 in the colour word.
        run_Length--;
        int full_runs = run_Length / MAX_RUN_LENGTH;
        int remainder = run_Length % MAX_RUN_LENGTH;
        for(int i=0; i<full_runs; i++ ) {
            output[index++] = dotL;
            output[index++] = dotR;
            output[index++] = MAX_RUN_BYTE1;
            output[index++] = MAX_RUN_BYTE2;
        }
        if( remainder == 0 ) {
            // all done
            return index;
        }
        if( remainder == 1 ) {
            //edge case
            dotR ^= 0x20;
        }
        output[index++] = dotL;
        output[index++] = dotR;
        output[index++] = (byte) ((remainder & 0xFF));
        output[index++] = (byte) ((remainder & 0xF00) >> 8);

        return index;
    }

    private void encodeImageData() {
        // allocate for worst case scenario up front - 1 word per pixel
        byte[] result = new byte[2*resolutionX * resolutionY];
        int index = 0;
        int cur_pixel = imageData[0];
        int repeat = 1;
        // Genuinely start from 1 as we have set up above.
        for(int i = 1; i<imageData.length; i++) {
            if(imageData[i] == cur_pixel) {
                repeat++;
                continue;
            }
            index = addRun(cur_pixel, repeat, index, result);
            cur_pixel = imageData[i];
            repeat = 1;
        }
        // close it off
        index = addRun(cur_pixel, repeat, index, result);
        // Now we know how much we need.
        rawImageData = new byte[index];
        System.arraycopy(result, 0, rawImageData, 0, index);

    }

    public int getResolutionX() {
        return resolutionX;
    }

    public int getResolutionY() {
        return resolutionY;
    }

    public int[] getImageData() {
        if(imageData == null && rawImageData != null) decodeImageData();
        return imageData;
    }

    public void unLink() {
        rawImageData = null;
        imageData = null;
    }

}
