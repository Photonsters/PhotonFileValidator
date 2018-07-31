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

package photon.application.render.storage;

import photon.application.render.elements.BaseElement;
import photon.application.render.elements.Segment;
import photon.application.render.elements.Triangle;
import photon.application.render.elements.Vertex;
import photon.application.render.interfaces.IRenderer;
import photon.application.render.utils.VectorUtil;
import photon.application.render.elements.RenderAsSegment;
import photon.application.render.elements.World;
import photon.application.render.utils.ZBufferUtil;

import java.awt.*;
import java.awt.image.ImageObserver;

public class Renderer implements IRenderer {

    //perspective division constant
    private static final double D = 400;

    /**
     * Renders segment based objects
     *
     * @param baseElement object to be rendered
     * @param view        off screen image
     * @param observer    image observer object
     * @param zbuffer     z depth buffer
     */
    private void renderSegmentBased(BaseElement baseElement, Image view, ImageObserver observer, int[][] zbuffer) {

        Graphics gr = view.getGraphics(); //obtain graphic object from off screen image

        int w = view.getWidth(observer); //obtain image width
        int h = view.getHeight(observer); //obtain image height

        for (int i = 0; baseElement != null && i < baseElement.iTransformables.size(); i++) {
            Segment segment = (Segment) baseElement.iTransformables.get(i);

            //sets the color of the graphic object used to draw
            gr.setColor(new Color(segment.r, segment.g, segment.b));

            //calculate perspective transformation for all vertexes
            //and for x,y and z coordinates
            int x1, x2;
            x1 = (int) (segment.start.x / (1 + segment.start.z / D)) + w / 2;
            x2 = (int) (segment.end.x / (1 + segment.end.z / D)) + w / 2;

            int y1, y2;
            y1 = (int) (segment.start.y / (1 + segment.start.z / D)) + h / 2;
            y2 = (int) (segment.end.y / (1 + segment.end.z / D)) + h / 2;

            int z1, z2;
            z1 = (int) segment.start.z;
            z2 = (int) segment.end.z;

            //draw line
            ZBufferUtil.drawLine(gr, x1, y1, z1, x2, y2, z2, zbuffer);

        }


    }

    /**
     * Renders triangle based objects
     *
     * @param baseElement object to be rendered
     * @param view        off screen image
     * @param observer    image observer object
     * @param zbuffer     z depth buffer
     */
    private void renderTriangleBased(BaseElement baseElement, Image view, ImageObserver observer, int[][] zbuffer) {

        Graphics gr = view.getGraphics(); //obtain graphic object from offscreen image

        int w = view.getWidth(observer); //obtain image width
        int h = view.getHeight(observer); //obtain image height

        for (int i = 0; i < baseElement.iTransformables.size(); i++) {
            Triangle tri = (Triangle) baseElement.iTransformables.get(i);

            //calculate perspective transformation for all vertexes
            //and for x,y and z coordinates
            int[] x = new int[3];
            x[0] = (int) (tri.v1.x / (1 + tri.v1.z / D)) + w / 2;
            x[1] = (int) (tri.v2.x / (1 + tri.v2.z / D)) + w / 2;
            x[2] = (int) (tri.v3.x / (1 + tri.v3.z / D)) + w / 2;

            int[] y = new int[3];
            y[0] = (int) (tri.v1.y / (1 + tri.v1.z / D)) + h / 2;
            y[1] = (int) (tri.v2.y / (1 + tri.v2.z / D)) + h / 2;
            y[2] = (int) (tri.v3.y / (1 + tri.v3.z / D)) + h / 2;

            int[] z = new int[3];
            z[0] = (int) tri.v1.z;
            z[1] = (int) tri.v2.z;
            z[2] = (int) tri.v3.z;

            //obtains the normal vector of the triangle
            Vertex normale = VectorUtil.calculateNormal(tri);

			/*
            calculates cos between triangle normal and light direction vector
			and calculate color intensity in function of cos value
			 */
            double cos = VectorUtil.calculateAngleCosBetweenVectors(normale, new Vertex(Math.cos(Math.PI / 2), Math.cos(Math.PI / 2) / 2, Math.cos(Math.PI / 2)));
            int r, g, b;
            cos = Math.abs(cos);
            r = (int) (cos * tri.r);
            g = (int) (cos * tri.g);
            b = (int) (cos * tri.b);

            //set graphic color
            gr.setColor(new Color(r, g, b));

            //if filled flag is true render filled triangles
            ZBufferUtil.fillTriangle(gr, x, y, z, zbuffer);

        }
    }

    /**
     * @param world    world object containing the set of 3d objects
     * @param view     offscreen image to render the world in
     * @param observer image observer object necessary to get width and height of the
     */
    public void render(World world, Image view, ImageObserver observer) {

        int w = view.getWidth(observer); //obtain image width
        int h = view.getHeight(observer); //obtain image height

        int[][] zbuffer = new int[w][h];

        //initialize zbuffer with the max depth value
        for (int k = 0; k < w; k++) {
            for (int n = 0; n < h; n++) {
                zbuffer[k][n] = 0xFFFE;
            }
        }

        for (int i = 0; i < world.iTransformables.size(); i++) {
            BaseElement baseElement = (BaseElement) world.iTransformables.get(i);

            //render the object appropriately according to their type
            if (baseElement instanceof RenderAsSegment) {
                renderSegmentBased(baseElement, view, observer, zbuffer);
            } else {
                renderTriangleBased(baseElement, view, observer, zbuffer);
            }
        }
    }
}
