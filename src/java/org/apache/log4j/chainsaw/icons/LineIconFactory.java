/*
 * ============================================================================
 *                   The Apache Software License, Version 1.1
 * ============================================================================
 *
 *    Copyright (C) 1999 The Apache Software Foundation. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modifica-
 * tion, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of  source code must  retain the above copyright  notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. The end-user documentation included with the redistribution, if any, must
 *    include  the following  acknowledgment:  "This product includes  software
 *    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
 *    Alternately, this  acknowledgment may  appear in the software itself,  if
 *    and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "log4j" and  "Apache Software Foundation"  must not be used to
 *    endorse  or promote  products derived  from this  software without  prior
 *    written permission. For written permission, please contact
 *    apache@apache.org.
 *
 * 5. Products  derived from this software may not  be called "Apache", nor may
 *    "Apache" appear  in their name,  without prior written permission  of the
 *    Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 * APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 * DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 * ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 * (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * This software  consists of voluntary contributions made  by many individuals
 * on  behalf of the Apache Software  Foundation.  For more  information on the
 * Apache Software Foundation, please see <http://www.apache.org/>.
 *
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
