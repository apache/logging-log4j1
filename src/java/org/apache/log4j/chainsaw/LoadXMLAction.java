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

import org.apache.log4j.Logger;

import java.awt.event.ActionEvent;

import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;


/**
 * Encapsulates the action to load an XML file.
 *
 * @author <a href="mailto:oliver@puppycrawl.com">Oliver Burn</a>
 * @version 1.0
 */
class LoadXMLAction extends AbstractAction {
  /** use to log messages **/
  private static final Logger LOG = Logger.getLogger(LoadXMLAction.class);

  /** the parent frame **/
  private final JFrame mParent;

  /**
  * the file chooser - configured to allow only the selection of a
  * single file.
  */
  private final JFileChooser mChooser = new JFileChooser();

  /** the content handler **/
  private final XMLFileHandler mHandler;

  {
    mChooser.setMultiSelectionEnabled(false);
    mChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
  }

  /**
  * Creates a new <code>LoadXMLAction</code> instance.
  *
  * @param aParent the parent frame
  * @param eventSink the eventSink to add events to
  */
  LoadXMLAction(JFrame aParent, EventDetailSink eventSink) {
    mParent = aParent;
    mHandler = new XMLFileHandler(eventSink);
  }

  /**
  * Prompts the user for a file to load events from.
  * @param aIgnore an <code>ActionEvent</code> value
  */
  public void actionPerformed(ActionEvent aIgnore) {
    LOG.info("load file called");

    if (mChooser.showOpenDialog(mParent) == JFileChooser.APPROVE_OPTION) {
      LOG.info("Need to load a file");

      final File chosen = mChooser.getSelectedFile();

      try {
        final int num = mHandler.loadFile(chosen);
        JOptionPane.showMessageDialog(
          mParent, "Loaded " + num + " events.", "CHAINSAW",
          JOptionPane.INFORMATION_MESSAGE);
      } catch (Exception e) {
        LOG.warn("caught an exception loading the file", e);
        JOptionPane.showMessageDialog(
          mParent, "Error parsing file - " + e.getMessage(), "CHAINSAW",
          JOptionPane.ERROR_MESSAGE);
      }
    }
  }
}
