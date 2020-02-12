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

import java.io.ByteArrayInputStream;
import java.util.Arrays;

/**
 * by bn on 01/07/2018.
 */
public class PhotonFileMachineInfo {
    private int u1, u2, u3, u4, u5, u6, u7;
    
    private int machineNameAddress;
    private int machineNameSize;
    private byte[] machineName = {};

    private int u8, u9, u10, u11, u12, u13, u14, u15, u16, u17;
    
    private int infoByteSize;

    public PhotonFileMachineInfo(int address, int byteSize, byte[] file) throws Exception {
    	
    	this.infoByteSize = byteSize;
    	
    	if (byteSize > 0) {
	        byte[] data = Arrays.copyOfRange(file, address, address + byteSize);
	        
	        try (PhotonInputStream ds = new PhotonInputStream(new ByteArrayInputStream(data))) {
	        	u1 = ds.readInt();
	        	u2 = ds.readInt();
	        	u3 = ds.readInt();
	        	u4 = ds.readInt();
	        	u5 = ds.readInt();
	        	u6 = ds.readInt();
	        	u7 = ds.readInt();
	        	
	        	machineNameAddress = ds.readInt();
	        	machineNameSize = ds.readInt();
	        	
	        	u8 = ds.readInt();
	        	u9 = ds.readInt();
	        	u10 = ds.readInt();
	        	u11 = ds.readInt();
	        	u12 = ds.readInt();
	        	u13 = ds.readInt();
	        	u14 = ds.readInt();
	        	u15 = ds.readInt();
	        	u16 = ds.readInt();
	        	u17 = ds.readInt();
	        	
	        }
	
	        machineName = Arrays.copyOfRange(file, machineNameAddress, machineNameAddress + machineNameSize);
    	}
    }

    public void save(PhotonOutputStream os, int startAddress) throws Exception {
    	if (infoByteSize > 0) {
	    	os.writeInt(u1);
	    	os.writeInt(u2);
	    	os.writeInt(u3);
	    	os.writeInt(u4);
	    	os.writeInt(u5);
	    	os.writeInt(u6);
	    	os.writeInt(u7);
	    	os.writeInt(startAddress + infoByteSize);
	    	os.writeInt(machineName.length);
	    	os.writeInt(u8);
	    	os.writeInt(u9);
	    	os.writeInt(u10);
	    	os.writeInt(u11);
	    	os.writeInt(u12);
	    	os.writeInt(u13);
	    	os.writeInt(u14);
	    	os.writeInt(u15);
	    	os.writeInt(u16);
	    	os.writeInt(u17);
	    	os.write(machineName);
    	}
    }

    public int getByteSize() {
        return infoByteSize + machineName.length;
    }


    public void unLink() {
    }

}
