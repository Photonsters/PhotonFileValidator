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

import photon.application.render.elements.RenderAsTriangle;
import photon.application.render.elements.Triangle;
import photon.application.render.elements.Vertex;

/**
 * by bn on 18/07/2018.
 */
public class PhotonBuildPlate extends RenderAsTriangle {

    public PhotonBuildPlate(int width, int height, int plateHeight, int offset) {

        double z = offset - plateHeight;
        double zz = offset;

        double x1 = -1 * (width / 2);
        double x2 = (width / 2);
        double y1 = -1 * (height / 2);
        double y2 = height / 2;


        iTransformables.add(new Triangle(new Vertex(x1, y1, z), new Vertex(x1, y2, z), new Vertex(x2, y2, z)));
        iTransformables.add(new Triangle(new Vertex(x1, y1, z), new Vertex(x2, y1, z), new Vertex(x2, y2, z)));

        if (plateHeight>0) {
            iTransformables.add(new Triangle(new Vertex(x1, y1, z), new Vertex(x1, y2, z), new Vertex(x1, y2, zz)));
            iTransformables.add(new Triangle(new Vertex(x1, y1, z), new Vertex(x1, y1, zz), new Vertex(x1, y2, zz)));

            iTransformables.add(new Triangle(new Vertex(x1, y1, z), new Vertex(x2, y1, z), new Vertex(x2, y1, zz)));
            iTransformables.add(new Triangle(new Vertex(x1, y1, z), new Vertex(x1, y1, zz), new Vertex(x2, y1, zz)));

            iTransformables.add(new Triangle(new Vertex(x2, y1, z), new Vertex(x2, y2, z), new Vertex(x2, y2, zz)));
            iTransformables.add(new Triangle(new Vertex(x2, y1, z), new Vertex(x2, y1, zz), new Vertex(x2, y2, zz)));

            iTransformables.add(new Triangle(new Vertex(x1, y2, z), new Vertex(x2, y2, z), new Vertex(x2, y2, zz)));
            iTransformables.add(new Triangle(new Vertex(x1, y2, z), new Vertex(x1, y2, zz), new Vertex(x2, y2, zz)));

            iTransformables.add(new Triangle(new Vertex(x1, y1, zz), new Vertex(x1, y2, zz), new Vertex(x2, y2, zz)));
            iTransformables.add(new Triangle(new Vertex(x1, y1, zz), new Vertex(x2, y1, zz), new Vertex(x2, y2, zz)));
        }

    }
}
