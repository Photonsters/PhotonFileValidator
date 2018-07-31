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

import photon.application.render.storage.RotationBaseMatrix;
import photon.application.render.interfaces.ITransformable;

public class Vertex implements ITransformable {
	public double x;
	public double y;
	public double z;
	public double w;

    /**
     * Creates a 0 vector
     */
    public Vertex(){
		x=0;
		y=0;
		z=0;
		w=1;
	}

    /**
     * Creates a vector with the parameter values
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     */
    public Vertex(double x,double y,double z){
		this.x=x;
		this.y=y;
		this.z=z;
		this.w=1;
	}

    /**
     * Creates a vector with the parameter values
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     * @param w W coordinate (not actually used as a coordinate)
     */
	public Vertex(double x,double y,double z,double w){
		this.x=x;
		this.y=y;
		this.z=z;
		this.w=w;
	}

    /**
     * @see ITransformable#scale(double)
     * @param factor scale factor
     */
    public void scale(double factor) {
		this.x*=factor;
		this.y*=factor;
		this.z*=factor;
	}

    /**
     * @see ITransformable#scale(Vertex)
     * @param vector Vertex value
     */
    public void scale(Vertex vector) {
		this.x*=vector.x;
		this.y*=vector.y;
		this.z*=vector.z;
	}

    /**
     * @see ITransformable#rotate(RotationBaseMatrix)
     * @param rotationMatrix rotation matrix
     */
    public void rotate(RotationBaseMatrix rotationMatrix) {
		rotationMatrix.multiply(this);
	}

    /**
     * @see ITransformable#translate(Vertex)
     * @param vector translation vector
     */
    public void translate(Vertex vector) {
		this.x+=vector.x;
		this.y+=vector.y;
		this.z+=vector.z;
	}

    /**
     * @see ITransformable#setColor(int, int, int)
     * @param r red (0..255)
     * @param g green (0..255)
     * @param b blue (0..255)
     */
    public void setColor(int r, int g, int b) {
        //not implemented for the moment
        //will be useful when implementing phong shading
    }

}
