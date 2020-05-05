package images;

import photon.file.parts.PhotonFileLayer;

import java.util.Arrays;

public class BinaryImage {

    private int width;
    private int ox;
    private int height;
    private int oy;
    private int stride;
    private int start;

    private byte[] data;

    public static BinaryImage from(PhotonFileLayer fileLayer) {
        return BinaryImage.from(fileLayer, fileLayer.getWidth(), 10, fileLayer.getHeight(), 10);
    }

    public static void fromTo(PhotonFileLayer fileLayer) {
        byte[] unpacked = BinaryImage.unpackPhotonRLE(fileLayer.getImageData(), fileLayer.getWidth() * fileLayer.getHeight());
        byte[] packed = BinaryImage.packPhotonRLE(unpacked);
        fileLayer.updateImageData(packed);
    }
    public static BinaryImage from(PhotonFileLayer fileLayer, int width, int ox, int height, int oy) {
        byte[] unpackedImage = BinaryImage.unpackPhotonRLE(fileLayer.getImageData(), fileLayer.getWidth() * fileLayer.getHeight());

        BinaryImage img = new BinaryImage(width, ox, height, oy);
        BinaryImage.bltIn(unpackedImage, fileLayer.getWidth(), fileLayer.getHeight(), img.data, img.stride, img.ox, img.oy);
        return img;
    }

    public void to(PhotonFileLayer fileLayer) {
        byte[] plainImage = new byte[width * height];
        BinaryImage.bltOut(data, stride, ox, oy, height, plainImage, width);
        byte[] packedImage = BinaryImage.packPhotonRLE(plainImage);
        fileLayer.updateImageData(packedImage);
    }

    private BinaryImage(int width, int ox, int height, int oy) {
        this.width = width;
        this.ox = ox;
        this.height = height;
        this.oy = oy;
        this.stride = width + 2 * ox;
        this.start = oy;
        this.data = new byte[(height + 2 * oy) * stride];
    }

    public void erode(int size, boolean useSquare) {

        if (size > 0) {
            int ptr = ox + oy * stride;

            byte[] dst = new byte[data.length];

            int kStride = size * 2 + 1;
            int kSize = kStride * kStride;
            int[] kernel = new int[kSize];
            for (int x = -size; x <= size; ++x) {
                for (int y = -size; y <= size; ++y) {
                    if (useSquare || (x * x + y * y <= size * size)) {
                        kernel[x + size + (y + size) * kStride] = x + y * stride;
                    }
                }
            }

            /* show the kernel when debugging */
            /*
            StringBuilder b = new StringBuilder();
            for (int y = 0; y < kStride; ++y) {
                for (int x = 0; x < kStride; ++x) {
                    b.append(kernel[x + y * kStride] == 0 ? '-' : '*');
                }
                b.append('\n');
            }
            System.out.println(b.toString());
            */

            // optimization: compact kernel
            kernel = Arrays.stream(kernel).filter(k -> k != 0).toArray();
            kSize = kernel.length;

            int h = height;
            while (--h >= 0) {
                int w = width;
                while (--w >= 0) {
                    int kPtr = 0;
                    int kLen = kSize;
                    byte v = (byte) 0xff;
                    while (--kLen >= 0) {
                        v &= data[ptr + kernel[kPtr++]];
                        if (v == 0) break;
                    }
                    if (v != 0) {
                        dst[ptr] = v;
                    }
                    ++ptr;
                }
                ptr += 2 * ox;
            }
            data = dst;
        }
    }


    private static byte[] unpackPhotonRLE(byte[] imageData, int length) {
        byte[] dst = new byte[length];
        int src = 0;
        int ptr = 0;
        int srcLength = imageData.length;
        while (src < srcLength) {
            byte rle = imageData[src++];
            int span = rle & 0x7F;
            byte value = (byte) ((rle & 0x80) == 0x80 ? 0xff : 0);
            while (--span >= 0) {
                dst[ptr++] = value;
            }
        }
        return dst;
    }

    private static byte[] packPhotonRLE(byte[] imageData) {
        byte[] scratchPad = new byte[imageData.length];

        int length = imageData.length;
        int ptr = 0;
        int dst = 0;
        byte current = 0;
        byte next = 0;
        int span = 0;
        while (ptr < length) {
            while (ptr < length && (next = imageData[ptr++]) == current) {
                ++span;
            }
            if (span > 0) {
                dst = addPhotonRLE(scratchPad, dst, current == 0, span);
            }
            current = next;
            span = 1;
        }

        byte[] img = new byte[dst];
        System.arraycopy(scratchPad, 0, img, 0, dst);
        return img;

    }

    private static int addPhotonRLE(byte[] dst, int ptr, boolean off, int length) {

        while (length > 0) {
            int lineLength = length < 125 ? length : 125; // max storage length of 0x7D (125) ?? Why not 127?
            dst[ptr++] = (byte) ((off ? 0x00: 0x80) | (lineLength & 0x7f));
            length -= lineLength;
        }

        return ptr;
    }

    private static void bltIn(byte[] src, int srcStride, int srcHeight, byte[] dst, int dstStride, int ox, int oy) {
        int dstPtr = ox + oy * dstStride;
        int srcPtr = 0;
        while (--srcHeight >= 0) {
            System.arraycopy(src, srcPtr, dst, dstPtr, srcStride);
            srcPtr += srcStride;
            dstPtr += dstStride;
        }
    }

    private static void bltOut(byte[] src, int srcStride, int ox, int oy, int srcHeight, byte[] dst, int dstStride) {
        int srcPtr = ox + oy * srcStride;
        int dstPtr = 0;
        while (--srcHeight >= 0) {
            System.arraycopy(src, srcPtr, dst, dstPtr, dstStride);
            srcPtr += srcStride;
            dstPtr += dstStride;
        }
    }

}