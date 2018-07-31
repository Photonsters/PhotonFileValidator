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

package photon.application.render.elements;

import photon.application.render.interfaces.ITransformable;
import photon.application.render.storage.RotationBaseMatrix;

public class Triangle implements ITransformable {

    public Vertex v1; //first vertex
	public Vertex v2; //second vertex
	public Vertex v3; //third vertex
    public int r=255; //red color component
    public int g=255; //green color component
    public int b=255; //blue color component

    /**
     * Creates a new triangle having as vertexes those given as parameter
     * @param v1 first vertex
     * @param v2 second vertex
     * @param v3 third vertex
     */
    public Triangle(Vertex v1,Vertex v2,Vertex v3){
		this.v1=v1;
		this.v2=v2;
		this.v3=v3;
	}

    /**
     * @see ITransformable#scale(double)
     * @param factor scale factor
     */
    public void scale(double factor) {
		v1.scale(factor);
		v2.scale(factor);
		v3.scale(factor);
	}

    /**
     * @see ITransformable#scale(Vertex)
     * @param vector Vertex value
     */
    public void scale(Vertex vector) {
		v1.scale(vector);
		v2.scale(vector);
		v3.scale(vector);
	}

    /**
     * @see ITransformable#rotate(RotationBaseMatrix)
     * @param rotationMatrix rotation matrix
     */
	public void rotate(RotationBaseMatrix rotationMatrix) {
		v1.rotate(rotationMatrix);
		v2.rotate(rotationMatrix);
		v3.rotate(rotationMatrix);
	}

    /**
     * @see ITransformable#translate(Vertex)
     * @param vector translation vector
     */
	public void translate(Vertex vector) {
		v1.translate(vector);
		v2.translate(vector);
		v3.translate(vector);
	}

    /**
     * @see ITransformable#setColor(int, int, int)
     * @param r red (0..255)
     * @param g green (0..255)
     * @param b blue (0..255)
     */
    public void setColor(int r,int g,int b){
        this.r = r;
        this.g = g;
        this.b = b;
    }
}
