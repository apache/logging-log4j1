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

import org.apache.log4j.Decoder;
import org.apache.log4j.chainsaw.icons.ChainsawIcons;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import java.io.File;
import java.io.IOException;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;


/**
 * Allows the user to specify a particular file to open
 * and import the events into a new tab.
 *
 * @author Paul Smith <psmith@apache.org>
 * @author Scott Deboy <sdeboy@apache.org>
 * 
 */
class FileLoadAction extends AbstractAction {
  /**
   * This action must have a reference to a LogUI
   * window so that it can append the events it loads
   *
   */
  Decoder decoder = null;
  private LogUI parent;
  private JFileChooser chooser = null;

  public FileLoadAction(LogUI parent, String decoder, String title) {
    super(title);


    try {
      Class c = Class.forName(decoder);
      Object o = c.newInstance();

      if (o instanceof Decoder) {
        this.decoder = (Decoder) o;
      }
    } catch (ClassNotFoundException cnfe) {
    } catch (IllegalAccessException iae) {
    } catch (InstantiationException ie) {
    }

    putValue(
      Action.ACCELERATOR_KEY,
      KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
    putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_L));
    putValue(Action.SHORT_DESCRIPTION, "Loads an XML event file");
    putValue(Action.SMALL_ICON, new ImageIcon(ChainsawIcons.FILE_OPEN));
    this.parent = parent;
  }

  /*
   * When the user chooses the Load action,
   * a File chooser is presented to allow them to
   * find an XML file to load events from.
   *
   * Any events decoded from this file are added to
   * one of the tabs.
   * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
   */
  public void actionPerformed(ActionEvent e) {
    if( chooser == null ){
      chooser = new JFileChooser();
    }

    chooser.setDialogTitle("Load Events from XML file...");

    chooser.setAcceptAllFileFilterUsed(true);

    chooser.setFileFilter(
      new FileFilter() {
        public boolean accept(File f) {
          return f.getName().toLowerCase().endsWith(".xml");
        }

        public String getDescription() {
          return "XML files (*.xml)";
        }
      });

    chooser.showOpenDialog(parent);

    File selectedFile = chooser.getSelectedFile();

    if (selectedFile != null) {
      Map additionalProperties = new HashMap();
      additionalProperties.put(
        ChainsawConstants.LOG4J_MACHINE_KEY,
        "localhost:" + selectedFile.getName());
      decoder.setAdditionalProperties(additionalProperties);

      try {
        Vector events = decoder.decode(selectedFile);
        parent.handler.appendBatch(events);
      } catch (IOException e1) {
        // TODO Handle the error with a nice msg
        e1.printStackTrace();
      }
    }
  }
}
