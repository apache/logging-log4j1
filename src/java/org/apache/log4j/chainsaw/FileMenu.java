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
package org.apache.log4j.chainsaw;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import org.apache.log4j.chainsaw.icons.ChainsawIcons;


/**
 * The complete File Menu for the main GUI, containing
 * the Load, Save, Close Welcome Tab, and Exit actions
 *
 * @author Paul Smith <psmith@apache.org>
 * @author Scott Deboy <sdeboy@apache.org>
 */
class FileMenu extends JMenu {
  private Action exitAction;
  private Action loadLog4JAction;
  private Action loadUtilLoggingAction;
  private Action remoteLog4JAction;
  private Action remoteUtilLoggingAction;
  private Action saveAction;

  public FileMenu(final LogUI logUI) {
    super("File");
    setMnemonic(KeyEvent.VK_F);

    loadLog4JAction =
      new FileLoadAction(
        logUI, "org.apache.log4j.xml.XMLDecoder", "Load Log4J File...", false);

      loadLog4JAction.putValue(
        Action.ACCELERATOR_KEY,
        KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
      loadLog4JAction.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_L));
      loadLog4JAction.putValue(Action.SHORT_DESCRIPTION, "Loads an XML event file");
      loadLog4JAction.putValue(Action.SMALL_ICON, new ImageIcon(ChainsawIcons.FILE_OPEN));

    loadUtilLoggingAction =
      new FileLoadAction(
        logUI, "org.apache.log4j.xml.UtilLoggingXMLDecoder",
        "Load Java Util File...", false);

    remoteLog4JAction =
      new FileLoadAction(
        logUI, "org.apache.log4j.xml.XMLDecoder", "Load Remote Log4J File...",
        true);
    remoteUtilLoggingAction =
      new FileLoadAction(
        logUI, "org.apache.log4j.xml.UtilLoggingXMLDecoder",
        "Load Remote Java Util File...", true);

    saveAction = new FileSaveAction(logUI);

    JMenuItem loadLog4JFile = new JMenuItem(loadLog4JAction);
    JMenuItem loadUtilLoggingFile = new JMenuItem(loadUtilLoggingAction);
    JMenuItem remoteLog4JFile = new JMenuItem(remoteLog4JAction);
    JMenuItem remoteUtilLoggingFile = new JMenuItem(remoteUtilLoggingAction);
    JMenuItem saveFile = new JMenuItem(saveAction);

    exitAction =
      new AbstractAction() {
          public void actionPerformed(ActionEvent e) {
            logUI.exit();
          }
        };

    exitAction.putValue(
      Action.ACCELERATOR_KEY,
      KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.ALT_MASK));
    exitAction.putValue(Action.SHORT_DESCRIPTION, "Exits the Application");
    exitAction.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_X));
    exitAction.putValue(Action.NAME, "Exit");

    JMenuItem menuItemExit = new JMenuItem(exitAction);

    add(loadLog4JFile);
    add(loadUtilLoggingFile);
    addSeparator();
    add(remoteLog4JFile);
    add(remoteUtilLoggingFile);
    addSeparator();
    add(saveFile);
    addSeparator();
    add(menuItemExit);
  }

  Action getLog4JFileOpenAction() {
    return loadLog4JAction;
  }

  Action getUtilLoggingJFileOpenAction() {
    return loadUtilLoggingAction;
  }

  Action getFileSaveAction() {
    return saveAction;
  }

  Action getExitAction() {
    return exitAction;
  }
}
