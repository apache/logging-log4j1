/*
 * Created on 2/09/2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.log4j.chainsaw;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.Icon;


/**
   * A nice and simple 'X' style icon that is used to indicate a 'close' operation.
   *
   * @author Scott Deboy <sdeboy@apache.org>
   *
   */
  class CloseIcon implements Icon {
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