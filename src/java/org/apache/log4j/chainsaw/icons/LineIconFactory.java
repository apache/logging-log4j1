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

package org.apache.log4j.chainsaw.icons;

import org.apache.log4j.helpers.LogLog;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.ImageIcon;


/**
 * A simple factory/facade for creating some of the standard Icons that are based
 * on line drawings
 * @author Paul Smith <psmith@apache.org>
 * @author Scott Deboy <sdeboy@apache.org>
 *
 */
public final class LineIconFactory {
  /**
   *
   */
  private LineIconFactory() {
  }

  public static final Icon createExpandIcon() {
      int size = 8;
      int xOffSet = 0;
      int yOffSet = 0;
    try {
      GraphicsEnvironment environment =
        GraphicsEnvironment.getLocalGraphicsEnvironment();
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
      Graphics2D g2D =
        environment.createGraphics(
          image);
        g2D.setBackground(new Color(0,0,0,0));
        g2D.clearRect(0,0,size,size);
        g2D.setStroke(new BasicStroke(1.5f));
        g2D.setRenderingHint(
          RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2D.setColor(Color.black);
        g2D.drawLine(
           xOffSet, (size / 2) + yOffSet, size - xOffSet,
           (size / 2) + yOffSet);

        g2D.drawLine(
           xOffSet + (size/2), yOffSet, xOffSet + (size/2),
           (size) + yOffSet);
        
        return new ImageIcon(image);
    } catch (Exception e) {
      LogLog.error("failed to create a Expand icon", e);
    }

    return null;
  }
  public static final Icon createCollapseIcon() {
      int size = 8;
      int xOffSet = 0;
      int yOffSet = 0;
    try {
      GraphicsEnvironment environment =
        GraphicsEnvironment.getLocalGraphicsEnvironment();
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
      Graphics2D g2D =
        environment.createGraphics(
          image);
        g2D.setBackground(new Color(0,0,0,0));
        g2D.clearRect(0,0,size,size);
        g2D.setStroke(new BasicStroke(1.5f));
        g2D.setRenderingHint(
          RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2D.setColor(Color.black);
        g2D.drawLine(
           xOffSet, (size / 2) + yOffSet, size - xOffSet,
           (size / 2) + yOffSet);
        
        return new ImageIcon(image);
    } catch (Exception e) {
      LogLog.error("failed to create a Collapse icon", e);
    }

    return null;
  }

  public static final Icon createCloseIcon() {
    return new CloseIcon(8, 0, 0);
  }

  public static final Icon createBlankIcon() {
    return new BlankIcon(16);
  }

  /**
     * A nice and simple 'X' style icon that is used to indicate a 'close' operation.
     *
     * @author Scott Deboy <sdeboy@apache.org>
     *
     */
  private static class BlankIcon implements Icon {
    int size;

    public BlankIcon(int size) {
      this.size = size;
    }

    public int getIconHeight() {
      return size;
    }

    public int getIconWidth() {
      return size;
    }

    public void paintIcon(Component c, Graphics g, int x, int y) {
    }
  }

  /**
     * A nice and simple 'X' style icon that is used to indicate a 'close' operation.
     *
     * @author Scott Deboy <sdeboy@apache.org>
     *
     */
  private static class CloseIcon implements Icon {
    int size;
    int xOffSet;
    int yOffSet;

    public CloseIcon(int size, int xOffSet, int yOffSet) {
      this.size = size;
      this.xOffSet = xOffSet;
      this.yOffSet = yOffSet;
    }

    public int getIconHeight() {
      return size;
    }

    public int getIconWidth() {
      return size;
    }

    public void paintIcon(Component c, Graphics g, int x, int y) {
      Graphics2D g2D = (Graphics2D) g;
      g2D.setStroke(new BasicStroke(1.5f));
      g2D.setRenderingHint(
        RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
      g2D.setColor(Color.black);
      g2D.drawLine(
        x + xOffSet, y + yOffSet, x + size + xOffSet, y + size + yOffSet);
      g2D.drawLine(
        x + xOffSet, y + size + yOffSet, x + size + xOffSet, y + yOffSet);
    }
  }
}
