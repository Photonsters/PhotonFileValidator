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

import photon.file.SlicedFileHeader;

import java.io.ByteArrayInputStream;
import java.util.Arrays;

/**
 * by bn on 01/07/2018.
 */
public class PhotonFileMachineInfo {
	/**
	 * Check if the header has all the fields required for a machineinfo block
	 * @param header to check
	 * @return true iff the header has all the required fields.
	 */
	static public boolean hasMachineInfo(SlicedFileHeader header) {
		return header.has(EParameter.machineInfoSize) && header.has(EParameter.machineName);
	}

    static public void initializeMachineInfo(String machineName, int size, SlicedFileHeader header) {
		header.put(EParameter.machineName, machineName);
		header.put(EParameter.machineInfoSize, size);
	}

    static public void initializeMachineInfo(int address, int byteSize, byte[] file, SlicedFileHeader header) throws Exception {
		int machineNameAddress, machineNameSize;
		header.put(EParameter.machineInfoSize, byteSize);

    	if (byteSize > 0) {
	        byte[] data = Arrays.copyOfRange(file, address, address + byteSize);
	        
	        try (PhotonInputStream ds = new PhotonInputStream(new ByteArrayInputStream(data))) {
	        	for( int i=0; i<7; i++ ) ds.readInt();

	        	machineNameAddress = ds.readInt();
	        	machineNameSize = ds.readInt();
	        }
	
	        byte[] machineName = Arrays.copyOfRange(file, machineNameAddress, machineNameAddress + machineNameSize);
			header.put(EParameter.machineName, new String(machineName));
    	}
    }

    static public void save(PhotonOutputStream os, int startAddress, SlicedFileHeader header) throws Exception {
    	// TODO:: VALIDATE
    	int infoByteSize = header.getInt(EParameter.machineInfoSize);
    	if (infoByteSize > 0) {
    		byte[] machineName = header.getString(EParameter.machineName).getBytes();
    		for(int i=0; i<7; i++) os.writeInt(0);
	    	os.writeInt(startAddress + infoByteSize);
	    	os.writeInt(machineName.length);
			for(int i=0; i<10; i++) os.writeInt(0);
			os.write(machineName);
    	}
    }

    static public int getByteSize(SlicedFileHeader header)
	{
        return header.getInt(EParameter.machineInfoSize) + header.getString(EParameter.machineName).getBytes().length;
    }
}
