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

package photon.file.ui;

import javax.swing.*;

/**
 * by bn on 02/07/2018.
 */
public class ScrollUtil {

    private ScrollUtil() {
    }


    public static void scrollTo(JScrollPane c, ScrollPosition scrollPosition) {
        switch (scrollPosition) {
            case Top:
                c.getVerticalScrollBar().setValue(0);
                break;
            case VerticalCenter:
                if (c.getVerticalScrollBar()!=null) {
                    c.getVerticalScrollBar().setValue(c.getVerticalScrollBar().getMaximum());
                    c.getVerticalScrollBar().setValue(c.getVerticalScrollBar().getValue() / 2);
                    c.getVerticalScrollBar().setUnitIncrement(16);
                }
                break;
            case Bottom:
                c.getVerticalScrollBar().setValue(c.getVerticalScrollBar().getMaximum());
                break;

            case Left:
                c.getHorizontalScrollBar().setValue(0);
                break;
            case HorizontalCenter:
                if (c.getHorizontalScrollBar()!=null) {
                    c.getHorizontalScrollBar().setValue(c.getHorizontalScrollBar().getMaximum());
                    c.getHorizontalScrollBar().setValue(c.getHorizontalScrollBar().getValue() / 2);
                    c.getHorizontalScrollBar().setUnitIncrement(16);
                }
                break;
            case Right:
                c.getHorizontalScrollBar().setValue(c.getHorizontalScrollBar().getMaximum());
                break;
        }
    }

    public static void scroll(JScrollPane c, ScrollPosition scrollPosition) {
        switch (scrollPosition) {
            case Top:
                int val = c.getVerticalScrollBar().getValue();
                if (val>18) {
                    c.getVerticalScrollBar().setValue(val - 18);
                } else {
                    c.getVerticalScrollBar().setValue(0);
                }
                break;
            case Bottom:
                val = c.getVerticalScrollBar().getValue();
                if (val < (c.getVerticalScrollBar().getMaximum() - 18)) {
                    c.getVerticalScrollBar().setValue(val + 18);
                } else {
                    c.getVerticalScrollBar().setValue(c.getVerticalScrollBar().getMaximum());
                }
                break;

            case Left:
                val = c.getHorizontalScrollBar().getValue();
                if (val>18) {
                    c.getHorizontalScrollBar().setValue(val - 18);
                } else {
                    c.getHorizontalScrollBar().setValue(0);
                }
                break;
            case Right:
                val = c.getHorizontalScrollBar().getValue();
                if (val< (c.getHorizontalScrollBar().getMaximum() - 18)) {
                    c.getHorizontalScrollBar().setValue(val + 18);
                } else {
                    c.getHorizontalScrollBar().setValue(c.getHorizontalScrollBar().getMaximum());
                }
                break;
        }
    }
}
