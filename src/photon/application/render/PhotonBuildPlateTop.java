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
public class PhotonBuildPlateTop extends RenderAsTriangle {

    public PhotonBuildPlateTop() {
        int width = 256;
        int height = 144;

        double z = -105;
        double zz = -130;

        double x1 = -1 * (width / 2);
        double x2 = (width / 2);
        double y1 = -1 * (height / 2);
        double y2 = height / 2;

        double xx1 = x1 + ((width/5) *2);
        double xx2 = x2 - ((width/5) *2);
        double yy1 = y1 + ((height/5) *2);
        double yy2 = y2 - ((height/5) *2);

        iTransformables.add(new Triangle(new Vertex(xx1, yy1, zz), new Vertex(xx1, yy2, zz), new Vertex(xx2, yy2, zz)));
        iTransformables.add(new Triangle(new Vertex(xx1, yy1, zz), new Vertex(xx2, yy1, zz), new Vertex(xx2, yy2, zz)));



        iTransformables.add(new Triangle(new Vertex(x1, y1, z), new Vertex(x1, yy1, z), new Vertex(xx1, yy1, zz)));
        iTransformables.add(new Triangle(new Vertex(x1, y1, z), new Vertex(xx1, y1, z), new Vertex(xx1, yy1, zz)));


        iTransformables.add(new Triangle(new Vertex(x1, y2, z), new Vertex(xx1, y2, z), new Vertex(xx1, yy2, zz)));
        iTransformables.add(new Triangle(new Vertex(x1, y2, z), new Vertex(x1, yy2, z), new Vertex(xx1, yy2, zz)));

        iTransformables.add(new Triangle(new Vertex(x2, y1, z), new Vertex(xx2, y1, z), new Vertex(xx2, yy1, zz)));
        iTransformables.add(new Triangle(new Vertex(x2, y1, z), new Vertex(x2, yy1, z), new Vertex(xx2, yy1, zz)));

        iTransformables.add(new Triangle(new Vertex(x2, y2, z), new Vertex(x2, yy2, z), new Vertex(xx2, yy2, zz)));
        iTransformables.add(new Triangle(new Vertex(x2, y2, z), new Vertex(xx2, y2, z), new Vertex(xx2, yy2, zz)));


        iTransformables.add(new Triangle(new Vertex(x1, yy1, z), new Vertex(x1, yy2, z), new Vertex(xx1, yy2, zz)));
        iTransformables.add(new Triangle(new Vertex(x1, yy1, z), new Vertex(xx1, yy1, zz), new Vertex(xx1, yy2, zz)));

        iTransformables.add(new Triangle(new Vertex(xx1, y1, z), new Vertex(xx1, yy1, zz), new Vertex(xx2, yy1, zz)));
        iTransformables.add(new Triangle(new Vertex(xx1, y1, z), new Vertex(xx2, y1, z), new Vertex(xx2, yy1, zz)));

        iTransformables.add(new Triangle(new Vertex(xx2, yy1, zz), new Vertex(xx2, yy2, zz), new Vertex(x2, yy2, z)));
        iTransformables.add(new Triangle(new Vertex(xx2, yy1, zz), new Vertex(x2, yy1, z), new Vertex(x2, yy2, z)));

        iTransformables.add(new Triangle(new Vertex(xx1, y2, z), new Vertex(xx1, yy2, zz), new Vertex(xx2, yy2, zz)));
        iTransformables.add(new Triangle(new Vertex(xx1, y2, z), new Vertex(xx2, y2, z), new Vertex(xx2, yy2, zz)));


        iTransformables.add(new Triangle(new Vertex(x1, y1, z), new Vertex(x1, y2, z), new Vertex(x2, y2, z)));
        iTransformables.add(new Triangle(new Vertex(x1, y1, z), new Vertex(x2, y1, z), new Vertex(x2, y2, z)));

    }
}
