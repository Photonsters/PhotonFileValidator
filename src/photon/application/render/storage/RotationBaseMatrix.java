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


public class RotationBaseMatrix extends BaseMatrix {
    /**
     * This constructor creates a complex 3D rotation matrix which rotates
     * around x,y and z by x_angle,y_angle and z_angle
     * by multiplying 3 elementary rotation matrices
     * @param x_angle x angle expressed in radians
     * @param y_angle y angle expressed in radians
     * @param z_angle z angle expressed in radians
     */
    public RotationBaseMatrix(double x_angle, double y_angle, double z_angle){
		//initializes the X rotation matrix
        super(1,                  0,                 0,       0,
			  0,  Math.cos(x_angle), Math.sin(x_angle),       0,
			  0, -Math.sin(x_angle), Math.cos(x_angle),       0,
			  0,                  0,                 0,       1);

        //multiplies with the Y rotation matrix
        multiply(
                new BaseMatrix(
                        Math.cos(y_angle),  0,  -Math.sin(y_angle),   0,
						0                ,  1,                   0,   0,
						Math.sin(y_angle),  0,   Math.cos(y_angle),   0,
						0                ,  0,                   0,   1)
        );

        //multiplies with the Z rotation matrix
        multiply(
                new BaseMatrix(
                         Math.cos(z_angle),  Math.sin(z_angle),   0,  0,
						-Math.sin(z_angle),  Math.cos(z_angle),   0,  0,
						                 0,                  0,   1,  0,
						                 0,                  0,   0,  1)
        );
	}
}
