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

public enum PhotonAALevel {
    NoAntiAliase (1,1, "No Anti Aliase"),
    AA2 (2, 2, "2 Layers AA"),
    AA4 (3, 4, "4 Layers AA"),
    AA8 (4, 8, "8 Layers AA"),
    AA16 (5, 16, "16 Layers AA"),

    ;

    public int index;
    public int levels;
    public String name;

    PhotonAALevel(int index, int levels, String name) {
        this.index = index;
        this.levels = levels;
        this.name = name;
    }

    public static PhotonAALevel find(int id) {
        for(PhotonAALevel photonAALevel : values()) {
            if (photonAALevel.index==id) {
                return photonAALevel;
            }
        }
        return NoAntiAliase;
    }

    public static PhotonAALevel findByLevel(int id) {
        for(PhotonAALevel photonAALevel : values()) {
            if (photonAALevel.levels==id) {
                return photonAALevel;
            }
        }
        return NoAntiAliase;
    }
}
