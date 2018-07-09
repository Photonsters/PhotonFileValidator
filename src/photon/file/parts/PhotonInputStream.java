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

/**
 *  by bn on 01/07/2018.
 */

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class PhotonInputStream extends InputStream implements DataInput {
    private DataInputStream dataInputStream;
    private InputStream inputStream;
    private byte byteBuffer[];

    public PhotonInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
        dataInputStream = new DataInputStream(inputStream);
        byteBuffer = new byte[8]; // Largest data type is 64-bits (8 bytes)
    }

    @Override
    public int read() throws IOException {
        return inputStream.read();
    }

    @Override
    public final int read(byte[] bytes, int offset, int readLen) throws IOException {
        return inputStream.read(bytes, offset, readLen);
    }

    @Override
    public final void readFully(byte[] bytes) throws IOException {
        dataInputStream.readFully(bytes, 0, bytes.length);
    }

    @Override
    public final void readFully(byte[] bytes, int offset, int readLen) throws IOException {
        dataInputStream.readFully(bytes, offset, readLen);
    }

    @Override
    public final int skipBytes(int n) throws IOException {
        return dataInputStream.skipBytes(n);
    }

    @Override
    public final boolean readBoolean() throws IOException {
        return dataInputStream.readBoolean();
    }

    @Override
    public final byte readByte() throws IOException {
        return dataInputStream.readByte();
    }

    @Override
    public final int readUnsignedByte() throws IOException {
        return dataInputStream.readUnsignedByte();
    }

    @Override
    public final short readShort() throws IOException {
        dataInputStream.readFully(byteBuffer, 0, 2);
        return (short)((byteBuffer[1] & 0xff) << 8 | (byteBuffer[0] & 0xff));
    }

    @Override
    public final int readUnsignedShort() throws IOException {
        dataInputStream.readFully(byteBuffer, 0, 2);
        return ((byteBuffer[1] & 0xff) << 8 | (byteBuffer[0] & 0xff));
    }

    @Override
    public final char readChar() throws IOException {
        dataInputStream.readFully(byteBuffer, 0, 2);
        return (char)((byteBuffer[1] & 0xff) << 8 | (byteBuffer[0] & 0xff));
    }

    @Override
    public final int readInt() throws IOException {
        dataInputStream.readFully(byteBuffer, 0, 4);
        return (byteBuffer[3]) << 24 | (byteBuffer[2] & 0xff) << 16 |
                (byteBuffer[1] & 0xff) << 8 | (byteBuffer[0] & 0xff);
    }

    @Override
    public final long readLong() throws IOException {
        dataInputStream.readFully(byteBuffer, 0, 8);
        return (long)(byteBuffer[7]) << 56 | (long)(byteBuffer[6]&0xff) << 48 |
                (long)(byteBuffer[5] & 0xff) << 40 | (long)(byteBuffer[4] & 0xff) << 32 |
                (long)(byteBuffer[3] & 0xff) << 24 | (long)(byteBuffer[2] & 0xff) << 16 |
                (long)(byteBuffer[1] & 0xff) <<  8 | (long)(byteBuffer[0] & 0xff);
    }

    @Override
    public final float readFloat() throws IOException {
        return Float.intBitsToFloat(readInt());
    }

    @Override
    public final double readDouble() throws IOException {
        return Double.longBitsToDouble(readLong());
    }

    @Deprecated
    @Override
    public final String readLine() throws IOException {
        return dataInputStream.readLine();
    }

    @Override
    public final String readUTF() throws IOException {
        return dataInputStream.readUTF();
    }

    @Override
    public int available() throws IOException {
        return dataInputStream.available();
    }

    @Override
    public final void close() throws IOException {
        dataInputStream.close();
    }

}
