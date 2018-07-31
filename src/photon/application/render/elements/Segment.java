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

public class Segment implements ITransformable {

    public Vertex start; //start point
	public Vertex end; //end point
    public int r=0;   //red color component
    public int g=255; //green color component
    public int b=0;   //blue color component

    /**
     * Crates a new Segment with start point "start" and end point "end"
     * @param start start point
     * @param end end point
     */
    public Segment(Vertex start,Vertex end){
		this.start=start;
		this.end=end;
	}

    /**
     * @see ITransformable#scale(double)
     * @param factor scale factor
     */
    public void scale(double factor) {
		start.scale(factor);
		end.scale(factor);
	}

    /**
     * @see ITransformable#scale(Vertex)
     * @param vector Vertex value
     */
    public void scale(Vertex vector) {
		start.scale(vector);
		end.scale(vector);
	}

    /**
     * @see ITransformable#rotate(RotationBaseMatrix)
     * @param rotationMatrix rotation matrix
     */
    public void rotate(RotationBaseMatrix rotationMatrix) {
		start.rotate(rotationMatrix);
		end.rotate(rotationMatrix);
	}

    /**
     * @see ITransformable#translate(Vertex)
     * @param vector translation vector
     */
    public void translate(Vertex vector) {
		start.translate(vector);
		end.translate(vector);
	}

    /**
     * @see ITransformable#setColor(int, int, int)
     * @param r red (0..255)
     * @param g green (0..255)
     * @param b blue (0..255)
     */
    public void setColor(int r, int g, int b) {
        this.r=r;
        this.g=g;
        this.b=b;
    }
}
