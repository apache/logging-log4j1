/*
 * Copyright 1999,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.log4j.chainsaw;

import java.awt.*;

import javax.swing.*;


/**
 * An Icon that is a Nice arrow to be used for displaying which
 * Column is being used for sorting.
 * @author Claude Duguay
*/
public class SortArrowIcon implements Icon {
  public static final int NONE = 0;
  public static final int DECENDING = 1;
  public static final int ASCENDING = 2;
  protected int direction;
  protected int width = 8;
  protected int height = 8;

  public SortArrowIcon(int direction) {
    this.direction = direction;
  }

  public int getIconWidth() {
    return width;
  }

  public int getIconHeight() {
    return height;
  }

  public void paintIcon(Component c, Graphics g, int x, int y) {
    Color bg = c.getBackground();
    Color light = bg.brighter();
    Color shade = bg.darker();

    int w = width;
    int h = height;
    int m = w / 2;

    if (direction == ASCENDING) {
      g.setColor(shade);
      g.drawLine(x, y, x + w, y);
      g.drawLine(x, y, x + m, y + h);
      g.setColor(light);
      g.drawLine(x + w, y, x + m, y + h);
    }

    if (direction == DECENDING) {
      g.setColor(shade);
      g.drawLine(x + m, y, x, y + h);
      g.setColor(light);
      g.drawLine(x, y + h, x + w, y + h);
      g.drawLine(x + m, y, x + w, y + h);
    }
  }
}
