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

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.KeyStroke;

import org.apache.log4j.chainsaw.icons.ChainsawIcons;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.xml.XMLLayout;


/**
 * Allows the user to specify a particular file to which the current tab's
 * displayed events will be saved.
 *
 * @author Scott Deboy <sdeboy@apache.org>
 * @author Paul Smith <psmith@apache.org>
 * @author Stephen Pain
 */
class FileSaveAction extends AbstractAction {
  private LogUI parent;
  private JFileChooser chooser = null;

  /**
   * This action must have a reference to a LogUI
   * in order to retrieve events to save
   *
   */
  public FileSaveAction(LogUI parent) {
    super("Save as...");

    putValue(
      Action.ACCELERATOR_KEY,
      KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
    putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_S));
    putValue(
      Action.SHORT_DESCRIPTION, "Saves displayed events for the current tab");
    putValue(Action.SMALL_ICON, new ImageIcon(ChainsawIcons.FILE_SAVE_AS));
    this.parent = parent;
  }
  
  /*
   * When the user chooses the Save action,
   * a File chooser is presented to allow them to
   * find an XML file to save events to.
   *
   * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
   */
  public void actionPerformed(ActionEvent e) {

    if( chooser == null ){
      chooser = new JFileChooser();
    }
    
    chooser.setAcceptAllFileFilterUsed(true);
    chooser.setDialogTitle("Save Events to XML file...");
    chooser.showSaveDialog(parent);

    File selectedFile = chooser.getSelectedFile();
    XMLLayout layout = new XMLLayout();
    LoggingEvent event = null;
    PrintWriter out = null;

    if (selectedFile != null) {
      try {
        List v = parent.getCurrentLogPanel().getFilteredEvents();

        if (((v != null) && (v.size() == 0)) || (v == null)) {
          //no events to save
          return;
        }

        Iterator iter = v.iterator();

        out =
          new PrintWriter(new BufferedWriter(new FileWriter(selectedFile)));

        while (iter.hasNext()) {
          event = (LoggingEvent) iter.next();
          layout.setLocationInfo(event.getThrowableInformation() != null);
          out.write(layout.format(event));
        }
      } catch (IOException ioe) {
        ioe.printStackTrace();
      } finally {
        if (out != null) {
          out.flush();
          out.close();
        }
      }
    }
  }
}
