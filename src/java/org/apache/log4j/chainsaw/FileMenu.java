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
package org.apache.log4j.chainsaw;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;


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
  private Action saveSettingsAction;

  public FileMenu(final LogUI logUI) {
    super("File");
    setMnemonic(KeyEvent.VK_F);

    loadLog4JAction =
      new FileLoadAction(
        logUI, "org.apache.log4j.xml.XMLDecoder", "Load Log4J File...", false);
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
