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
import javax.swing.JSeparator;
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
  private Action saveAction;
  private Action saveSettingsAction;
  
  public FileMenu(final LogUI logUI) {
    super("File");
    setMnemonic(KeyEvent.VK_F);
    
    loadLog4JAction = new FileLoadAction(logUI, "org.apache.log4j.xml.XMLDecoder", "Load Log4J File...");
    loadUtilLoggingAction = new FileLoadAction(logUI, "org.apache.log4j.xml.UtilLoggingXMLDecoder", "Load Java Util File...");
    saveAction = new FileSaveAction(logUI);
    
    JMenuItem loadLog4JFile = new JMenuItem(loadLog4JAction);
    JMenuItem loadUtilLoggingFile = new JMenuItem(loadUtilLoggingAction);
    JMenuItem saveFile = new JMenuItem(saveAction);

    exitAction = new AbstractAction() {

      public void actionPerformed(ActionEvent e) {
				logUI.exit();
      }
    };

    exitAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.ALT_MASK));
    exitAction.putValue(Action.SHORT_DESCRIPTION, "Exits the Application");
    exitAction.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_X));
    exitAction.putValue(Action.NAME, "Exit");
    
    JMenuItem menuItemExit = new JMenuItem(exitAction);
    
    add(loadLog4JFile);
    add(loadUtilLoggingFile);
    add(saveFile);
    add(new JSeparator());
    add(menuItemExit);
  }
  
  Action getLog4JFileOpenAction() { return loadLog4JAction;}
  Action getUtilLoggingJFileOpenAction() { return loadUtilLoggingAction;}
  Action getFileSaveAction() { return saveAction;}
  Action getExitAction() { return exitAction;}
  
}
