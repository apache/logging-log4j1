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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;


/**
 * 
 * Ensures that a specific popupMenu is displayed when the relevant
 * mouse events are trapped.
 * 
 * @author Scott Deboy <sdeboy@apache.org>
 * @author Paul Smith <psmith@apache.org>
 * 
 */
public class PopupListener extends MouseAdapter {
  final JPopupMenu popupMenu;

  public PopupListener(JPopupMenu popupMenu) {
    this.popupMenu = popupMenu;
  }

  public void mousePressed(MouseEvent e) {
    checkPopup(e);
  }

  public void mouseReleased(MouseEvent e) {
    checkPopup(e);
  }
  
  public void mouseClicked(MouseEvent e)
  {
      checkPopup(e);
  }

  private void checkPopup(MouseEvent e) {
    if (e.isPopupTrigger()) {
      popupMenu.show(e.getComponent(), e.getX(), e.getY());
    }
  }
}
