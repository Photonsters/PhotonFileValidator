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

import java.util.Vector;


public class BaseElement implements ITransformable {

    public Vector<ITransformable> iTransformables;

    public BaseElement(){
		iTransformables =new Vector<>();
	}

    public BaseElement(Vector<ITransformable> iTransformables){
		this.iTransformables = iTransformables;
	}

    public void add(ITransformable ITransformable){
		iTransformables.add(ITransformable);
	}

    public void scale(double factor) {
        for (Object transformable : iTransformables) {
            ((ITransformable) transformable).scale(factor);
        }
    }

    public void scale(Vertex vector) {
        for (ITransformable iTransformable : iTransformables) {
            iTransformable.scale(vector);
        }
    }

    public void rotate(RotationBaseMatrix rotationMatrix) {
        for (ITransformable iTransformable : iTransformables) {
            iTransformable.rotate(rotationMatrix);
        }
    }

    public void translate(Vertex vector) {
        for (ITransformable iTransformable : iTransformables) {
            iTransformable.translate(vector);
        }
    }

    public void setColor(int r,int g,int b){
        for (ITransformable iTransformable : iTransformables) {
            iTransformable.setColor(r, g, b);
        }
    }

}
