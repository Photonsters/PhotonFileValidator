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

package photon.application.render.interfaces;

import photon.application.render.elements.Vertex;
import photon.application.render.storage.RotationBaseMatrix;

public interface ITransformable {

    /**
     * This method scales all 3 dimensions by the same quantity
     * @param factor scale factor
     */
    void scale(double factor);

    /**
     * This method scales each dimension indipendently
     * x,y and z values of the Vertex object are used separately to scale
     * each dimension respectively
     * @param vector Vertex value
     */
    void scale(Vertex vector);

    /**
     * This method rotates the object multiplicating with rotation matrix
     * wich must be created and prepared previously
     * @param rotationMatrix rotation matrix
     */
    void rotate(RotationBaseMatrix rotationMatrix);

    /**
     * This method translates an object by the x,y,z values of the
     * vector in the x,y,z directions
     * @param vector translation vector
     */
    void translate(Vertex vector);

    /**
     * Sets the color of this transformable
     * @param r red (0..255)
     * @param g green (0..255)
     * @param b blue (0..255)
     */
    void setColor(int r,int g,int b);
}
