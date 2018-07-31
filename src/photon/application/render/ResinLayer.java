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

package photon.application.render;

import photon.application.render.elements.RenderAsSegment;
import photon.application.render.elements.Segment;
import photon.application.render.elements.Vertex;
import photon.file.parts.PhotonFileLayer;
import photon.file.parts.PhotonLayer;
import photon.file.parts.PhotonLine;
import photon.file.parts.PhotonRow;

import java.awt.*;
import java.util.ArrayList;

/**
 * by bn on 18/07/2018.
 */
public class ResinLayer extends RenderAsSegment {

    public ResinLayer(int width, int height, PhotonFileLayer fileLayer, byte type, int offset) {
        double z = offset;
        double x1 = -1 * (width/2);
        double y1 = -1 * (height/2);

        int black = Color.black.getRGB();
        int red = Color.decode("#FF0000").getRGB();

        ArrayList<PhotonRow> rows = fileLayer.getRows();
        if (rows != null) {
            int columnNumber = 0;
            for (PhotonRow row : rows) {
                int i = 0;
                for (PhotonLine line : row.lines) {
                    int end = i + line.length;
                    if (line.color.getRGB() != black) {
                        if (type== PhotonLayer.OFF || (type==PhotonLayer.SUPPORTED && line.color.getRGB() != red) || (type==PhotonLayer.ISLAND && line.color.getRGB() == red)) {
                            iTransformables.add(new Segment(new Vertex(x1 + columnNumber, y1 + i, z), new Vertex(x1 + columnNumber, y1 + end, z)));
                        }
                    }
                    i = end;
                }
                columnNumber++;
            }
        }


    }

}
