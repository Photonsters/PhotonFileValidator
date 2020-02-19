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

import java.util.List;

public interface IFileHeader {

    String getInformation();
    int getNumberOfLayers();

    int getResolutionY();
    int getResolutionX();

    float getBuildAreaX();
    float getBuildAreaY();

    int getBottomLayers();
    void setBottomLayers(int bottomLayers);

    float getLayerHeight();

    float getExposureTimeSeconds();

    float getBottomExposureTimeSeconds();
    void setExposureBottomTimeSeconds(float exposureBottomTimeSeconds);

    void setExposureTimeSeconds(float exposureTimeSeconds);
    float getNormalExposure();

    float getOffTimeSeconds();
    void setOffTimeSeconds(float offTimeSeconds);

    int getPrintTimeSeconds();
    boolean isMirrored();

    boolean hasAA();
    int getAALevels();
    void setAALevels(int levels, List<PhotonFileLayer> layers);

    int getVersion();
    void setFileVersion(int i);

    int getByteSize();
    void unLink();
}
