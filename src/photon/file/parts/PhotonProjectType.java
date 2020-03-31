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
 * by bn on 30/06/2018.
 */
public enum PhotonProjectType {
    cast (0, "Cast"),
    lcdMirror (1, "LCD_mirror"),
    ;

    int projectID;
    String text;

    PhotonProjectType(int projectID, String text) {
        this.projectID = projectID;
        this.text = text;
    }

    public static PhotonProjectType find(int typeID) {
        for(PhotonProjectType photonProjectType : values()) {
            if (photonProjectType.projectID == typeID) {
                return photonProjectType;
            }
        }
        return lcdMirror;
    }

    public static PhotonProjectType find(String typeText) {
        for(PhotonProjectType photonProjectType : values()) {
            if (photonProjectType.text.equalsIgnoreCase(typeText)) {
                return photonProjectType;
            }
        }
        return lcdMirror;
    }

    public int getProjectID() {
        return projectID;
    }

    @Override
    public String toString() {
        return text;
    }
}
