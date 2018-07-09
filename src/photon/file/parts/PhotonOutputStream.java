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

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;


/**
 * by bn on 06/07/2018.
 */
public class PhotonOutputStream extends OutputStream implements DataOutput {
    private OutputStream outputStream;
    private DataOutputStream dataOutputStream;


    public PhotonOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
        dataOutputStream = new DataOutputStream(outputStream);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        outputStream.write(b, off, len);
    }

    @Override
    public void writeBoolean(boolean v) throws IOException {
        dataOutputStream.writeBoolean(v);
    }

    @Override
    public void writeByte(int v) throws IOException {
        dataOutputStream.writeByte(v);
    }

    @Deprecated
    @Override
    public void writeBytes(String s) throws IOException {
        dataOutputStream.writeBytes(s);
    }

    @Override
    public void writeChar(int v) throws IOException {
        writeShort(v);
    }

    @Override
    public void writeChars(String s) throws IOException {
        for (int i = 0; i < s.length(); i++) {
            writeChar(s.charAt(i));
        }
    }

    @Override
    public void writeDouble(double v) throws IOException {
        writeLong(Double.doubleToLongBits(v));
    }

    @Override
    public void writeFloat(float v) throws IOException {
        writeInt(Float.floatToIntBits(v));
    }

    @Override
    public void write(int b) throws IOException {
        writeInt(b);
    }

    @Override
    public void writeInt(int v) throws IOException {
        outputStream.write(0xFF & v);
        outputStream.write(0xFF & (v >> 8));
        outputStream.write(0xFF & (v >> 16));
        outputStream.write(0xFF & (v >> 24));
    }

    @Override
    public void writeLong(long v) throws IOException {
        byte[] bytes = longToBytes(Long.reverseBytes(v));
        write(bytes, 0, bytes.length);
    }

    private byte[] longToBytes(long l) {
        byte[] result = new byte[8];
        for (int i = 7; i >= 0; i--) {
            result[i] = (byte)(l & 0xFF);
            l >>= 8;
        }
        return result;
    }

    @Override
    public void writeShort(int v) throws IOException {
        outputStream.write(0xFF & v);
        outputStream.write(0xFF & (v >> 8));
    }

    @Override
    public void writeUTF(String str) throws IOException {
        dataOutputStream.writeUTF(str);
    }

    @Override
    public void close() throws IOException {
        outputStream.close();
    }


}
