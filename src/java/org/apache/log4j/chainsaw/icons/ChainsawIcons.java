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
  public static final URL UP =
          ChainsawIcons.class.getClassLoader().getResource(
            BASE_ICON_PATH + "Up16.gif");
  public static final URL DOWN =
      ChainsawIcons.class.getClassLoader().getResource(
        BASE_ICON_PATH + "Down16.gif");
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
  public static final Icon ICON_UP = new ImageIcon(UP);
  public static final Icon ICON_DOWN = new ImageIcon(DOWN);
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

  public static final URL ANIM_NET_CONNECT =
    ChainsawIcons.class.getClassLoader().getResource(
      BASE_ICON_PATH + "channelexplorer_satellite.gif");
  public static final URL ANIM_RADIO_TOWER = ANIM_NET_CONNECT;
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

  public static final ImageIcon FOCUS_ON_ICON = new ImageIcon(ChainsawIcons.class.getClassLoader().getResource(
      BASE_ICON_PATH + "ZoomIn24.gif"));
  public static final ImageIcon IGNORE_ICON = new ImageIcon(ChainsawIcons.class.getClassLoader().getResource(
      BASE_ICON_PATH + "ZoomOut24.gif"));  
  
  
  public static final URL SERVER =
    ChainsawIcons.class.getClassLoader().getResource(
      BASE_ICON_PATH + "Server16.gif");
  
  public static final ImageIcon ICON_SERVER =  new ImageIcon(SERVER);
  
  private ChainsawIcons() {
  }
}
