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

/*
 * @author Paul Smith <psmith@apache.org>
 *
 */
package org.apache.log4j.chainsaw.icons;

import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;


/**
 * @author Paul Smith <psmith@apache.org>
 *
 */
public class ChainsawIcons {
  private static final String BASE_ICON_PATH =
    "org/apache/log4j/chainsaw/icons/";
  public static final URL FILE_OPEN =
    ChainsawIcons.class.getClassLoader().getResource(
      BASE_ICON_PATH + "Open16.gif");
  public static final URL FILE_SAVE_AS =
    ChainsawIcons.class.getClassLoader().getResource(
      BASE_ICON_PATH + "SaveAs16.gif");
  public static final URL PAUSE =
    ChainsawIcons.class.getClassLoader().getResource(
      BASE_ICON_PATH + "Pause16.gif");
  public static final URL REFRESH =
    ChainsawIcons.class.getClassLoader().getResource(
      BASE_ICON_PATH + "Refresh16.gif");
  public static final URL DELETE =
    ChainsawIcons.class.getClassLoader().getResource(
      BASE_ICON_PATH + "Delete16.gif");
  public static final URL INFO =
    ChainsawIcons.class.getClassLoader().getResource(
      BASE_ICON_PATH + "Information16.gif");
  public static final URL FIND =
    ChainsawIcons.class.getClassLoader().getResource(
      BASE_ICON_PATH + "Find16.gif");
  public static final URL ABOUT =
    ChainsawIcons.class.getClassLoader().getResource(
      BASE_ICON_PATH + "About16.gif");
  public static final URL SCROLL_TO_BOTTOM =
    ChainsawIcons.class.getClassLoader().getResource(
      BASE_ICON_PATH + "RowInsertAfter16.gif");
  public static final URL TOOL_TIP =
    ChainsawIcons.class.getClassLoader().getResource(
      BASE_ICON_PATH + "TipOfTheDay16.gif");
  public static final URL UNDOCK =
    ChainsawIcons.class.getClassLoader().getResource(
      BASE_ICON_PATH + "Export16.gif");
  public static final URL DOCK =
    ChainsawIcons.class.getClassLoader().getResource(
      BASE_ICON_PATH + "Import16.gif");
  public static final URL PREFERENCES =
    ChainsawIcons.class.getClassLoader().getResource(
      BASE_ICON_PATH + "Preferences16.gif");
  public static final URL DEBUG =
    ChainsawIcons.class.getClassLoader().getResource(
      BASE_ICON_PATH + "Bean16.gif");
  public static final URL HELP =
    ChainsawIcons.class.getClassLoader().getResource(
      BASE_ICON_PATH + "Help16.gif");
  public static final Icon ICON_HELP = new ImageIcon(HELP);
  public static final Icon ICON_PREFERENCES = new ImageIcon(PREFERENCES);
  public static final Icon ICON_DOCK = new ImageIcon(DOCK);
  public static final URL COPY =
    ChainsawIcons.class.getClassLoader().getResource(
      BASE_ICON_PATH + "Copy16.gif");
  public static final URL CUT =
    ChainsawIcons.class.getClassLoader().getResource(
      BASE_ICON_PATH + "Cut16.gif");
  public static final Icon ICON_COPY = new ImageIcon(COPY);
  public static final Icon ICON_CUT = new ImageIcon(CUT);
  public static final Icon ICON_UNDOCK = new ImageIcon(UNDOCK);
  public static final Icon ICON_DEBUG = new ImageIcon(DEBUG);
  public static final URL WINDOW_ICON =
    ChainsawIcons.class.getClassLoader().getResource(
      BASE_ICON_PATH + "Zoom16.gif");
  public static final URL UNDOCKED_ICON =
    ChainsawIcons.class.getClassLoader().getResource(
      BASE_ICON_PATH + "ZoomIn16.gif");
  public static final ImageIcon ICON_LOG4J =
    new ImageIcon(
      ChainsawIcons.class.getClassLoader().getResource(
        "org/apache/log4j/chainsaw/logo.jpg"));

  //	TODO give appropriate Credit, http://members.aol.com/gx0vzs/radiotower.html
  //  public static final URL ANIM_RADIO_TOWER = ChainsawIcons.class.getClassLoader().getResource(BASE_ICON_PATH + "anim_radiotower.gif");
  public static final URL ANIM_RADIO_TOWER = TOOL_TIP;
  public static final URL ANIM_NET_CONNECT =
    ChainsawIcons.class.getClassLoader().getResource(
      BASE_ICON_PATH + "Host16.gif");
  public static final URL ICON_ACTIVE_RECEIVER =
    ChainsawIcons.class.getClassLoader().getResource(
      BASE_ICON_PATH + "Play16.gif");
  public static final URL ICON_HOME =
    ChainsawIcons.class.getClassLoader().getResource(
      BASE_ICON_PATH + "Home16.gif");
  public static final URL ICON_BACK =
    ChainsawIcons.class.getClassLoader().getResource(
      BASE_ICON_PATH + "Back16.gif");
  public static final URL ICON_RESTART =
    ChainsawIcons.class.getClassLoader().getResource(
      BASE_ICON_PATH + "Redo16.gif");
  public static final URL ICON_STOP_RECEIVER =
    ChainsawIcons.class.getClassLoader().getResource(
      BASE_ICON_PATH + "Stop16.gif");
  public static final URL ICON_NEW_RECEIVER =
    ChainsawIcons.class.getClassLoader().getResource(
      BASE_ICON_PATH + "New16.gif");
  public static final URL ICON_EDIT_RECEIVER =
    ChainsawIcons.class.getClassLoader().getResource(
      BASE_ICON_PATH + "Edit16.gif");
  public static final URL ICON_RESUME_RECEIVER = ICON_ACTIVE_RECEIVER;
  public static final URL ICON_INACTIVE_RECEIVER = PAUSE;
  public static final URL ICON_COLLAPSE =
    ChainsawIcons.class.getClassLoader().getResource(
      BASE_ICON_PATH + "ZoomOut16.gif");

  private ChainsawIcons() {
  }
}
