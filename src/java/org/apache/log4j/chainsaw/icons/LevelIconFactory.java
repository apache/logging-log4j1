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

import org.apache.log4j.Level;

import java.awt.Image;

import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.UIManager;


/**
 */
public class LevelIconFactory {
  private static final LevelIconFactory instance = new LevelIconFactory();
  private final Map iconMap = new HashMap();

  private LevelIconFactory() {
    String[] iconFileNames =
      new String[] { "Warn.gif", "Inform.gif", "Error.gif" };
    String[] iconLabels = new String[] { "WARN", "INFO", "ERROR" };
    Level[] levels = new Level[] { Level.WARN, Level.INFO, Level.ERROR };

    for (int i = 0; i < iconLabels.length; i++) {
      final ImageIcon icon =
        new ImageIcon(
          UIManager.getLookAndFeel().getClass().getResource(
            "icons/" + iconFileNames[i]));
      double scalex = .5;
      double scaley = .5;
      final int newWidth = (int) (scalex * icon.getIconWidth());
      final int newHeight = (int) (scaley * icon.getIconHeight());
      Image iconImage =
        icon.getImage().getScaledInstance(
          newWidth, newHeight, Image.SCALE_SMOOTH);
      iconMap.put(iconLabels[i], new ImageIcon(iconImage));
    }

    iconMap.put("DEBUG", ChainsawIcons.ICON_DEBUG);
  }

  public static final LevelIconFactory getInstance() {
    return instance;
  }

  public Map getLevelToIconMap() {
    return iconMap;
  }
}
