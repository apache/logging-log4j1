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

package org.apache.log4j.chainsaw;

import org.apache.log4j.chainsaw.icons.ChainsawIcons;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.xml.XMLLayout;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.Iterator;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;


/**
 * Allows the user to specify a particular file to which the current tab's
 * displayed events will be saved.
 *
 * @author Scott Deboy <sdeboy@apache.org>
 * @author Paul Smith <psmith@apache.org>
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
        Vector v = ((EventContainer)parent.getCurrentLogPanel().getModel()).getAllEvents();

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
