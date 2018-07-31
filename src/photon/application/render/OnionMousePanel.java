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

import photon.application.render.storage.RotationBaseMatrix;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class OnionMousePanel extends JPanel implements MouseMotionListener, MouseListener{
    private OnionPanel onionPanel;

    private double totalScale = 2;

    private Point startPoint; //used to save the previous point when the mouse is draged
    private Point endPoint; //used to save the last point when the mouse is draged

    public OnionMousePanel(OnionPanel onionPanel) {
        this.onionPanel = onionPanel;
        onionPanel.addMouseListener(this);
        onionPanel.addMouseMotionListener(this);
    }



    private void rotateFromDrag(){
        double angleMultiplyFactor=0.02;
        double angleY=angleMultiplyFactor*(endPoint.getX()-startPoint.getX());
        double angleX=angleMultiplyFactor*(endPoint.getY()-startPoint.getY());
        onionPanel.rotate(new RotationBaseMatrix(-angleX, angleY, 0));
    }

    private void scaleFromDrag(){
        double scaleMultiplyFactor=0.01;
        double scaleFactor=scaleMultiplyFactor*(endPoint.getY()-startPoint.getY());

        if (scaleFactor>0) {
            if (totalScale*(1+scaleFactor)<2) {
                onionPanel.scale(1+scaleFactor);
                totalScale = totalScale*(1+scaleFactor);
            }
        } else {
            if (totalScale*(1+scaleFactor)>0.25) {
                onionPanel.scale(1 + scaleFactor);
                totalScale = totalScale*(1+scaleFactor);
            }
        }
    }

    public void mousePressed(MouseEvent event) {
        startPoint=event.getPoint();
    }

    public void mouseDragged(MouseEvent event) {
        endPoint=event.getPoint();
        
        if((event.getModifiers() & InputEvent.BUTTON1_MASK) == InputEvent.BUTTON1_MASK){
            rotateFromDrag();
        }else if((event.getModifiers() & InputEvent.BUTTON3_MASK) == InputEvent.BUTTON3_MASK){
            scaleFromDrag();
        }

        startPoint=endPoint;
    }

    public void mouseMoved(MouseEvent event) {
        //not needed
    }

    public void mouseClicked(MouseEvent event) {
        //not needed
    }

    public void mouseEntered(MouseEvent event) {
        //not needed
    }

    public void mouseExited(MouseEvent event) {
        //not needed
    }

    public void mouseReleased(MouseEvent event) {
        //not needed
    }
}
